-- DICOM Data Schema
-- Uses database-generated IDs as primary keys since DICOM PatientID can be unreliable
-- DICOM UIDs are stored as indexed columns for lookups

-- Patient table
CREATE TABLE IF NOT EXISTS patients (
                                        id BIGSERIAL PRIMARY KEY,
                                        patient_dicom_id VARCHAR(64),           -- DICOM PatientID (0010,0020) - can be empty/duplicate
                                        patient_name VARCHAR(255),               -- DICOM PatientName (0010,0010)
                                        patient_birth_date VARCHAR(8),           -- DICOM PatientBirthDate (0010,0030) - format: YYYYMMDD
                                        patient_sex VARCHAR(16),                 -- DICOM PatientSex (0010,0040) - M, F, O
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index on patient_dicom_id for lookups (not unique since anonymized files may have duplicates)
CREATE INDEX IF NOT EXISTS idx_patients_dicom_id ON patients(patient_dicom_id);

-- Study table
CREATE TABLE IF NOT EXISTS studies (
                                       id BIGSERIAL PRIMARY KEY,
                                       patient_id BIGINT NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
                                       study_instance_uid VARCHAR(128) NOT NULL UNIQUE,  -- DICOM StudyInstanceUID (0020,000D) - guaranteed unique
                                       study_id VARCHAR(64),                    -- DICOM StudyID (0020,0010)
                                       study_date VARCHAR(8),                   -- DICOM StudyDate (0008,0020) - format: YYYYMMDD
                                       study_description VARCHAR(255),          -- DICOM StudyDescription (0008,1030)
                                       accession_number VARCHAR(64),            -- DICOM AccessionNumber (0008,0050)
                                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_studies_patient_id ON studies(patient_id);
CREATE INDEX IF NOT EXISTS idx_studies_study_date ON studies(study_date);

-- Series table
CREATE TABLE IF NOT EXISTS series (
                                      id BIGSERIAL PRIMARY KEY,
                                      study_id BIGINT NOT NULL REFERENCES studies(id) ON DELETE CASCADE,
                                      series_instance_uid VARCHAR(128) NOT NULL UNIQUE,  -- DICOM SeriesInstanceUID (0020,000E) - guaranteed unique
                                      series_number INTEGER,                   -- DICOM SeriesNumber (0020,0011)
                                      modality VARCHAR(16),                    -- DICOM Modality (0008,0060) - CT, MR, US, etc.
                                      series_description VARCHAR(255),         -- DICOM SeriesDescription (0008,103E)
                                      protocol_name VARCHAR(255),              -- DICOM ProtocolName (0018,1030)
                                      body_part_examined VARCHAR(64),          -- DICOM BodyPartExamined (0018,0015)
                                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_series_study_id ON series(study_id);
CREATE INDEX IF NOT EXISTS idx_series_modality ON series(modality);

-- Instance table (individual DICOM images/objects)
CREATE TABLE IF NOT EXISTS instances (
                                         id BIGSERIAL PRIMARY KEY,
                                         series_id BIGINT NOT NULL REFERENCES series(id) ON DELETE CASCADE,
                                         sop_instance_uid VARCHAR(128) NOT NULL UNIQUE,    -- DICOM SOPInstanceUID (0008,0018) - guaranteed unique
                                         instance_number INTEGER,                 -- DICOM InstanceNumber (0020,0013)
                                         sop_class_uid VARCHAR(128),              -- DICOM SOPClassUID (0008,0016) - type of DICOM object
                                         transfer_syntax_uid VARCHAR(128),        -- DICOM TransferSyntaxUID (0002,0010) - encoding
                                         file_path VARCHAR(512) NOT NULL,         -- Path to the DICOM file on disk
                                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_instances_series_id ON instances(series_id);
CREATE INDEX IF NOT EXISTS idx_instances_file_path ON instances(file_path);
