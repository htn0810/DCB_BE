package com.bosch.digicore.dtos;

import com.bosch.digicore.entities.Organization;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class OrganizationDTO {

    private Long id;
    private String name;
    private String description;
    private String orgUrl;
    private List<EmployeeDTO> owners;

    public OrganizationDTO(final Organization organization) {
        this.id = organization.getId();
        this.name = organization.getName();
        this.description = organization.getDescription();
        this.orgUrl = organization.getOrgUrl();
        this.owners = organization.getOwners().stream().map(EmployeeDTO::new).collect(Collectors.toList());
    }
}
