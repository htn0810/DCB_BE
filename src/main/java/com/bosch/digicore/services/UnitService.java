package com.bosch.digicore.services;

import com.bosch.digicore.constants.EmployeeType;
import com.bosch.digicore.dtos.CreateUnitDTO;
import com.bosch.digicore.dtos.UnitDTO;
import com.bosch.digicore.dtos.SyncUnitChildrenDTO;
import com.bosch.digicore.dtos.UpdateManagerDTO;
import com.bosch.digicore.entities.Employee;
import com.bosch.digicore.entities.Organization;
import com.bosch.digicore.entities.Unit;
import com.bosch.digicore.exceptions.ResourceNotFoundException;
import com.bosch.digicore.repositories.EmployeeRepository;
import com.bosch.digicore.repositories.UnitRepository;
import com.bosch.digicore.repositories.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UnitService {

    private final UnitRepository unitRepository;
    private final EmployeeRepository employeeRepository;
    private final OrganizationRepository organizationRepository;

    @Transactional(readOnly = true)
    public List<Unit> getAll() {
        return unitRepository.findAllByDeletedIsFalse();
    }

    @Transactional(readOnly = true)
    public List<Unit> getAllByOrgId(final Long orgId) {
        final Organization organization = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "ID", orgId));

        return unitRepository.findAllByOrganizationsInAndDeletedIsFalse(Collections.singletonList(organization));
    }

    @Transactional(readOnly = true)
    public List<Unit> getAllByParentId(final Long parentId) {
        return unitRepository.findAllByParentUnit_IdAndDeletedIsFalse(parentId);
    }

    @Transactional(readOnly = true)
    public List<Unit> getAllByName(final String name) {
        return unitRepository.findAllByNameContainsIgnoreCaseAndDeletedIsFalse(name);
    }

    @Transactional(readOnly = true)
    public Unit getById(final Long id) {
        return unitRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Unit", "ID", id));
    }

    @Transactional(readOnly = true)
    public UnitDTO getParentUnitAndSiblingUnitsBySelfId(final Long groupId) {
        Unit parent = getById(groupId).getParentUnit();
        return new UnitDTO(parent, unitRepository.findAllByParentUnit_IdAndDeletedIsFalse(parent.getId()));
    }

    @Transactional(readOnly = true)
    public List<Unit> getAllParentUnitsByUnitId(final Long unitId) {
        final List<Unit> parents = new ArrayList<>();
        Long currentId = unitId;
        boolean haveParent = true;

        while (haveParent) {
            final Unit parent = getById(currentId).getParentUnit();
            if (parent != null) {
                parents.add(parent);
                currentId = parent.getId();
            }
            haveParent = parent != null;
        }

        return parents;
    }

    @Transactional
    public Unit save(final CreateUnitDTO unitDTO) {
        final Unit unit = new Unit();
        unit.setName(unitDTO.getName());
        unit.setDescription(unitDTO.getDescription());
        // Save Parent Unit's ID
        if (unitDTO.getParentId() != null) {
            unit.setParentUnit(getById(unitDTO.getParentId()));
        }
        // Save Organization's ID
        if (unitDTO.getOrgId() != null) {
            final Organization organization = organizationRepository.findById(unitDTO.getOrgId())
                    .orElseThrow(() -> new ResourceNotFoundException("Organization", "ID", unitDTO.getOrgId()));
            unit.setOrganizations(Collections.singletonList(organization));
        }
        return unitRepository.save(unit);
    }

    @Transactional
    public void syncUnitChildren(final SyncUnitChildrenDTO syncDTO) {
        final List<Unit> units = new ArrayList<>();

        Unit parentUnit = getById(syncDTO.getParentId());

        // Handle new children
        units.addAll(syncDTO.getNewChildren().stream().map(children -> {
            final Unit unit = new Unit();
            unit.setName(children.getName());
            unit.setDescription(children.getDescription());
            unit.setParentUnit(parentUnit);
            return unit;
        }).collect(Collectors.toList()));

        // Handle update children
        units.addAll(syncDTO.getUpdatedChildren().stream().map(children -> {
            final Unit unit = getById(children.getId());
            unit.setName(children.getName());
            unit.setDescription(children.getDescription());
            return unit;
        }).collect(Collectors.toList()));

        // Handle delete children
        syncDTO.getDeletedChildren().forEach(children -> units.add(deleteById(children.getId())));

        parentUnit.setLastSynchronizedSubUnitsDate(Instant.now());
        units.add(parentUnit);

        unitRepository.saveAll(units);
    }

    @Transactional
    public Unit updateUnitManager(final UpdateManagerDTO managerDTO) {
        Unit unit = getById(managerDTO.getUnitId());
        Optional<Employee> optional = employeeRepository.findByNtidAndDeletedIsFalse(managerDTO.getNtid());
        Employee employee;

        if (optional.isPresent()) {
            employee = optional.get();
            List<Unit> units = employee.getUnits();
            Optional<Unit> optionalGroup = units.stream().filter(group1 -> group1.getId().equals(unit.getId())).findFirst();
            if (optionalGroup.isEmpty()) {
                units.add(unit);
                employee.setUnits(units);
            }
        } else {
            employee = new Employee();
            employee.setNtid(managerDTO.getNtid());
            employee.setUnits(Collections.singletonList(unit));
        }

        employee.setEmployeeId(managerDTO.getEmployeeId());
        employee.setDisplayName(managerDTO.getName());
        employee.setEmail(managerDTO.getEmail());
        employee.setType(EmployeeType.INTERNAL);

        unit.setManager(employeeRepository.save(employee));

        return unitRepository.save(unit);
    }

    @Transactional
    public Unit deleteById(Long id) {
        final Unit unit = getById(id);
        final List<Employee> employees = employeeRepository.findAllByUnitsInAndDeletedIsFalse(List.of(unit));
        employees.forEach(employee -> {
            final List<Unit> units = employee.getUnits();
            units.remove(unit);
            employee.setUnits(units);
        });
        employeeRepository.saveAll(employees);

        unit.setDeleted(true);
        return unitRepository.save(unit);
    }
}
