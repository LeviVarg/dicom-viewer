package com.levivarga.dicomviewerbackend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstanceDto {

    private Long id;
    private String sopInstanceUid;
    private Integer instanceNumber;
    private String sopClassUid;
    private String transferSyntaxUid;
    private String filePath;

    @Override
    public String toString() {
        return "InstanceDto{" +
                "id=" + id +
                ", sopInstanceUid='" + sopInstanceUid + '\'' +
                ", instanceNumber=" + instanceNumber +
                ", sopClassUid='" + sopClassUid + '\'' +
                ", transferSyntaxUid='" + transferSyntaxUid + '\'' +
                ", filePath='" + filePath + '\'' +
                '}';
    }
}
