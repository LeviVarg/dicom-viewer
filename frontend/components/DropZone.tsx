'use client';
import { useDropzone } from 'react-dropzone';
import { Card, CardContent } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Upload } from 'lucide-react';

export function DicomDropzone() {
  const onDrop = (acceptedFiles: any) => {
    console.log(acceptedFiles);
  };

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    multiple: true,
    accept: {
      'application/dicom': ['.dcm'],
    }
  });

  return (
    <Card
      {...getRootProps()}
      className={`border-2 border-dashed transition-colors 
        ${isDragActive ? 'border-indigo-600 bg-indigo-50' : 'border-gray-300'}
      `}
    >
      <CardContent className="flex flex-col items-center justify-center p-12">
        <Upload className="w-8 h-8 text-gray-400 mb-2" />

        <Input {...getInputProps()} className="hidden" />

        {isDragActive ? (
          <p className="font-semibold">Drop the DICOM files here...</p>
        ) : (
          <p className="text-sm text-center text-muted-foreground">
            Drag 'n' drop DICOM files here, or click to select files
          </p>
        )}
      </CardContent>
    </Card>
  );
}
