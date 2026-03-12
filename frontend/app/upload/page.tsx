"use client";

import { DicomDropzone } from "@/components/DicomDropZone";
import { useTheme } from "@/lib/theme-context";
import { Header } from "@/components/Header";

export default function Upload() {
  const { theme } = useTheme();

  return (
    <div className={`min-h-screen flex flex-col ${theme.colors.background}`}>
      <Header />
      <main className="flex-1 flex items-center justify-center">
        <DicomDropzone />
      </main>
    </div>
  );
}
