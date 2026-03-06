package core

import (
	"fmt"
	"sync"

	"github.com/LeviVarg/dicom-viewer/internals/models"
)

func ProcessBatch(filePaths []string) models.ParseResponse {
	numFiles := len(filePaths)
	resultChan := make(chan models.RawInstanceMetaData, numFiles)
	var wg sync.WaitGroup
	var errors []error
	var mx sync.Mutex

	for _, path := range filePaths {
		wg.Add(1)
		go func(p string) {
			defer wg.Done()
			parsedData, err := ParseDicomFile(p)
			if err != nil {
				mx.Lock()
				errors = append(errors, fmt.Errorf("error parsing file %s: %v", p, err))
				mx.Unlock()
			} else {
				resultChan <- parsedData
			}
		}(path)
	}

	go func() {
		wg.Wait()
		close(resultChan)
	}()

	result := summarizeResults(resultChan)

	if len(errors) > 0 {
		result.Error = fmt.Errorf("%d files failed to parse", len(errors))
	}
	return result
}

func summarizeResults(resultChan <-chan models.RawInstanceMetaData) models.ParseResponse {
	patientsMap := make(map[string]*models.Patient)
	studiesMap := make(map[string]*models.Study)
	seriesMap := make(map[string]*models.Series)

	for dicomData := range resultChan {
		fmt.Printf("Processing DICOM data: PatientID=%s, StudyUID=%s, SeriesUID=%s, SOPUID=%s\n",
			dicomData.PatientID, dicomData.StudyInstanceUID, dicomData.SeriesInstanceUID, dicomData.SOPInstanceUID)

		// Create or get patient
		if _, exists := patientsMap[dicomData.PatientUID]; !exists {
			patientsMap[dicomData.PatientUID] = &models.Patient{
				PatientUID:       dicomData.PatientUID,
				PatientID:        dicomData.PatientID,
				PatientName:      dicomData.PatientName,
				PatientBirthDate: dicomData.PatientBirthDate,
				PatientSex:       dicomData.PatientSex,
				Studies:          []models.Study{},
			}
		}

		// Create or get study
		if _, exists := studiesMap[dicomData.StudyInstanceUID]; !exists {
			studiesMap[dicomData.StudyInstanceUID] = &models.Study{
				StudyInstanceUID: dicomData.StudyInstanceUID,
				PatientUID:       dicomData.PatientUID,
				StudyID:          dicomData.StudyID,
				StudyDate:        dicomData.StudyDate,
				StudyDescription: dicomData.StudyDescription,
				AccessionNumber:  dicomData.AccessionNumber,
				Series:           []models.Series{},
			}
		}

		// Create or get series
		if _, exists := seriesMap[dicomData.SeriesInstanceUID]; !exists {
			seriesMap[dicomData.SeriesInstanceUID] = &models.Series{
				SeriesInstanceUID: dicomData.SeriesInstanceUID,
				StudyInstanceUID:  dicomData.StudyInstanceUID,
				SeriesNumber:      dicomData.SeriesNumber,
				Modality:          dicomData.Modality,
				SeriesDescription: dicomData.SeriesDescription,
				ProtocolName:      dicomData.ProtocolName,
				BodyPartExamined:  dicomData.BodyPartExamined,
				Instances:         []models.Instance{},
			}
		}

		// Create instance and add to series
		instance := models.Instance{
			SOPInstanceUID:    dicomData.SOPInstanceUID,
			SeriesInstanceUID: dicomData.SeriesInstanceUID,
			InstanceNumber:    dicomData.InstanceNumber,
			SOPClassUID:       dicomData.SOPClassUID,
			TransferSyntaxUID: dicomData.TransferSyntaxUID,
			FilePath:          dicomData.FilePath,
		}

		seriesMap[dicomData.SeriesInstanceUID].Instances = append(
			seriesMap[dicomData.SeriesInstanceUID].Instances,
			instance,
		)
	}

	// Build the nested structure: Series -> Studies -> Patients
	for _, series := range seriesMap {
		study := studiesMap[series.StudyInstanceUID]
		if study != nil {
			study.Series = append(study.Series, *series)
		}
	}

	// Then, add all studies to their respective patients
	for _, study := range studiesMap {
		patient := patientsMap[study.PatientUID]
		if patient != nil {
			patient.Studies = append(patient.Studies, *study)
		}
	}

	// Build final patient list
	finalPatients := make([]models.Patient, 0, len(patientsMap))
	for _, patient := range patientsMap {
		finalPatients = append(finalPatients, *patient)
	}

	fmt.Printf("Summarized: %d patients, %d studies, %d series\n",
		len(patientsMap), len(studiesMap), len(seriesMap))

	return models.ParseResponse{Patients: finalPatients, Error: nil}
}
