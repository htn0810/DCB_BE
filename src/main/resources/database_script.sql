Digital Core - ver1.6

====== < Script Create table "technical_employees" && Insert data into (this table is sample data for filtering technical account from LDAP) > ====

create table technical_employees (
    ntid varchar(50) NOT NULL,
    display_name varchar(255) NOT NULL,
    primary key (ntid, display_name),
    foreign key (ntid) references employees(ntid) ON UPDATE CASCADE ON DELETE CASCADE,
    foreign key (ntid) references employees(ntid) ON UPDATE CASCADE ON DELETE CASCADE
);

INSERT INTO `uaa-core-dev`.technical_employees (ntid, display_name) VALUES ('RCI8HC', 'RBVH CI PO');
INSERT INTO `uaa-core-dev`.technical_employees (ntid, display_name) VALUES ('ETD8HC', 'ETM2 DEE (SX/BSV-VN)');
INSERT INTO `uaa-core-dev`.technical_employees (ntid, display_name) VALUES ('EUT8HC', 'ETM Learning (SX/BSV-VN)');
INSERT INTO `uaa-core-dev`.technical_employees (ntid, display_name) VALUES ('ETC8HC', 'ETM-People Connect');
INSERT INTO `uaa-core-dev`.technical_employees (ntid, display_name) VALUES ('NOR1HC', 'NoReply RBVHTools (SX/BSV2-VN)');
INSERT INTO `uaa-core-dev`.technical_employees (ntid, display_name) VALUES ('UST8HC', 'User Technical (SX/BSV2-VN)');
INSERT INTO `uaa-core-dev`.technical_employees (ntid, display_name) VALUES ('OCC1KOR', 'Process Compass (SX/BSV25-VN)');
INSERT INTO `uaa-core-dev`.technical_employees (ntid, display_name) VALUES ('EAB2KOR', 'EAA-crEAAtive Team RBEI (SX/EAA)');
INSERT INTO `uaa-core-dev`.technical_employees (ntid, display_name) VALUES ('EOT1KOR', 'Automation Test (SX/EAA)');
INSERT INTO `uaa-core-dev`.technical_employees (ntid, display_name) VALUES ('BDEE1KOR', 'deepsights technicaluser');
INSERT INTO `uaa-core-dev`.technical_employees (ntid, display_name) VALUES ('IEP2KOR', 'DeviceBridge ApplicationSupport');
INSERT INTO `uaa-core-dev`.technical_employees (ntid, display_name) VALUES ('ILU1KOR', 'mailer auto');
INSERT INTO `uaa-core-dev`.technical_employees (ntid, display_name) VALUES ('INE1KOR', 'Inertia EIA');
INSERT INTO `uaa-core-dev`.technical_employees (ntid, display_name) VALUES ('PLT1KOR', 'Plus Techno');
INSERT INTO `uaa-core-dev`.technical_employees (ntid, display_name) VALUES ('PEO2KOR', 'Product Support EIA');
INSERT INTO `uaa-core-dev`.technical_employees (ntid, display_name) VALUES ('CED9KOR', 'DSC Server');
INSERT INTO `uaa-core-dev`.technical_employees (ntid, display_name) VALUES ('ACP8HC', 'acc poc (SX/BSV42-VN)');
INSERT INTO `uaa-core-dev`.technical_employees (ntid, display_name) VALUES ('POI8HC', 'poc internal (SX/BSV42-VN)');
INSERT INTO `uaa-core-dev`.technical_employees (ntid, display_name) VALUES ('RCO2KOR', 'SX/BSS Communications');
INSERT INTO `uaa-core-dev`.technical_employees (ntid, display_name) VALUES ('FJC1COB', 'Technical 9 JSDL');
INSERT INTO `uaa-core-dev`.technical_employees (ntid, display_name) VALUES ('EMS8HC', 'emt1 system user ETM (SX/BSV12-VN)');
INSERT INTO `uaa-core-dev`.technical_employees (ntid, display_name) VALUES ('AII1HC', 'AI Big Data (SX/BSV-VN)');
INSERT INTO `uaa-core-dev`.technical_employees (ntid, display_name) VALUES ('CPQ2KOR', 'FFF PCO (SX/NE-EM)');
INSERT INTO `uaa-core-dev`.technical_employees (ntid, display_name) VALUES ('FXR1KOR', 'SX/ETH Broadcast Team (SX/ETH)');
INSERT INTO `uaa-core-dev`.technical_employees (ntid, display_name) VALUES ('AAZ2KOR', 'DemoBot (BGSW SX/ETL-DL23)');
INSERT INTO `uaa-core-dev`.technical_employees (ntid, display_name) VALUES ('HCU1KOR', 'Healthcare Customer Feedback (SX/EHC)');
INSERT INTO `uaa-core-dev`.technical_employees (ntid, display_name) VALUES ('HGL2KOR', 'BGSW Healthcare Sales (SX/EHC)');
INSERT INTO `uaa-core-dev`.technical_employees (ntid, display_name) VALUES ('XIU2KOR', 'Vivasuite Device Connect Support - BGSW');
INSERT INTO `uaa-core-dev`.technical_employees (ntid, display_name) VALUES ('GFB2KOR', 'Vivasuite Support BGSW (SX/EHC)');
INSERT INTO `uaa-core-dev`.technical_employees (ntid, display_name) VALUES ('DGV1KOR', 'Digital_Pathology Vivascope (SX/EHC)');
INSERT INTO `uaa-core-dev`.technical_employees (ntid, display_name) VALUES ('ENQ5KOR', 'Eyecare Enquiries (RBEI/BUD)');
INSERT INTO `uaa-core-dev`.technical_employees (ntid, display_name) VALUES ('EHC2KOR', 'RBEI EHC-AWS (RBEI/EHC)');
INSERT INTO `uaa-core-dev`.technical_employees (ntid, display_name) VALUES ('EYC1KOR', 'Eyecare Service (SX/EHC)');
INSERT INTO `uaa-core-dev`.technical_employees (ntid, display_name) VALUES ('NBM1KOR', 'NBT Medibilder (SX/EHC2)');
INSERT INTO `uaa-core-dev`.technical_employees (ntid, display_name) VALUES ('LCP5KOR', 'LCPT-05 RBEI-NE1 (SX/EHC BHCS/PAS)');
INSERT INTO `uaa-core-dev`.technical_employees (ntid, display_name) VALUES ('LCR4KOR', 'LCPT-04 RBEI-NE1 (SX/EHC BHCS/PAS)');
INSERT INTO `uaa-core-dev`.technical_employees (ntid, display_name) VALUES ('LCR5KOR', 'LCPT-05 RBEI-NE1 (SX/EHC BHCS/PAS)');
INSERT INTO `uaa-core-dev`.technical_employees (ntid, display_name) VALUES ('LCB1KOR', '-LCTB-22 RBEI-NE1 (SX/EHC BHCS/PAS)');
INSERT INTO `uaa-core-dev`.technical_employees (ntid, display_name) VALUES ('LCP4KOR', 'LCPT-04 RBEI-NE1 (SX/EHC BHCS/PAS)');
INSERT INTO `uaa-core-dev`.technical_employees (ntid, display_name) VALUES ('BWO2KOR', 'BOSCH Vivalytic (SX/EHC3)');
INSERT INTO `uaa-core-dev`.technical_employees (ntid, display_name) VALUES ('BGSA1KOR', 'Healthcare Support BGSW (SX/EHC)');
INSERT INTO `uaa-core-dev`.technical_employees (ntid, display_name) VALUES ('CRS6COB', 'CR-Support Tool Chain (SX/EHC BHCS/PAS)');
INSERT INTO `uaa-core-dev`.technical_employees (ntid, display_name) VALUES ('NER5COB', 'NE1-05CZ RBEI-LCPT (SX/EHC BHCS/PAS)');
INSERT INTO `uaa-core-dev`.technical_employees (ntid, display_name) VALUES ('MMD2KOR', 'Manager MDS (SX/EHC BHCS/PAS)');
INSERT INTO `uaa-core-dev`.technical_employees (ntid, display_name) VALUES ('HEC1BAN', 'Cloudservices Healthcare (SX/EHC)');
INSERT INTO `uaa-core-dev`.technical_employees (ntid, display_name) VALUES ('TESTID', 'CATPT 3847 (SX/BSV32-EA)');

=======================================================================================================================
