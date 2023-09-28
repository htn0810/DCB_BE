package com.bosch.digicore.dtos.publicdtos;

import com.bosch.digicore.entities.Employee;
import com.bosch.digicore.entities.Organization;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class OrganizationPublicDTO {

    private Long id;
    private String uuid;
    private String name;
    private String description;
    private String orgUrl;
    private List<Owner> owners;

    public OrganizationPublicDTO(final Organization organization) {
        this.id = organization.getId();
        this.uuid = organization.getUuid();
        this.name = organization.getName();
        this.description = organization.getDescription();
        this.orgUrl = organization.getOrgUrl();
        this.owners = organization.getOwners().stream().map(Owner::new).collect(Collectors.toList());
    }

    @Data
    static class Owner {
        private String ntid;
        private String fullName;
        private String email;

        public Owner(final Employee employee) {
            this.ntid = employee.getNtid();
            this.fullName = employee.getDisplayName();
            this.email = employee.getEmail();
        }
    }
}
