package core

import (
	"fmt"
	"os"
	"strconv"
	"strings"

	"github.com/LeviVarg/dicom-viewer/internals/models"
	"github.com/suyashkumar/dicom"
	"github.com/suyashkumar/dicom/pkg/tag"
)

func ParseDicomFile(filePath string) (models.RawInstanceMetaData, error) {
	fmt.Printf("=== DEBUG: Parsing file: %s ===\n", filePath)

	// Check file info before parsing
	fileInfo, statErr := os.Stat(filePath)
	if statErr != nil {
		fmt.Printf("  -> Error getting file info: %v\n", statErr)
		return models.RawInstanceMetaData{FilePath: filePath, Error: statErr}, statErr
	} else {
		fmt.Printf("  -> File size: %d bytes\n", fileInfo.Size())
	}

	dataset, err := dicom.ParseFile(filePath, nil)

	if err != nil {
		fmt.Printf("  -> PARSE ERROR: %v\n", err)
		return models.RawInstanceMetaData{FilePath: filePath, Error: err}, err
	}

	fmt.Printf("  -> Parse successful, dataset has %d elements\n", len(dataset.Elements))

	return models.RawInstanceMetaData{
		PatientUID:       getString(dataset, tag.PatientID),
		PatientID:        getString(dataset, tag.PatientID),
		PatientName:      getString(dataset, tag.PatientName),
		PatientBirthDate: getString(dataset, tag.PatientBirthDate),
		PatientSex:       getString(dataset, tag.PatientSex),

		StudyInstanceUID: getString(dataset, tag.StudyInstanceUID),
		StudyID:          getString(dataset, tag.StudyID),
		StudyDate:        getString(dataset, tag.StudyDate),
		StudyDescription: getString(dataset, tag.StudyDescription),
		AccessionNumber:  getString(dataset, tag.AccessionNumber),

		SeriesInstanceUID: getString(dataset, tag.SeriesInstanceUID),
		SeriesNumber:      getInt(dataset, tag.SeriesNumber),
		Modality:          getString(dataset, tag.Modality),
		SeriesDescription: getString(dataset, tag.SeriesDescription),
		ProtocolName:      getString(dataset, tag.ProtocolName),
		BodyPartExamined:  getString(dataset, tag.BodyPartExamined),

		SOPInstanceUID:    getString(dataset, tag.SOPInstanceUID),
		InstanceNumber:    getInt(dataset, tag.InstanceNumber),
		SOPClassUID:       getString(dataset, tag.SOPClassUID),
		TransferSyntaxUID: getString(dataset, tag.TransferSyntaxUID),
		FilePath:          filePath,
		Error:             nil,
	}, nil
}

func getString(dataset dicom.Dataset, t tag.Tag) string {
	element, err := dataset.FindElementByTag(t)
	if err != nil {
		// Don't log "element not found" errors as they're expected for optional tags
		return ""
	}

	value := element.Value.String()

	// The DICOM library returns values wrapped in brackets like "[value]"
	// Strip them to get the actual value
	value = strings.TrimPrefix(value, "[")
	value = strings.TrimSuffix(value, "]")

	// Trim any whitespace
	value = strings.TrimSpace(value)

	return value
}

func getInt(dataset dicom.Dataset, t tag.Tag) int {
	element, err := dataset.FindElementByTag(t)
	if err != nil {
		return 0
	}

	value := element.Value.String()

	// Strip brackets from the value
	value = strings.TrimPrefix(value, "[")
	value = strings.TrimSuffix(value, "]")
	value = strings.TrimSpace(value)

	intValue, err := strconv.Atoi(value)
	if err != nil {
		return 0
	}

	return intValue
}
