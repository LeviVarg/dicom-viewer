"use client";

import { createContext, useContext } from "react";

export type ThemeMode = "light" | "dark";

export interface EInkTheme {
  mode: ThemeMode;
  colors: {
    background: string;
    foreground: string;
    surface: string;
    surfaceAlt: string;
    border: string;
    text: {
      primary: string;
      secondary: string;
      muted: string;
    };

    button: {
      background: string;
      hover: string;
      text: string;
    };
    accent: {
      background: string;
      text: string;
    };
    error: string;
  };
}

const LIGHT_THEME: EInkTheme = {
  mode: "light",
  colors: {
    background: "bg-white",
    foreground: "text-black",
    surface: "bg-gray-100",
    surfaceAlt: "bg-gray-50",
    border: "border-gray-200",
    text: {
      primary: "text-black",
      secondary: "text-gray-600",
      muted: "text-gray-400",
    },
    button: {
      background: "bg-gray-200",
      hover: "hover:bg-gray-300",
      text: "text-black",
    },
    accent: {
      background: "bg-gray-300",
      text: "text-black",
    },
    error: "text-red-600",
  },
};

const DARK_THEME: EInkTheme = {
  mode: "dark",
  colors: {
    background: "bg-gray-900",
    foreground: "text-white",
    surface: "bg-gray-800",
    surfaceAlt: "bg-gray-700",
    border: "border-gray-700",
    text: {
      primary: "text-white",
      secondary: "text-gray-300",
      muted: "text-gray-500",
    },
    button: {
      background: "bg-gray-700",
      hover: "hover:bg-gray-600",
      text: "text-white",
    },
    accent: {
      background: "bg-gray-600",
      text: "text-white",
    },
    error: "text-red-400",
  },
};

interface ThemeContextType {
  theme: EInkTheme;
  mode: ThemeMode;
}

const ThemeContext = createContext<ThemeContextType | undefined>(undefined);

interface ThemeProviderProps {
  children: React.ReactNode;
  mode?: ThemeMode;
}

export function ThemeProvider({
  children,
  mode = "light",
}: ThemeProviderProps) {
  const theme = mode === "light" ? LIGHT_THEME : DARK_THEME;
  const value: ThemeContextType = {
    theme,
    mode,
  };

  return (
    <ThemeContext.Provider value={value}>{children}</ThemeContext.Provider>
  );
}

export function useTheme(): ThemeContextType {
  const context = useContext(ThemeContext);
  if (!context) {
    throw new Error("useTheme must be used within a ThemeProvider");
  }
  return context;
}
