// Types matching the Spring Boot backend DTOs

export interface InstanceDto {
  id: number;
  sopInstanceUid: string;
  instanceNumber: number | null;
  sopClassUid: string | null;
  transferSyntaxUid: string | null;
  filePath: string;
}

export interface SeriesDto {
  id: number;
  seriesInstanceUid: string;
  seriesNumber: number | null;
  modality: string | null;
  seriesDescription: string | null;
  protocolName: string | null;
  bodyPartExamined: string | null;
  instances: InstanceDto[];
}

export interface StudyDto {
  id: number;
  studyInstanceUid: string;
  studyId: string | null;
  studyDate: string | null;
  studyDescription: string | null;
  accessionNumber: string | null;
  seriesList: SeriesDto[];
}

export interface PatientDto {
  id: number;
  patientDicomId: string | null;
  patientName: string | null;
  patientBirthDate: string | null;
  patientSex: string | null;
  studies: StudyDto[];
}

export interface PatientSummaryDto {
  id: number;
  patientDicomId: string | null;
  patientName: string | null;
  patientBirthDate: string | null;
  patientSex: string | null;
  studyCount: number;
}

// API response types
export interface ApiError {
  message: string;
  status?: number;
}

export type ApiResponse<T> =
  | {
      success: true;
      data: T;
    }
  | {
      success: false;
      error: ApiError;
    };
