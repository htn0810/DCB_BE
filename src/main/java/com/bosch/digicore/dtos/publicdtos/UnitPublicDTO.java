package com.bosch.digicore.dtos.publicdtos;

import com.bosch.digicore.entities.Employee;
import com.bosch.digicore.entities.Unit;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UnitPublicDTO {

    private Long id;

    private String name;

    private String description;

    private Manager manager;

    public UnitPublicDTO(final Unit unit) {
        this.id = unit.getId();
        this.name = unit.getName();
        this.description = unit.getDescription();
        this.manager = unit.getManager() != null ? new Manager(unit.getManager()) : null;
    }

    @Data
    public static class Manager {
        private String ntid;
        private String fullName;
        private String email;

        public Manager(final Employee employee) {
            this.ntid = employee.getNtid();
            this.fullName = employee.getDisplayName();
            this.email = employee.getEmail();
        }
    }
}
