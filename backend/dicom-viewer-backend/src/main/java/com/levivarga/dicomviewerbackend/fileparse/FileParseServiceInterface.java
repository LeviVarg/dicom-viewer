package com.levivarga.dicomviewerbackend.fileparse;


import java.util.List;
import java.util.Map;

public interface FileParseService {
   public Map<String, Object> parseDicomFile(List<String> filePaths);
}
