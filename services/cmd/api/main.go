package main

import (
	"github.com/LeviVarg/dicom-viewer/internals/handlers"
	"github.com/gin-gonic/gin"
)

func main() {
	router := gin.Default()

	router.POST("/api/dicom/parse-batch", handlers.DicomParseHandler)
	router.Run(":8081")
}
