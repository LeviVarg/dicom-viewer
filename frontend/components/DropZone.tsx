'use client';
import { useDropzone } from 'react-dropzone';
import { Card, CardContent } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Upload } from 'lucide-react';
import { useCallback, useState } from 'react';

interface DicomDropzoneProps {
  onFilesAccepted: (files: File[]) => void;
}

export function DicomDropzone({ onFilesAccepted }: DicomDropzoneProps) {
  const [files, setFiles] = useState<File[]>();




  const onDrop = useCallback((acceptedFiles: File[]) => {
    setFiles(acceptedFiles);

    onFilesAccepted(acceptedFiles);
  }, [onFilesAccepted]);

  const { getRootProps, getInputProps, isDragActive, open } = useDropzone({
    onDrop,
    multiple: true,
    accept: {
      'application/dicom': ['.dcm'],
    },
  });

  return (
    <Card
      {...getRootProps()}
      className={`border-2 border-dashed transition-colors w-xl h-2xl 
        ${isDragActive ? 'border-indigo-600 bg-indigo-50' : 'border-gray-300'}
      `}
    >
      <CardContent className="flex flex-col items-center justify-center p-12">
        <Upload className="w-8 h-8 text-gray-400 mb-2" />

        <Input {...getInputProps()} type="file" id='dicom files' />

        {isDragActive ? (
          <p className="font-semibold text-lg text-indigo-700">Drop the DICOM files here...</p>
        ) : (
          <div className="text-center">
            <p className="text-sm font-semibold mb-1">
              Drag 'n' drop files here
            </p>
            <p className="text-xs text-muted-foreground">
              or click to select DICOM files (.dcm)
            </p>
            <div className="mt-3 inline-block px-4 py-2 
                bg-gray-100 border rounded-md 
                text-sm font-medium
                hover:bg-gray-200 transition-colors"

              onClick={open}>
              Browse Files
            </div>
          </div>
        )}
      </CardContent>
    </Card>
  );
}
