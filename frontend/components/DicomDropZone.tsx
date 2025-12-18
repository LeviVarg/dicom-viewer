"use client";
import { useDropzone, FileRejection } from "react-dropzone";
import { Card, CardContent } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Upload } from "lucide-react";
import { useCallback, useState } from "react";
import { toast } from "sonner";
import { fileUpload } from "@/app/upload/fileupload.actions";

// Maximum size per batch in bytes since nextjs only allows 1mb per request
const MAX_BATCH_SIZE = 900 * 1024; // 900KB

interface UploadProgress {
  currentBatch: number;
  totalBatches: number;
  isUploading: boolean;
}

/**
 * Splits an array of files into batches where each batch's total size is under the limit.
 * Each file is guaranteed to be in exactly one batch.
 * If a single file exceeds the limit, it gets its own batch.
 */
function splitFilesIntoBatches(files: File[], maxBatchSize: number): File[][] {
  const batches: File[][] = [];
  let currentBatch: File[] = [];
  let currentBatchSize = 0;

  for (const file of files) {
    if (
      currentBatchSize + file.size > maxBatchSize &&
      currentBatch.length > 0
    ) {
      batches.push(currentBatch);
      currentBatch = [];
      currentBatchSize = 0;
    }

    currentBatch.push(file);
    currentBatchSize += file.size;

    // If this single file exceeds the limit, push it as its own batch and reset
    if (file.size > maxBatchSize) {
      batches.push(currentBatch);
      currentBatch = [];
      currentBatchSize = 0;
    }
  }

  // last batch
  if (currentBatch.length > 0) {
    batches.push(currentBatch);
  }

  return batches;
}

