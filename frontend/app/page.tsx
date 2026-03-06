"use client";

import Link from "next/link";
import { useTheme } from "@/lib/theme-context";

export default function Home() {
  const { theme } = useTheme();

  return (
    <div
      className={`w-screen h-screen flex items-center justify-center gap-4 ${theme.colors.background}`}
    >
      <Link href="/upload">
        <p
          className={`px-6 py-4
          ${theme.colors.button.background} ${theme.colors.button.hover}
          ${theme.colors.button.text}
          rounded-2xl transition-colors cursor-pointer`}
        >
          Upload
        </p>
      </Link>
      <Link href="/patients">
        <p
          className={`px-6 py-4
          ${theme.colors.button.background} ${theme.colors.button.hover}
          ${theme.colors.button.text}
          rounded-2xl transition-colors cursor-pointer`}
        >
          Patients
        </p>
      </Link>
    </div>
  );
}
