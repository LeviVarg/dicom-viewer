'use client';
import { useDropzone, FileRejection } from 'react-dropzone';
import { Card, CardContent } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Upload } from 'lucide-react';
import { useCallback, useState } from 'react';
import { toast } from 'sonner';
import { fileUpload } from '@/app/upload/fileupload.actions';

// interface DicomDropzoneProps {
//   onFilesAccepted: (files: File[]) => void;
// }

export function DicomDropzone() {
  const [files, setFiles] = useState<File[]>([]);




  const onDrop = useCallback((acceptedFiles: File[], fileRejections: FileRejection[]) => {
    if (acceptedFiles.length > 0) {
      setFiles((prev) => ([...prev, ...acceptedFiles]));

    }

    if (fileRejections.length > 0) {
      const rejectedNames = fileRejections.map(rejection => rejection.file.name).join(', ');

      toast.error(`Invalid File Type Detected`, {
        description: `The following files were rejected: ${rejectedNames}. Only .dcm files are accepted.`,
        duration: 5000,
      });
    }
  }, []);

  const { getRootProps, getInputProps, isDragActive, open } = useDropzone({
    onDrop,
    multiple: true,
    accept: {
      'application/dicom': ['.dcm'],
    },
  });

  async function uploadAction() {
    if (!(files.length > 0)) {
      toast.error(`No files to upload`, { duration: 5000 })
    }

    const data = await fileUpload(files);

    if (!data.success) {
      toast.error(`Error uploading files: ${data.message}`, { duration: 5000 })
    } else {
      toast.success(`Succes uploading: ${data.message}`, { duration: 5000 })
      console.log(data)
    }

    setFiles([]);
  }


  return (
    <Card
      {...getRootProps({ className: 'dropzone' })}
      className={`
        border-2 border-dashed transition-colors
        ${isDragActive ? 'border-indigo-600 bg-indigo-50' : 'border-gray-300 hover:border-gray-400'}
        ${files.length > 0 ? 'min-h-[100px]' : ''}
      `}
    >
      <CardContent className="flex flex-col items-center justify-center p-6 w-4xl h-4xl">
        <Input {...getInputProps()} />

        {files.length > 0 ? (
          <div className="w-full">
            <h4 className="text-md font-semibold mb-2 text-center">Selected DICOM Files ({files.length}):</h4>
            <ul className="list-inside space-y-1 text-left px-4 max-h-40 overflow-y-auto list-none">
              {
                files.map(file => (
                  <li key={file.name} className="text-sm truncate text-gray-700">
                    {file.name} - {Math.round(file.size / 1024)} KB
                  </li>
                ))
              }
            </ul>
          </div>
        ) : isDragActive ? (
          <>
            <Upload className="w-8 h-8 text-indigo-500 mb-4" />
            <p className="font-semibold text-lg text-indigo-700">Drop the DICOM files here...</p>
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
        <div
          className="mt-3 inline-block px-4 py-2 bg-gray-100 border rounded-md text-sm font-medium hover:bg-gray-200 transition-colors cursor-pointer"
          onClick={open}
        >
          Browse Files
        </div>

      </CardContent>
    </Card>);
}
