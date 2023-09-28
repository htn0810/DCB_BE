package com.bosch.digicore.dtos;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class SyncEmployeeDTO {

    private Long groupId;
    private List<EmployeeDTO> newEmployees;
    private List<EmployeeDTO> updatedEmployees;
    private List<EmployeeDTO> deletedEmployees;

    @Data
    public static class EmployeeDTO {
        private String ntid;
        private String employeeId;
        private String firstName;
        private String lastName;
        private String displayName;
        private String email;
        private String corporate;
        private String imageUrl;
        private Instant createdDate;
        private Instant lastModifiedDate;
    }
}
