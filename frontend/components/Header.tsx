"use client";

import Link from "next/link";
import { useTheme } from "@/lib/theme-context";

export function Header() {
  const { theme } = useTheme();

  return (
    <header
      className={`w-full px-8 py-4 flex items-center justify-between border-b ${theme.colors.border} ${theme.colors.background}`}
    >
      <Link
        href="/"
        className={`text-lg font-semibold tracking-tight ${theme.colors.text.primary}`}
      >
        DICOM Viewer
      </Link>
      <nav className="flex items-center gap-2">
        <Link href="/upload">
          <span
            className={`px-4 py-2 rounded-xl text-sm font-medium transition-colors cursor-pointer
              ${theme.colors.button.background} ${theme.colors.button.hover} ${theme.colors.button.text}`}
          >
            Upload
          </span>
        </Link>
        <Link href="/patients">
          <span
            className={`px-4 py-2 rounded-xl text-sm font-medium transition-colors cursor-pointer
              ${theme.colors.button.background} ${theme.colors.button.hover} ${theme.colors.button.text}`}
          >
            Patients
          </span>
        </Link>
      </nav>
    </header>
  );
}
