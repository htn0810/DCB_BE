package com.bosch.digicore.dtos;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class EmployeeHistoryDTO {

    private Long id;

    private EmployeeDTO employee;

    private String type;

    private String firstName;

    private String lastName;

    private String nationality;

    private String email;

    private String phoneNumber;

    private String departments;

    private String modifiedBy;

    private Timestamp modifiedDate;
}
