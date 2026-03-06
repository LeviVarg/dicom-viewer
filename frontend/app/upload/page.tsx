"use client";

import { DicomDropzone } from "@/components/DicomDropZone";
import { useTheme } from "@/lib/theme-context";

export default function Upload() {
  const { theme } = useTheme();

  return (
    <div
      className={`w-screen h-screen flex items-center justify-center gap-4 ${theme.colors.background}`}
    >
      <DicomDropzone></DicomDropzone>
    </div>
  );
}
