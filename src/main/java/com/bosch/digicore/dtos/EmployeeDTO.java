package com.bosch.digicore.dtos;

import com.bosch.digicore.constants.EmployeeType;
import com.bosch.digicore.entities.Employee;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class EmployeeDTO {

    private Long id;
    private String ntid;
    private String employeeId;
    private String firstName;
    private String lastName;
    private String displayName;
    private String email;
    private EmployeeType type;
    private String imageUrl;
    private List<UnitDTO> units;
    private List<OrganizationDTO> organizations;

    public EmployeeDTO(final Employee employee) {
        this.id = employee.getId();
        this.ntid = employee.getNtid();
        this.employeeId = employee.getEmployeeId();
        this.firstName = employee.getFirstName();
        this.lastName = employee.getLastName();
        this.displayName = employee.getDisplayName();
        this.email = employee.getEmail();
        this.type = employee.getType();
        this.imageUrl = employee.getImageUrl();
    }

    public EmployeeDTO(final Employee employee, final boolean getUnit) {
        this(employee);
        if (getUnit) {
            this.units = employee.getUnits().stream().map(UnitDTO::new).collect(Collectors.toList());
        }
    }
}
