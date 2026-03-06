"use client";

import React, {
  useEffect,
  useRef,
  useState,
  useCallback,
  MouseEvent,
} from "react";
import {
  initCornerstone,
  isCornerstoneInitialized,
  createRenderingEngine,
  createToolGroup,
  destroyToolGroup,
  getToolGroup,
  buildWadoUriImageIds,
  getStackViewportType,
} from "@/lib/cornerstone";
import { useTheme } from "@/lib/theme-context";

interface DicomViewerProps {
  /** Array of instance IDs to display in the viewer */
  instanceIds: (number | string)[];
  /** Optional CSS class name for the container */
  className?: string;
  /** Optional width for the viewer */
  width?: string | number;
  /** Optional height for the viewer */
  height?: string | number;
  /** Called when the current image index changes */
  onImageChange?: (index: number) => void;
  /** Called when an error occurs */
  onError?: (error: Error) => void;
}

const DicomViewer: React.FC<DicomViewerProps> = ({
  instanceIds,
  className = "",
  width = "100%",
  height = "512px",
  onImageChange,
  onError,
}) => {
  const { theme } = useTheme();
  const containerRef = useRef<HTMLDivElement>(null);
  const [isInitialized, setIsInitialized] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [totalImages, setTotalImages] = useState(0);

  // Unique IDs for this viewer instance
  const viewerIdRef = useRef<string>(
    `dicom-viewer-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`,
  );
  const renderingEngineRef = useRef<ReturnType<
    typeof createRenderingEngine
  > | null>(null);

  const VIEWPORT_ID = `${viewerIdRef.current}-viewport`;
  const TOOL_GROUP_ID = `${viewerIdRef.current}-toolgroup`;

  // Prevent context menu on right-click
  const handleContextMenu = useCallback((e: MouseEvent<HTMLDivElement>) => {
    e.preventDefault();
    return false;
  }, []);

  // Initialize Cornerstone
  useEffect(() => {
    const init = async () => {
      try {
        if (!isCornerstoneInitialized()) {
          await initCornerstone();
        }
        setIsInitialized(true);
      } catch (err) {
        const error =
          err instanceof Error
            ? err
            : new Error("Failed to initialize Cornerstone");
        setError(error.message);
        onError?.(error);
      }
    };

    init();
  }, [onError]);

  // Set up the viewer when initialized and instanceIds change
  useEffect(() => {
    if (!isInitialized || !containerRef.current || instanceIds.length === 0) {
      return;
    }

    const element = containerRef.current;
    let isMounted = true;

    const setupViewer = async () => {
      setIsLoading(true);
      setError(null);

      try {
        // Clean up existing rendering engine if it exists
        if (renderingEngineRef.current) {
          renderingEngineRef.current.destroy();
          renderingEngineRef.current = null;
        }

        // Destroy existing tool group if it exists
        const existingToolGroup = getToolGroup(TOOL_GROUP_ID);
        if (existingToolGroup) {
          destroyToolGroup(TOOL_GROUP_ID);
        }

        // Create new rendering engine
        const renderingEngine = createRenderingEngine(viewerIdRef.current);
        renderingEngineRef.current = renderingEngine;

        // Create viewport input
        const viewportInput = {
          viewportId: VIEWPORT_ID,
          type: getStackViewportType(),
          element: element,
          defaultOptions: {
            background: [0, 0, 0] as [number, number, number],
          },
        };

        // Enable the element
        renderingEngine.enableElement(viewportInput);

        // Get the viewport
        const viewport = renderingEngine.getViewport(VIEWPORT_ID);
        if (!viewport) {
          throw new Error("Failed to get viewport");
        }

        // Build image IDs from instance IDs
        const imageIds = buildWadoUriImageIds(instanceIds);
        setTotalImages(imageIds.length);

        // Set up the stack - cast to IStackViewport to access setStack
        const stackViewport = viewport as {
          setStack: (
            imageIds: string[],
            initialIndex?: number,
          ) => Promise<void>;
          render: () => void;
        };
        await stackViewport.setStack(imageIds, 0);

        // Create tool group
        const toolGroup = createToolGroup(TOOL_GROUP_ID);
        if (toolGroup) {
          toolGroup.addViewport(VIEWPORT_ID, viewerIdRef.current);
        }

        // Render the viewport
        stackViewport.render();

        if (isMounted) {
          setIsLoading(false);
          setCurrentIndex(0);
          onImageChange?.(0);
        }
      } catch (err) {
        if (isMounted) {
          const error =
            err instanceof Error
              ? err
              : new Error("Failed to load DICOM images");
          setError(error.message);
          setIsLoading(false);
          onError?.(error);
        }
      }
    };

    setupViewer();

    // Cleanup function
    return () => {
      isMounted = false;
      try {
        if (renderingEngineRef.current) {
          renderingEngineRef.current.destroy();
          renderingEngineRef.current = null;
        }
        const existingToolGroup = getToolGroup(TOOL_GROUP_ID);
        if (existingToolGroup) {
          destroyToolGroup(TOOL_GROUP_ID);
        }
      } catch {
        // Ignore errors during cleanup
      }
    };
  }, [
    isInitialized,
    instanceIds,
    onImageChange,
    onError,
    VIEWPORT_ID,
    TOOL_GROUP_ID,
  ]);

  // Handle keyboard navigation
  const handleKeyDown = useCallback(
    async (e: KeyboardEvent) => {
      if (!renderingEngineRef.current || instanceIds.length <= 1) return;

      const viewport = renderingEngineRef.current.getViewport(VIEWPORT_ID) as
        | {
            setImageIdIndex: (index: number) => Promise<void>;
            render: () => void;
          }
        | undefined;
      if (!viewport) return;

      let newIndex = currentIndex;

      if (e.key === "ArrowUp" || e.key === "ArrowLeft") {
        newIndex = Math.max(0, currentIndex - 1);
      } else if (e.key === "ArrowDown" || e.key === "ArrowRight") {
        newIndex = Math.min(instanceIds.length - 1, currentIndex + 1);
      } else {
        return;
      }

      if (newIndex !== currentIndex) {
        await viewport.setImageIdIndex(newIndex);
        viewport.render();
        setCurrentIndex(newIndex);
        onImageChange?.(newIndex);
      }
    },
    [currentIndex, instanceIds.length, onImageChange, VIEWPORT_ID],
  );

  useEffect(() => {
    window.addEventListener("keydown", handleKeyDown);
    return () => window.removeEventListener("keydown", handleKeyDown);
  }, [handleKeyDown]);

  // Navigation functions
  const goToImage = useCallback(
    async (index: number) => {
      if (
        !renderingEngineRef.current ||
        index < 0 ||
        index >= instanceIds.length
      )
        return;

      const viewport = renderingEngineRef.current.getViewport(VIEWPORT_ID) as
        | {
            setImageIdIndex: (index: number) => Promise<void>;
            render: () => void;
          }
        | undefined;
      if (!viewport) return;

      await viewport.setImageIdIndex(index);
      viewport.render();
      setCurrentIndex(index);
      onImageChange?.(index);
    },
    [instanceIds.length, onImageChange, VIEWPORT_ID],
  );

  const goToNext = useCallback(() => {
    goToImage(Math.min(instanceIds.length - 1, currentIndex + 1));
  }, [currentIndex, instanceIds.length, goToImage]);

  const goToPrevious = useCallback(() => {
    goToImage(Math.max(0, currentIndex - 1));
  }, [currentIndex, goToImage]);

  // Reset viewport
  const handleResetViewport = useCallback(() => {
    if (!renderingEngineRef.current) return;

    const viewport = renderingEngineRef.current.getViewport(VIEWPORT_ID) as
      | {
          resetCamera: () => void;
          resetProperties: () => void;
          render: () => void;
        }
      | undefined;
    if (!viewport) return;

    viewport.resetCamera();
    viewport.resetProperties();
    viewport.render();
  }, [VIEWPORT_ID]);

  return (
    <div
      className={`dicom-viewer-container ${className}`}
      style={{ width, height }}
    >
      {/* Viewer Element */}
      <div
        ref={containerRef}
        className="dicom-viewport"
        style={{
          width: "100%",
          height:
            instanceIds.length > 1 ? "calc(100% - 80px)" : "calc(100% - 32px)",
          backgroundColor: "#000",
          position: "relative",
        }}
        tabIndex={0}
        onContextMenu={handleContextMenu}
      >
        {/* Loading Overlay */}
        {isLoading && (
          <div
            style={{
              position: "absolute",
              inset: 0,
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              backgroundColor: "rgba(0, 0, 0, 0.7)",
              color: "white",
              zIndex: 10,
            }}
          >
            <div className="text-center">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-white mx-auto mb-2"></div>
              <p>Loading DICOM images...</p>
            </div>
          </div>
        )}

        {/* Error Overlay */}
        {error && (
          <div
            style={{
              position: "absolute",
              inset: 0,
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              backgroundColor: "rgba(0, 0, 0, 0.7)",
              color: "white",
              zIndex: 10,
            }}
          >
            <div className="text-center text-red-400">
              <p className="text-lg font-semibold mb-2">Error</p>
              <p>{error}</p>
            </div>
          </div>
        )}

        {/* No Images Message */}
        {!isLoading && !error && instanceIds.length === 0 && (
          <div
            style={{
              position: "absolute",
              inset: 0,
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              backgroundColor: "#1a1a1a",
              color: "#888",
            }}
          >
            <p>No DICOM images to display</p>
          </div>
        )}
      </div>

      {/* Navigation Controls */}
      {instanceIds.length > 1 && (
        <div
          className="dicom-controls"
          style={{
            height: "48px",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            gap: "16px",
            backgroundColor: theme.colors.surface,
            padding: "0 16px",
          }}
        >
          <button
            onClick={goToPrevious}
            disabled={currentIndex === 0}
            className={`px-3 py-1 ${theme.colors.button.background} ${theme.colors.button.text} rounded-lg disabled:opacity-50 disabled:cursor-not-allowed ${theme.colors.button.hover} transition-colors`}
          >
            ← Previous
          </button>

          <span className={`${theme.colors.text.primary} text-sm`}>
            {currentIndex + 1} / {totalImages}
          </span>

          <button
            onClick={goToNext}
            disabled={currentIndex === instanceIds.length - 1}
            className={`px-3 py-1 ${theme.colors.button.background} ${theme.colors.button.text} rounded-lg disabled:opacity-50 disabled:cursor-not-allowed ${theme.colors.button.hover} transition-colors`}
          >
            Next →
          </button>

          <div
            style={{
              borderLeft: `1px solid ${theme.mode === "dark" ? "#374151" : "#e5e7eb"}`,
              height: "24px",
              margin: "0 8px",
            }}
          />

          <button
            onClick={handleResetViewport}
            className={`px-3 py-1 ${theme.colors.button.background} ${theme.colors.button.text} rounded-lg ${theme.colors.button.hover} transition-colors`}
          >
            Reset
          </button>

          <span className={`${theme.colors.text.secondary} text-xs ml-4`}>
            Arrow keys or mouse wheel to navigate
          </span>
        </div>
      )}

      {/* Tool Instructions */}
      <div
        className={`text-xs ${theme.colors.text.secondary} py-2 text-center ${theme.colors.surface}`}
      >
        Left click: Window/Level | Right click: Zoom | Middle click: Pan
      </div>
    </div>
  );
};

export default DicomViewer;
