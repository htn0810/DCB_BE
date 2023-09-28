package com.bosch.digicore.dtos;

import com.bosch.digicore.entities.Employee;
import com.bosch.digicore.entities.Unit;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class UnitDTO {

    private Long id;

    private String name;

    private String description;

    private Manager manager;

    private List<UnitDTO> children;

    private Instant lastSynchronizedSubUnitsDate;

    private Instant lastSynchronizedEmployeesDate;

    private Integer totalEmployeesOfEachLevel;

    private Integer fixedtermEmployeesCount;

    private Integer externalEmployeesCount;

    private Integer internalEmployeesCount;

    public UnitDTO(final Unit unit) {
        this.id = unit.getId();
        this.name = unit.getName();
        this.description = unit.getDescription();
        this.manager = unit.getManager() != null ? new Manager(unit.getManager()) : null;
        this.lastSynchronizedSubUnitsDate = unit.getLastSynchronizedSubUnitsDate();
        this.lastSynchronizedEmployeesDate = unit.getLastSynchronizedEmployeesDate();
        this.totalEmployeesOfEachLevel = unit.getTotalEmployeesOfEachLevel();
        this.fixedtermEmployeesCount = unit.getFixedtermEmployeesCount();
        this.externalEmployeesCount = unit.getExternalEmployeesCount();
        this.internalEmployeesCount = unit.getInternalEmployeesCount();
    }

    public UnitDTO(final Unit unit, final List<Unit> children) {
        this(unit);
        this.children = children.stream().map(UnitDTO::new).collect(Collectors.toList());
    }

    @Data
    @NoArgsConstructor
    public static class Manager {
        private String ntid;
        private String employeeId;
        private String fullName;
        private String email;

        public Manager(final Employee employee) {
            this.ntid = employee.getNtid();
            this.employeeId = employee.getEmployeeId();
            this.fullName = employee.getDisplayName();
            this.email = employee.getEmail();
        }
    }
}
