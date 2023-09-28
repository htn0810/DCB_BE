package com.bosch.digicore.dtos;

import com.bosch.digicore.entities.Organization;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class GroupHistoryDTO {

    private Long id;

    private UnitDTO group;

    private String type;

    private String manager;

    private Long numberOfEmployees;

    private String modifiedBy;

    private Timestamp modifiedDate;

    private Organization org;

}
