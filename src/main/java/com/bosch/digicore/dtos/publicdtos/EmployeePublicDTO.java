package com.bosch.digicore.dtos.publicdtos;

import com.bosch.digicore.constants.EmployeeType;
import com.bosch.digicore.entities.Employee;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmployeePublicDTO {

    private String ntid;
    private String displayName;
    private String email;
    private EmployeeType type;
    private String imageUrl;

    public EmployeePublicDTO(final Employee employee) {
        this.ntid = employee.getNtid();
        this.displayName = employee.getDisplayName();
        this.email = employee.getEmail();
        this.type = employee.getType();
        this.imageUrl = employee.getImageUrl();
    }
}
