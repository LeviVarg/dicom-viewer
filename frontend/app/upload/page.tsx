'use client';

import { DicomDropzone } from "@/components/DropZone";

export default function Upload() {
  return (
    <div className="w-screen h-screen flex items-center justify-center gap-4">
      <DicomDropzone></DicomDropzone>
    </div>
  )
}
