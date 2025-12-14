package models

type Instance struct {
	SOPInstanceUID    string `json:"sopInstanceUID"`
	SeriesInstanceUID string `json:"seriesInstanceUID"`
	InstanceNumber    int    `json:"instanceUID"`
	SOPClassUID       string `json:"sopclassUID"`
	TransferSyntaxUID string `json:"transferSyntaxUID"`
	FilePath          string `json:"filePath"`
}

type Series struct {
	SeriesInstanceUID string     `json:"seriesInstanceUID"`
	StudyInstanceUID  string     `json:"studyInstanceUID"`
	SeriesNumber      int        `json:"seriesNumber"`
	Modality          string     `json:"modality"`
	SeriesDescription string     `json:"seriesDescription"`
	ProtocolName      string     `json:"protocolName"`
	BodyPartExamined  string     `json:"bodyPartExamined"`
	Instances         []Instance `json:"instances"`
}

type Study struct {
	StudyInstanceUID string   `json:"studyInstanceUID"`
	PatientUID       string   `json:"patientUID"`
	StudyID          string   `json:"studyID"`
	StudyDate        string   `json:"studyDate"`
	StudyDescription string   `json:"studyDescription"`
	AccessionNumber  string   `json:"accessionNumber"`
	Series           []Series `json:"series"`
}

type Patient struct {
	PatientUID       string  `json:"patientUID"`
	PatientID        string  `json:"patientID"`
	PatientName      string  `json:"patientName"`
	PatientBirthDate string  `json:"patientBirthDate"`
	PatientSex       string  `json:"patientSex"`
	Studies          []Study `json:"studies"`
}

type RawInstanceMetaData struct {
	PatientUID       string `json:"patientUID"`
	PatientID        string `json:"patientID"`
	PatientName      string `json:"patientName"`
	PatientBirthDate string `json:"patientBirthDate"`
	PatientSex       string `json:"patientSex"`

	StudyInstanceUID string `json:"studyInstanceUID"`
	StudyID          string `json:"studyID"`
	StudyDate        string `json:"studyDate"`
	StudyDescription string `json:"studyDescription"`
	AccessionNumber  string `json:"accessionNumber"`

	SeriesInstanceUID string `json:"seriesInstanceUID"`
	SeriesNumber      int    `json:"seriesNumber"`
	Modality          string `json:"modality"`
	SeriesDescription string `json:"seriesDescription"`
	ProtocolName      string `json:"protocolName"`
	BodyPartExamined  string `json:"bodyPartExamined"`

	SOPInstanceUID    string `json:"sopInstanceUID"`
	InstanceNumber    int    `json:"instanceUID"`
	SOPClassUID       string `json:"sopclassUID"`
	TransferSyntaxUID string `json:"transferSyntaxUID"`
	FilePath          string `json:"filePath"`
	Error             error  `json:"error"`
}

type ParseResponse struct {
	Patients []Patient `json:"patients"`
	Error    error     `json:"error"`
}

type ParseRequest struct {
	FilePaths []string `json:"filePaths"`
}
