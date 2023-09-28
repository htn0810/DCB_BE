package com.bosch.digicore.dtos;

import com.bosch.digicore.constants.EmployeeType;
import com.bosch.digicore.entities.Employee;
import lombok.Data;

@Data
public class CreateEmployeeDTO {

    private String ntid;
    private String firstName;
    private String lastName;
    private String displayName;
    private String email;
    private EmployeeType employeeType;

    public Employee convertToEmployee() {
        Employee employee = new Employee();
        employee.setNtid(this.ntid);
        employee.setFirstName(this.firstName);
        employee.setLastName(this.lastName);
        employee.setDisplayName(this.displayName);
        employee.setEmail(this.email);
        employee.setType(this.employeeType);
        return employee;
    }
}
