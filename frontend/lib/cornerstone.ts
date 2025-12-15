import dicomParser from "dicom-parser";

// Type definitions for dynamically imported modules
type CornerstoneCore = typeof import("@cornerstonejs/core");
type CornerstoneTools = typeof import("@cornerstonejs/tools");

// Module references (set after initialization)
let cornerstoneCore: CornerstoneCore | null = null;
let cornerstoneTools: CornerstoneTools | null = null;

let initialized = false;

/**
 * Initialize Cornerstone.js and its dependencies.
 * This should be called once before using any Cornerstone functionality.
 * Uses dynamic imports to avoid SSR issues.
 */
export async function initCornerstone(): Promise<void> {
  if (initialized) {
    return;
  }

  if (typeof window === "undefined") {
    throw new Error("Cornerstone can only be initialized in the browser");
  }

  try {
    // Dynamically import all cornerstone modules to avoid SSR issues
    const [coreModule, toolsModule, imageLoaderModule] = await Promise.all([
      import("@cornerstonejs/core"),
      import("@cornerstonejs/tools"),
      import("@cornerstonejs/dicom-image-loader"),
    ]);

    cornerstoneCore = coreModule;
    cornerstoneTools = toolsModule;

    // Debug: Log the structure of the image loader module
    console.log("=== DICOM Image Loader Debug ===");
    console.log("imageLoaderModule:", imageLoaderModule);
    console.log("imageLoaderModule keys:", Object.keys(imageLoaderModule));
    console.log("imageLoaderModule.default:", imageLoaderModule.default);
    if (imageLoaderModule.default) {
      console.log(
        "imageLoaderModule.default keys:",
        Object.keys(imageLoaderModule.default),
      );
    }

    // Initialize Cornerstone core first
    await cornerstoneCore.init();

    // Get the default export or the module itself
    const cornerstoneDICOMImageLoader =
      imageLoaderModule.default ?? imageLoaderModule;

    console.log("cornerstoneDICOMImageLoader:", cornerstoneDICOMImageLoader);
    console.log(
      "cornerstoneDICOMImageLoader.external:",
      cornerstoneDICOMImageLoader.external,
    );
    console.log(
      "cornerstoneDICOMImageLoader.init:",
      cornerstoneDICOMImageLoader.init,
    );
    console.log(
      "cornerstoneDICOMImageLoader.configure:",
      cornerstoneDICOMImageLoader.configure,
    );

    // For @cornerstonejs/dicom-image-loader v4+, initialization is different
    // The loader auto-registers with cornerstone, but we need to configure it

    // Check if the loader has the external property (older API)
    if (
      cornerstoneDICOMImageLoader.external &&
      typeof cornerstoneDICOMImageLoader.external === "object"
    ) {
      console.log("Using external property API");
      cornerstoneDICOMImageLoader.external.cornerstone = cornerstoneCore;
      cornerstoneDICOMImageLoader.external.dicomParser = dicomParser;
    }

    // Configure the DICOM image loader if configure function exists
    if (typeof cornerstoneDICOMImageLoader.configure === "function") {
      console.log("Calling configure()");
      cornerstoneDICOMImageLoader.configure({
        useWebWorkers: false, // Disable web workers to avoid codec/WASM issues
        decodeConfig: {
          convertFloatPixelDataToInt: false,
          use16BitDataType: true,
        },
      });
    }

    // For v4+, we need to use the init function if available
    if (typeof cornerstoneDICOMImageLoader.init === "function") {
      console.log("Calling init()");
      await cornerstoneDICOMImageLoader.init({
        maxWebWorkers: 0, // Disable web workers
      });
    }

    console.log("=== End DICOM Image Loader Debug ===");

    // Initialize Cornerstone Tools
    await cornerstoneTools.init();

    // Add tools
    cornerstoneTools.addTool(cornerstoneTools.PanTool);
    cornerstoneTools.addTool(cornerstoneTools.ZoomTool);
    cornerstoneTools.addTool(cornerstoneTools.WindowLevelTool);
    cornerstoneTools.addTool(cornerstoneTools.StackScrollTool);
    cornerstoneTools.addTool(cornerstoneTools.LengthTool);
    cornerstoneTools.addTool(cornerstoneTools.RectangleROITool);

    initialized = true;
    console.log("Cornerstone.js initialized successfully");
  } catch (error) {
    console.error("Failed to initialize Cornerstone:", error);
    throw error;
  }
}

