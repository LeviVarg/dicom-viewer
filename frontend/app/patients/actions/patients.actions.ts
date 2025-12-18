"use server";

import { PatientDto, ApiResponse, ApiError } from "@/lib/types";

// Uses Docker service name for server-side calls (runs inside Docker network)
const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL || "http://springboot-backend:8080/api";

/**
 * Fetch all patients from the Spring Boot backend
 */
export async function getPatients(): Promise<ApiResponse<PatientDto[]>> {
  try {
    const response = await fetch(`${API_BASE_URL}/patients`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
      cache: "no-store", // Don't cache to always get fresh data
    });

    if (!response.ok) {
      const error: ApiError = {
        message: `Failed to fetch patients: ${response.statusText}`,
        status: response.status,
      };
      return { success: false, error };
    }

    const data: PatientDto[] = await response.json();
    return { success: true, data };
  } catch (err) {
    const error: ApiError = {
      message: err instanceof Error ? err.message : "Unknown error occurred",
    };
    return { success: false, error };
  }
}

/**
 * Fetch a single patient by ID from the Spring Boot backend
 */
export async function getPatientById(
  patientId: number,
): Promise<ApiResponse<PatientDto>> {
  try {
    const response = await fetch(`${API_BASE_URL}/patients/${patientId}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
      cache: "no-store",
    });

    if (!response.ok) {
      if (response.status === 404) {
        const error: ApiError = {
          message: `Patient with ID ${patientId} not found`,
          status: 404,
        };
        return { success: false, error };
      }

      const error: ApiError = {
        message: `Failed to fetch patient: ${response.statusText}`,
        status: response.status,
      };
      return { success: false, error };
    }

    const data: PatientDto = await response.json();
    return { success: true, data };
  } catch (err) {
    const error: ApiError = {
      message: err instanceof Error ? err.message : "Unknown error occurred",
    };
    return { success: false, error };
  }
}

/**
 * Get all instance IDs for a specific series
 * This is useful for loading a series into the DicomViewer
 */
export async function getInstanceIdsForSeries(
  patient: PatientDto,
  seriesId: number,
): Promise<number[]> {
  for (const study of patient.studies) {
    for (const series of study.seriesList) {
      if (series.id === seriesId) {
        return series.instances
          .sort((a, b) => (a.instanceNumber ?? 0) - (b.instanceNumber ?? 0))
          .map((instance) => instance.id);
      }
    }
  }
  return [];
}

/**
 * Get all instance IDs for a specific study
 */
export async function getInstanceIdsForStudy(
  patient: PatientDto,
  studyId: number,
): Promise<number[]> {
  for (const study of patient.studies) {
    if (study.id === studyId) {
      const instanceIds: number[] = [];
      for (const series of study.seriesList) {
        const sortedInstances = series.instances
          .sort((a, b) => (a.instanceNumber ?? 0) - (b.instanceNumber ?? 0))
          .map((instance) => instance.id);
        instanceIds.push(...sortedInstances);
      }
      return instanceIds;
    }
  }
  return [];
}

/**
 * Get the first instance ID for a patient (useful for thumbnails/previews)
 */
export async function getFirstInstanceId(
  patient: PatientDto,
): Promise<number | null> {
  if (patient.studies.length === 0) return null;
  const firstStudy = patient.studies[0];
  if (firstStudy.seriesList.length === 0) return null;
  const firstSeries = firstStudy.seriesList[0];
  if (firstSeries.instances.length === 0) return null;

  const sortedInstances = firstSeries.instances.sort(
    (a, b) => (a.instanceNumber ?? 0) - (b.instanceNumber ?? 0),
  );
  return sortedInstances[0].id;
}
