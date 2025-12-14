package handlers

import (
	"fmt"
	"net/http"
	"os"

	"github.com/LeviVarg/dicom-viewer/internals/core"
	"github.com/LeviVarg/dicom-viewer/internals/models"
	"github.com/gin-gonic/gin"
)

func DicomParseHandler(context *gin.Context) {
	var parseRequest models.ParseRequest

	if err := context.BindJSON(&parseRequest); err != nil {
		context.JSON(http.StatusBadRequest, models.ParseResponse{Patients: nil, Error: err})
		return
	}

	// Debug: Print received file paths and check if they exist
	fmt.Println("=== DEBUG: Received file paths ===")
	for _, path := range parseRequest.FilePaths {
		fmt.Printf("Path: %s\n", path)
		if _, err := os.Stat(path); os.IsNotExist(err) {
			fmt.Printf("  -> FILE DOES NOT EXIST!\n")
		} else if err != nil {
			fmt.Printf("  -> Error checking file: %v\n", err)
		} else {
			fmt.Printf("  -> File exists ✓\n")
		}
	}
	fmt.Println("=================================")

	response := core.ProcessBatch(parseRequest.FilePaths)
	context.JSON(http.StatusOK, response)
}