/**
 * Check if Cornerstone is initialized
 */
export function isCornerstoneInitialized(): boolean {
  return initialized;
}

/**
 * Get the Cornerstone core module (must be initialized first)
 */
export function getCornerstone(): CornerstoneCore {
  if (!cornerstoneCore) {
    throw new Error(
      "Cornerstone not initialized. Call initCornerstone() first.",
    );
  }
  return cornerstoneCore;
}

/**
 * Get the Cornerstone tools module (must be initialized first)
 */
export function getCornerstoneTools(): CornerstoneTools {
  if (!cornerstoneTools) {
    throw new Error(
      "Cornerstone not initialized. Call initCornerstone() first.",
    );
  }
  return cornerstoneTools;
}

/**
 * Create a rendering engine
 */
export function createRenderingEngine(
  engineId: string,
): InstanceType<CornerstoneCore["RenderingEngine"]> {
  const cs = getCornerstone();
  return new cs.RenderingEngine(engineId);
}

/**
 * Create a tool group with common tools for DICOM viewing
 */
export function createToolGroup(
  toolGroupId: string,
): ReturnType<CornerstoneTools["ToolGroupManager"]["createToolGroup"]> {
  const csTools = getCornerstoneTools();

  const toolGroup = csTools.ToolGroupManager.createToolGroup(toolGroupId);

  if (!toolGroup) {
    console.error("Failed to create tool group");
    return undefined;
  }

  // Add tools to the group
  toolGroup.addTool(csTools.PanTool.toolName);
  toolGroup.addTool(csTools.ZoomTool.toolName);
  toolGroup.addTool(csTools.WindowLevelTool.toolName);
  toolGroup.addTool(csTools.StackScrollTool.toolName);
  toolGroup.addTool(csTools.LengthTool.toolName);
  toolGroup.addTool(csTools.RectangleROITool.toolName);

  // Set default active tools
  // Left mouse button: Window/Level
  toolGroup.setToolActive(csTools.WindowLevelTool.toolName, {
    bindings: [{ mouseButton: csTools.Enums.MouseBindings.Primary }],
  });

  // Right mouse button: Zoom
  toolGroup.setToolActive(csTools.ZoomTool.toolName, {
    bindings: [{ mouseButton: csTools.Enums.MouseBindings.Secondary }],
  });

  // Middle mouse button: Pan
  toolGroup.setToolActive(csTools.PanTool.toolName, {
    bindings: [{ mouseButton: csTools.Enums.MouseBindings.Auxiliary }],
  });

  // Mouse wheel: Stack scroll
  toolGroup.setToolActive(csTools.StackScrollTool.toolName, {
    bindings: [{ mouseButton: csTools.Enums.MouseBindings.Wheel }],
  });

  return toolGroup;
}

/**
 * Destroy a tool group
 */
export function destroyToolGroup(toolGroupId: string): void {
  const csTools = getCornerstoneTools();
  csTools.ToolGroupManager.destroyToolGroup(toolGroupId);
}

/**
 * Get a tool group by ID
 */
export function getToolGroup(
  toolGroupId: string,
): ReturnType<CornerstoneTools["ToolGroupManager"]["getToolGroup"]> {
  const csTools = getCornerstoneTools();
  return csTools.ToolGroupManager.getToolGroup(toolGroupId);
}

/**
 * Build a wadouri image ID for a DICOM file served from the backend.
 *
 * IMPORTANT: This uses NEXT_PUBLIC_BACKEND_URL which must be accessible from the BROWSER.
 * In Docker setup:
 * - Server actions use Docker service names (e.g., "springboot-backend:8080")
 * - Client-side code (like this) uses localhost since browser is outside Docker network
 *
 * Default: http://localhost:8080 (mapped from Docker's port 8080:8080)
 */
export function buildWadoUriImageId(instanceId: number | string): string {
  const backendUrl =
    process.env.NEXT_PUBLIC_BACKEND_URL || "http://localhost:8080";
  return `wadouri:${backendUrl}/api/patients/instances/${instanceId}/file`;
}

/**
 * Build multiple wadouri image IDs for a series
 */
export function buildWadoUriImageIds(
  instanceIds: (number | string)[],
): string[] {
  return instanceIds.map(buildWadoUriImageId);
}

/**
 * Get viewport type enum value for stack viewport
 */
export function getStackViewportType(): number {
  const cs = getCornerstone();
  return cs.Enums.ViewportType.STACK;
}
