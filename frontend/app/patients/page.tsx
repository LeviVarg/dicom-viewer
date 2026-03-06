"use client";

import React, { useEffect, useState, useCallback } from "react";
import dynamic from "next/dynamic";
import {
  getPatients,
  getInstanceIdsForSeries,
} from "./actions/patients.actions";
import { PatientDto, SeriesDto, StudyDto } from "@/lib/types";
import { useTheme } from "@/lib/theme-context";

// Dynamically import DicomViewer to avoid SSR issues with Cornerstone
const DicomViewer = dynamic(() => import("@/components/DicomViewer"), {
  ssr: false,
  loading: () => (
    <div className="w-full h-[512px] bg-gray-200 flex items-center justify-center">
      <div className="text-gray-600">Loading viewer...</div>
    </div>
  ),
});

interface SelectedSeries {
  patient: PatientDto;
  study: StudyDto;
  series: SeriesDto;
  instanceIds: number[];
}

const PatientsPage: React.FC = () => {
  const { theme } = useTheme();
  const [patients, setPatients] = useState<PatientDto[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedSeries, setSelectedSeries] = useState<SelectedSeries | null>(
    null,
  );
  const [expandedPatients, setExpandedPatients] = useState<Set<number>>(
    new Set(),
  );
  const [expandedStudies, setExpandedStudies] = useState<Set<number>>(
    new Set(),
  );

  // Fetch patients on mount
  useEffect(() => {
    const fetchPatients = async () => {
      setIsLoading(true);
      setError(null);

      const result = await getPatients();

      if (result.success) {
        setPatients(result.data);
      } else {
        setError(result.error.message);
      }

      setIsLoading(false);
    };

    fetchPatients();
  }, []);

  // Toggle patient expansion
  const togglePatient = useCallback((patientId: number) => {
    setExpandedPatients((prev) => {
      const next = new Set(prev);
      if (next.has(patientId)) {
        next.delete(patientId);
      } else {
        next.add(patientId);
      }
      return next;
    });
  }, []);

  // Toggle study expansion
  const toggleStudy = useCallback((studyId: number) => {
    setExpandedStudies((prev) => {
      const next = new Set(prev);
      if (next.has(studyId)) {
        next.delete(studyId);
      } else {
        next.add(studyId);
      }
      return next;
    });
  }, []);

  // Handle series selection for viewing
  const handleSeriesClick = useCallback(
    async (patient: PatientDto, study: StudyDto, series: SeriesDto) => {
      const instanceIds = await getInstanceIdsForSeries(patient, series.id);
      setSelectedSeries({
        patient,
        study,
        series,
        instanceIds,
      });
    },
    [],
  );

  const formatPatientName = (name: string | null): string => {
    if (!name) return "Unknown";
    return name.replace(/\^/g, ", ");
  };

  const formatDate = (date: string | null): string => {
    if (!date) return "N/A";
    // DICOM dates are in format YYYYMMDD
    if (date.length === 8) {
      return `${date.slice(0, 4)}-${date.slice(4, 6)}-${date.slice(6, 8)}`;
    }
    return date;
  };

  if (isLoading) {
    return (
      <div
        className={`min-h-screen flex items-center justify-center ${theme.colors.background}`}
      >
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-gray-600 mx-auto mb-4"></div>
          <p className={`${theme.colors.text.primary} text-lg`}>
            Loading patients...
          </p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div
        className={`min-h-screen flex items-center justify-center ${theme.colors.background}`}
      >
        <div className={`text-center ${theme.colors.error}`}>
          <p className={`text-xl font-semibold mb-2`}>Error Loading Patients</p>
          <p>{error}</p>
          <button
            onClick={() => window.location.reload()}
            className={`mt-4 px-4 py-2 ${theme.colors.button.background} ${theme.colors.button.text} rounded-2xl ${theme.colors.button.hover} transition-colors`}
          >
            Retry
          </button>
        </div>
      </div>
    );
  }

  return (
    <div
      className={`min-h-screen ${theme.colors.background} ${theme.colors.text.primary}`}
    >
      <div className="container mx-auto px-4 py-8">
        <h1 className="text-3xl font-bold mb-8">DICOM Patients</h1>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          {/* Patient List */}
          <div className="space-y-4">
            <h2 className="text-xl font-semibold mb-4">
              Patients ({patients.length})
            </h2>

            {patients.length === 0 ? (
              <div
                className={`${theme.colors.surface} rounded-2xl p-6 text-center ${theme.colors.text.secondary}`}
              >
                <p>No patients found.</p>
                <p className="text-sm mt-2">
                  Upload DICOM files to get started.
                </p>
              </div>
            ) : (
              <div className="space-y-2">
                {patients.map((patient) => (
                  <div
                    key={patient.id}
                    className={`${theme.colors.surface} rounded-2xl overflow-hidden border ${theme.colors.border}`}
                  >
                    {/* Patient Header */}
                    <button
                      onClick={() => togglePatient(patient.id)}
                      className={`w-full px-4 py-3 flex items-center justify-between ${theme.colors.button.hover} transition-colors`}
                    >
                      <div className="flex items-center gap-3">
                        <span className="text-xl">
                          {expandedPatients.has(patient.id) ? "▼" : "▶"}
                        </span>
                        <div className="text-left">
                          <p className="font-medium">
                            {formatPatientName(patient.patientName)}
                          </p>
                          <p
                            className={`text-sm ${theme.colors.text.secondary}`}
                          >
                            ID: {patient.patientDicomId || "N/A"} | DOB:{" "}
                            {formatDate(patient.patientBirthDate)} | Sex:{" "}
                            {patient.patientSex || "N/A"}
                          </p>
                        </div>
                      </div>
                      <span
                        className={`text-sm ${theme.colors.text.secondary}`}
                      >
                        {patient.studies.length} studies
                      </span>
                    </button>

                    {/* Studies */}
                    {expandedPatients.has(patient.id) && (
                      <div className={`border-t ${theme.colors.border}`}>
                        {patient.studies.map((study) => (
                          <div
                            key={study.id}
                            className={`border-b ${theme.colors.border} last:border-b-0`}
                          >
                            {/* Study Header */}
                            <button
                              onClick={() => toggleStudy(study.id)}
                              className={`w-full px-6 py-2 flex items-center justify-between ${theme.colors.button.hover} transition-colors`}
                            >
                              <div className="flex items-center gap-2">
                                <span className="text-sm">
                                  {expandedStudies.has(study.id) ? "▼" : "▶"}
                                </span>
                                <div className="text-left">
                                  <p className="text-sm font-medium">
                                    {study.studyDescription || "Unnamed Study"}
                                  </p>
                                  <p
                                    className={`text-xs ${theme.colors.text.secondary}`}
                                  >
                                    Date: {formatDate(study.studyDate)} | ID:{" "}
                                    {study.studyId || "N/A"}
                                  </p>
                                </div>
                              </div>
                              <span
                                className={`text-xs ${theme.colors.text.secondary}`}
                              >
                                {study.seriesList.length} series
                              </span>
                            </button>

                            {/* Series */}
                            {expandedStudies.has(study.id) && (
                              <div className={theme.colors.surfaceAlt}>
                                {study.seriesList.map((series) => (
                                  <button
                                    key={series.id}
                                    onClick={() =>
                                      handleSeriesClick(patient, study, series)
                                    }
                                    className={`w-full px-8 py-2 flex items-center justify-between ${theme.colors.button.hover} transition-colors text-left ${
                                      selectedSeries?.series.id === series.id
                                        ? `${theme.colors.accent.background} border-l-2 border-gray-600`
                                        : ""
                                    }`}
                                  >
                                    <div>
                                      <p className="text-sm">
                                        {series.seriesDescription ||
                                          `Series ${series.seriesNumber || "N/A"}`}
                                      </p>
                                      <p
                                        className={`text-xs ${theme.colors.text.secondary}`}
                                      >
                                        Modality: {series.modality || "N/A"} |{" "}
                                        {series.instances.length} images
                                      </p>
                                    </div>
                                    <span
                                      className={`${theme.colors.text.secondary} text-sm`}
                                    >
                                      View →
                                    </span>
                                  </button>
                                ))}
                              </div>
                            )}
                          </div>
                        ))}
                      </div>
                    )}
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* DICOM Viewer */}
          <div className="space-y-4">
            <h2 className="text-xl font-semibold mb-4">DICOM Viewer</h2>

            {selectedSeries ? (
              <div>
                {/* Selected Series Info */}
                <div
                  className={`${theme.colors.surface} rounded-t-2xl p-4 border-b ${theme.colors.border}`}
                >
                  <p className="font-medium">
                    {formatPatientName(selectedSeries.patient.patientName)}
                  </p>
                  <p className={`text-sm ${theme.colors.text.secondary}`}>
                    {selectedSeries.study.studyDescription || "Unnamed Study"} →{" "}
                    {selectedSeries.series.seriesDescription ||
                      `Series ${selectedSeries.series.seriesNumber || ""}`}
                  </p>
                  <p className={`text-xs ${theme.colors.text.muted} mt-1`}>
                    Modality: {selectedSeries.series.modality || "N/A"} |{" "}
                    {selectedSeries.instanceIds.length} images
                  </p>
                </div>

                {/* Viewer */}
                <div
                  className={`${theme.colors.surface} rounded-b-2xl overflow-hidden`}
                >
                  <DicomViewer
                    instanceIds={selectedSeries.instanceIds}
                    height="512px"
                    onError={(error) => console.error("Viewer error:", error)}
                  />
                </div>
              </div>
            ) : (
              <div
                className={`${theme.colors.surface} rounded-2xl h-[512px] flex items-center justify-center`}
              >
                <div className={`text-center ${theme.colors.text.secondary}`}>
                  <p className="text-lg mb-2">No Series Selected</p>
                  <p className="text-sm">
                    Select a series from the patient list to view DICOM images
                  </p>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default PatientsPage;
