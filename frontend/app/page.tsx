"use client";

import Image from "next/image";
import Link from "next/link";
import { useTheme } from "@/lib/theme-context";
import { Header } from "@/components/Header";

export default function Home() {
  const { theme } = useTheme();

  const isLight = theme.mode === "light";

  return (
    <div className={`min-h-screen flex flex-col ${theme.colors.background}`}>
      <Header />

      {/* Hero */}
      <main className="flex-1 grid grid-cols-1 lg:grid-cols-2">
        {/* ── Left: MRI image panel ── */}
        <div className="relative overflow-hidden bg-black flex items-center justify-center min-h-[400px]">
          {/* The MRI image */}
          <Image
            src="/brain_mri.jpg"
            alt="Brain MRI scan"
            fill
            className="object-cover opacity-80 animate-flicker"
            priority
          />

          {/* Subtle dark vignette so edges fade to black */}
          <div
            className="absolute inset-0"
            style={{
              background:
                "radial-gradient(ellipse at center, transparent 40%, rgba(0,0,0,0.75) 100%)",
            }}
          />

          {/* Scan line */}
          <div className="absolute inset-0 overflow-hidden pointer-events-none">
            <div
              className="animate-scan absolute left-0 right-0 h-0.5"
              style={{
                background:
                  "linear-gradient(to right, transparent, rgba(255,255,255,0.18), rgba(255,255,255,0.35), rgba(255,255,255,0.18), transparent)",
                boxShadow: "0 0 12px 2px rgba(255,255,255,0.12)",
              }}
            />
          </div>

          {/* Corner annotation — mimics DICOM viewer metadata */}
          <div className="absolute top-4 left-4 font-mono text-xs text-white/40 space-y-0.5 select-none">
            <p>SERIES 01 / 24</p>
            <p>TR: 4500ms TE: 90ms</p>
            <p>1.5T AXIAL T2</p>
          </div>
          <div className="absolute bottom-4 right-4 font-mono text-xs text-white/40 select-none text-right">
            <p>W: 400 L: 200</p>
            <p>FOV: 220mm</p>
          </div>

          {/* Subtle horizontal CRT scanline texture */}
          <div
            className="absolute inset-0 pointer-events-none"
            style={{
              backgroundImage:
                "repeating-linear-gradient(to bottom, transparent, transparent 3px, rgba(0,0,0,0.08) 3px, rgba(0,0,0,0.08) 4px)",
            }}
          />
        </div>

        {/* ── Right: text content ── */}
        <div
          className={`flex flex-col justify-center px-12 py-16 lg:px-20 ${
            isLight ? "bg-white" : "bg-gray-900"
          }`}
        >
          {/* Eyebrow label */}
          <p
            className={`text-xs font-mono tracking-[0.2em] uppercase mb-6 ${theme.colors.text.muted}`}
          >
            Medical Imaging Platform
          </p>

          <h1
            className={`text-4xl lg:text-5xl font-bold leading-tight tracking-tight mb-6 ${theme.colors.text.primary}`}
          >
            Upload your DICOM files
            <br />
            <span className={theme.colors.text.secondary}>for inspection.</span>
          </h1>

          <p
            className={`text-base leading-relaxed mb-10 max-w-md ${theme.colors.text.secondary}`}
          >
            DICOM Viewer lets you upload and explore medical imaging files
            directly in your browser. Organise studies by patient, browse
            series, and inspect individual images — no additional software
            required.
          </p>

          {/* Feature pills */}
          <div className="flex flex-wrap gap-2 mb-12">
            {["DICOM 3.0", "Multi-series", "In-browser", "Zero install"].map(
              (tag) => (
                <span
                  key={tag}
                  className={`px-3 py-1 rounded-full text-xs font-mono border ${theme.colors.border} ${theme.colors.text.muted}`}
                >
                  {tag}
                </span>
              ),
            )}
          </div>

          {/* CTA buttons */}
          <div className="flex items-center gap-3">
            <Link href="/upload">
              <span
                className={`inline-block px-6 py-3 rounded-xl text-sm font-medium transition-colors cursor-pointer
                  ${theme.colors.button.background} ${theme.colors.button.hover} ${theme.colors.button.text}`}
              >
                Upload files →
              </span>
            </Link>
            <Link href="/patients">
              <span
                className={`inline-block px-6 py-3 rounded-xl text-sm font-medium transition-colors cursor-pointer border
                  ${theme.colors.border} ${theme.colors.button.hover} ${theme.colors.text.secondary}`}
              >
                View patients
              </span>
            </Link>
          </div>
        </div>
      </main>
    </div>
  );
}
