package com.levivarga.dicomviewerbackend.fileparse;

import com.levivarga.dicomviewerbackend.dto.DicomParseResultDto;
import java.util.List;

public interface FileParseServiceInterface {
    DicomParseResultDto parseDicomFile(List<String> filePaths);
}