export function DicomDropzone() {
  const [files, setFiles] = useState<File[]>([]);
  const [uploadProgress, setUploadProgress] = useState<UploadProgress>({
    currentBatch: 0,
    totalBatches: 0,
    isUploading: false,
  });

  const onDrop = useCallback(
    (acceptedFiles: File[], fileRejections: FileRejection[]) => {
      if (acceptedFiles.length > 0) {
        setFiles((prev) => [...prev, ...acceptedFiles]);
      }

      if (fileRejections.length > 0) {
        const rejectedNames = fileRejections
          .map((rejection) => rejection.file.name)
          .join(", ");

        toast.error(`Invalid File Type Detected`, {
          description: `The following files were rejected: ${rejectedNames}. Only .dcm files are accepted.`,
          duration: 5000,
        });
      }
    },
    [],
  );

  const { getRootProps, getInputProps, isDragActive, open } = useDropzone({
    onDrop,
    multiple: true,
    accept: {
      "application/dicom": [".dcm"],
    },
  });

  async function uploadAction() {
    if (!(files.length > 0)) {
      toast.error(`No files to upload`, { duration: 5000 });
      return;
    }

    // Split files into batches
    const batches = splitFilesIntoBatches(files, MAX_BATCH_SIZE);
    const totalBatches = batches.length;

    setUploadProgress({
      currentBatch: 0,
      totalBatches,
      isUploading: true,
    });

    let successCount = 0;
    let errorCount = 0;
    const errors: string[] = [];

    // Upload each batch 
    for (let i = 0; i < batches.length; i++) {
      const batch = batches[i];
      const batchNumber = i + 1;

      setUploadProgress({
        currentBatch: batchNumber,
        totalBatches,
        isUploading: true,
      });

      try {
        const data = await fileUpload(batch);
        console.log(`Batch ${batchNumber}/${totalBatches}:`, data);

        if (!data.success) {
          errorCount++;
          errors.push(`Batch ${batchNumber}: ${data.message || data.error}`);
        } else {
          successCount++;
        }
      } catch (error) {
        errorCount++;
        errors.push(`Batch ${batchNumber}: ${error}`);
        console.error(`Error uploading batch ${batchNumber}:`, error);
      }
    }

    setUploadProgress({
      currentBatch: 0,
      totalBatches: 0,
      isUploading: false,
    });

    if (errorCount === 0) {
      toast.success(
        `Successfully uploaded ${files.length} files in ${totalBatches} batch${totalBatches > 1 ? "es" : ""}`,
        { duration: 5000 },
      );
    } else if (successCount === 0) {
      toast.error(`Failed to upload all files`, {
        description: errors.join("\n"),
        duration: 5000,
      });
    } else {
      toast.warning(
        `Uploaded ${successCount}/${totalBatches} batches successfully`,
        {
          description: `${errorCount} batch${errorCount > 1 ? "es" : ""} failed: ${errors.join(", ")}`,
          duration: 5000,
        },
      );
    }

    setFiles([]);
  }

  // Calculate total size of selected files
  const totalSize = files.reduce((sum, file) => sum + file.size, 0);
  const totalSizeDisplay =
    totalSize > 1024 * 1024
      ? `${(totalSize / (1024 * 1024)).toFixed(2)} MB`
      : `${Math.round(totalSize / 1024)} KB`;

  // Calculate number of batches that would be created
  const estimatedBatches =
    files.length > 0 ? splitFilesIntoBatches(files, MAX_BATCH_SIZE).length : 0;

  return (
    <Card
      {...getRootProps({ className: "dropzone" })}
      className={`
        border-2 border-dashed transition-colors
        ${isDragActive ? "border-indigo-600 bg-indigo-50" : "border-gray-300 hover:border-gray-400"}
        ${files.length > 0 ? "min-h-[100px]" : ""}
      `}
    >
      <CardContent className="flex flex-col gap-3 items-center justify-center p-6 w-4xl h-4xl">
        <Input {...getInputProps()} />

        {files.length > 0 ? (
          <div className="w-full">
            <h4 className="text-md font-semibold mb-2 text-center">
              Selected DICOM Files ({files.length}):
            </h4>
            <p className="text-xs text-gray-500 text-center mb-2">
              Total size: {totalSizeDisplay} • Will upload in {estimatedBatches}{" "}
              batch{estimatedBatches > 1 ? "es" : ""}
            </p>
            <ul className="list-inside space-y-1 text-left px-4 max-h-40 overflow-y-auto list-none">
              {files.map((file) => (
                <li key={file.name} className="text-sm truncate text-gray-700">
                  {file.name} - {Math.round(file.size / 1024)} KB
                </li>
              ))}
            </ul>
          </div>
        ) : isDragActive ? (
          <>
            <Upload className="w-8 h-8 text-indigo-500 mb-4" />
            <p className="font-semibold text-lg text-indigo-700">
              Drop the DICOM files here...
            </p>
          </>
        ) : (
          <>
            <Upload className="w-8 h-8 text-gray-400 mb-4" />
            <div className="text-center">
              <p className="text-sm font-semibold mb-1">
                Drag 'n' drop files here
              </p>
              <p className="text-xs text-muted-foreground">
                or click to select DICOM files (.dcm)
              </p>
            </div>
          </>
        )}

        {/* Upload Progress */}
        {uploadProgress.isUploading && (
          <div className="w-full text-center">
            <div className="text-sm font-medium text-blue-600 mb-2">
              Uploading batch {uploadProgress.currentBatch} of{" "}
              {uploadProgress.totalBatches}...
            </div>
            <div className="w-full bg-gray-200 rounded-full h-2">
              <div
                className="bg-blue-500 h-2 rounded-full transition-all duration-300"
                style={{
                  width: `${(uploadProgress.currentBatch / uploadProgress.totalBatches) * 100}%`,
                }}
              />
            </div>
          </div>
        )}

        <div
          className="mt-3 inline-block px-4 py-2 bg-gray-100 border rounded-md text-sm font-medium hover:bg-gray-200 transition-colors cursor-pointer"
          onClick={(e) => {
            e.stopPropagation();
            open();
          }}
        >
          Browse Files
        </div>

        <div className="flex gap-5">
          <button
            onClick={(e) => {
              e.stopPropagation();
              uploadAction();
            }}
            disabled={uploadProgress.isUploading}
            className="px-4 py-2 bg-blue-400 rounded-md text-small hover:bg-blue-500 text-white disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {uploadProgress.isUploading ? "Uploading..." : "Upload"}
          </button>

          <button
            onClick={(e) => {
              e.stopPropagation();
              setFiles([]);
            }}
            disabled={uploadProgress.isUploading}
            className="px-4 py-2 bg-blue-400 rounded-md text-small hover:bg-blue-500 text-white disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Clear files
          </button>
        </div>
      </CardContent>
    </Card>
  );
}
