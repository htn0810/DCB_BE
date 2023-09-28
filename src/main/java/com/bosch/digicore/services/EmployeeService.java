package com.bosch.digicore.services;

import com.bosch.digicore.constants.EmployeeType;
import com.bosch.digicore.controllers.ImageController;
import com.bosch.digicore.dtos.CountEmployeeDTO;
import com.bosch.digicore.dtos.CreateEmployeeDTO;
import com.bosch.digicore.dtos.SyncEmployeeDTO;
import com.bosch.digicore.entities.Employee;
import com.bosch.digicore.entities.Organization;
import com.bosch.digicore.entities.Unit;
import com.bosch.digicore.exceptions.ResourceNotFoundException;
import com.bosch.digicore.repositories.EmployeeRepository;
import com.bosch.digicore.repositories.OrganizationRepository;
import com.bosch.digicore.repositories.UnitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeService {

    private static final String ENTITY_NAME = "Employee";

    private final EmployeeRepository employeeRepository;
    private final UnitRepository unitRepository;
    private final OrganizationRepository organizationRepository;
    private final ImageService imageService;
    private final ExternalService externalService;

    @Transactional(readOnly = true)
    public Page<Employee> getAll(Pageable pageable) {
        return employeeRepository.findAllByDeletedIsFalse(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Employee> getAllByParameters(List<String> unitName, String employeeName, List<EmployeeType> employeeTypes, Pageable pageable) {
        Page<Employee> page;

        if (unitName != null) {
            List<Unit> parentUnits = unitRepository.findAllByNameIn(unitName);
            List<Unit> units = new ArrayList<>();
            parentUnits.forEach(parentUnit -> {
                units.addAll(unitRepository.findUnitAndAllChildrenUnitsByUnitId(parentUnit.getId()));
            });

            if (employeeName != null && employeeTypes != null) {
                // GROUP_NAME + EMPLOYEE_NAME + EMPLOYEE_TYPE
                page = employeeRepository.findDistinctByUnitsInAndDisplayNameContainsIgnoreCaseAndTypeInAndDeletedIsFalse(
                        units, employeeName, employeeTypes, pageable);
            } else if (employeeName != null) {
                // GROUP_NAME + EMPLOYEE_NAME
                page = employeeRepository.findDistinctByUnitsInAndDisplayNameContainsIgnoreCaseAndDeletedIsFalse(units, employeeName, pageable);
            } else if (employeeTypes != null) {
                // GROUP_NAME + EMPLOYEE_TYPE
                page = employeeRepository.findDistinctByUnitsInAndTypeInAndDeletedIsFalse(units, employeeTypes, pageable);
            } else {
                // GROUP_NAME
                page = employeeRepository.findDistinctByUnitsInAndDeletedIsFalse(units, pageable);
            }
        } else {
            if (employeeName != null && employeeTypes != null) {
                // EMPLOYEE_NAME + EMPLOYEE_TYPE
                page = employeeRepository.findAllByDisplayNameContainsIgnoreCaseAndTypeInAndDeletedIsFalse(employeeName, employeeTypes, pageable);
            } else if (employeeName != null) {
                // EMPLOYEE_NAME
                page = employeeRepository.findAllByDisplayNameContainsIgnoreCaseAndDeletedIsFalse(employeeName, pageable);
            } else {
                // EMPLOYEE_TYPE
                page = employeeRepository.findAllByTypeInAndDeletedIsFalse(employeeTypes, pageable);
            }
        }

        return page;
    }

    @Transactional(readOnly = true)
    public Page<Employee> getAllByUnitId(final Long unitId, Pageable pageable) {
        final Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new ResourceNotFoundException(Unit.class.getName(), "ID", unitId));
        return employeeRepository.findDistinctByUnitsInAndDeletedIsFalse(List.of(unit), pageable);
    }

    @Transactional(readOnly = true)
    public Page<Employee> getAllByOrgId(final Long orgId, Pageable pageable) {
        final Organization organization = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException(Unit.class.getName(), "ID", orgId));
        return employeeRepository.findDistinctByOrganizationsInAndDeletedIsFalse(List.of(organization), pageable);
    }

    @Transactional(readOnly = true)
    public Employee getByNtid(final String ntid) {
        return employeeRepository.findByNtidAndDeletedIsFalse(ntid).orElseThrow(() -> new ResourceNotFoundException(ENTITY_NAME, "ntid", ntid));
    }

    @Transactional(readOnly = true)
    public Employee getByEmail(final String email) {
        return employeeRepository.findByEmailAndDeletedIsFalse(email).orElseThrow(() -> new ResourceNotFoundException(ENTITY_NAME, "email", email));
    }

    public Employee save(final CreateEmployeeDTO employeeDTO) {
        return employeeRepository.save(employeeDTO.convertToEmployee());
    }

    @Transactional
    public Employee updateEmployeeAvatar(final String ntid, final MultipartFile image) {
        final Employee employee = getByNtid(ntid);

        if (image != null) {
            if (employee.getImageUrl() != null) {
                String oldImageId = employee.getImageUrl().substring(employee.getImageUrl().lastIndexOf("/") + 1);
                imageService.deleteById(oldImageId);
            }
            String imageId = imageService.save(image).getId();
            String imageUrl = WebMvcLinkBuilder.linkTo(methodOn(ImageController.class).getImageById(imageId)).toString();
            employee.setImageUrl(imageUrl);
        }

        return employeeRepository.save(employee);
    }

    @Transactional
    public void syncEmployeesInUnit(final SyncEmployeeDTO employeeDTOs) {
        final Unit unit = unitRepository.findById(employeeDTOs.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException(Unit.class.getName(), "id", employeeDTOs.getGroupId()));
        final List<Employee> employees = new ArrayList<>();

        // Handle new employees
        employeeDTOs.getNewEmployees().forEach(employeeDTO -> {
            final Optional<Employee> optional = employeeRepository.findByNtid(employeeDTO.getNtid());
            Employee employee;
            if (optional.isPresent()) {
                // In case employee existed in DB
                employee = syncEmployeeData(employeeDTO, optional.get());
                final List<Unit> units = employee.getUnits();
                if (units.stream().filter(group1 -> group1.getId().equals(unit.getId())).findFirst().isEmpty()) {
                    units.add(unit);
                    employee.setUnits(units);
                }
            } else {
                employee = syncEmployeeData(employeeDTO, new Employee());
                employee.setUnits(List.of(unit));
            }
            employees.add(employee);
        });

        // Handle updated employees
        employeeDTOs.getUpdatedEmployees().forEach(employeeDTO ->
                        employeeRepository.findByNtid(employeeDTO.getNtid()).ifPresent(employee ->
                                employees.add(syncEmployeeData(employeeDTO, employee)))
        );

        // Handle remove employees
        employeeDTOs.getDeletedEmployees().forEach(employeeDTO ->
                        employeeRepository.findByNtid(employeeDTO.getNtid()).ifPresent(employee -> {
                            // remove that employee out of group
                            employee.getUnits().removeIf(group1 -> group1.getId().equals(unit.getId()));
                            employees.add(employee);
                        })
        );

        // Handling to set date of employee synchronize
        unit.setLastSynchronizedEmployeesDate(Instant.now());

        unitRepository.saveAndFlush(unit);
        employeeRepository.saveAllAndFlush(new ArrayList<>(employees));

        if (!employeeDTOs.getDeletedEmployees().isEmpty()) {
            new Thread(() -> {
                int numberOfCompletelyDeletedEmployees = employeeRepository.updateInactiveStatusForDeletedEmployeesInLDAP(
                        externalService.getDeletedEmployeesFromLDAP(
                                employeeDTOs.getDeletedEmployees()
                                            .stream()
                                            .map(SyncEmployeeDTO.EmployeeDTO::getNtid)
                                            .collect(Collectors.toList())
                        )
                );

                log.info("Completely deleted employees - Result in Database:  " + numberOfCompletelyDeletedEmployees + " row affected.");
            }).start();
        }
        new Thread(this::countEmployeesAfterSyncData).start();
    }

    @Transactional
    public void deleteByNtid(String ntid) {
        final Employee employee = employeeRepository.findByNtidAndDeletedIsFalse(ntid)
                .orElseThrow(() -> new ResourceNotFoundException(ENTITY_NAME, "ntid", ntid));
        employee.setDeleted(true);
        employeeRepository.save(employee);

        new Thread(this::countEmployeesAfterSyncData).start();
    }

    @Transactional
    public CountEmployeeDTO countAllActiveEmployees() { //count default with non-parameter on first-visited Employee Mgt page
                                                        //result used by StatisticComponent func in Front-end
        return new CountEmployeeDTO(employeeRepository.countAllByTypeAndDeletedIsFalse(EmployeeType.INTERNAL),
                                    employeeRepository.countAllByTypeAndDeletedIsFalse(EmployeeType.EXTERNAL),
                                    employeeRepository.countAllByTypeAndDeletedIsFalse(EmployeeType.FIXED_TERM)
        );
    }

    @Transactional
    public CountEmployeeDTO countAllEmployeesByParameters(final List<String> unitName, final String employeeName,
                                                          final List<EmployeeType> employeeTypes) {
        final Map<EmployeeType, Long> countMap = new EnumMap<>(EmployeeType.class);

        if (unitName != null) {
            List<Unit> parentUnits = unitRepository.findAllByNameIn(unitName);
            List<Unit> units = new ArrayList<>();
            parentUnits.forEach(parentUnit -> {
                units.addAll(unitRepository.findUnitAndAllChildrenUnitsByUnitId(parentUnit.getId()));
            });

            if (employeeName != null && employeeTypes != null) {
                // GROUP_NAME + EMPLOYEE_NAME + EMPLOYEE_TYPE
                employeeTypes.forEach(employeeType -> countMap.put(employeeType,
                        employeeRepository.countDistinctByUnitsInAndDisplayNameContainsIgnoreCaseAndTypeAndDeletedIsFalse(
                                units, employeeName, employeeType))
                );
            } else if (employeeName != null) {
                // GROUP_NAME + EMPLOYEE_NAME
                countMap.put(EmployeeType.INTERNAL,
                             employeeRepository.countDistinctByUnitsInAndDisplayNameContainsIgnoreCaseAndTypeAndDeletedIsFalse(
                                     units, employeeName, EmployeeType.INTERNAL)
                );
                countMap.put(EmployeeType.EXTERNAL,
                             employeeRepository.countDistinctByUnitsInAndDisplayNameContainsIgnoreCaseAndTypeAndDeletedIsFalse(
                                     units, employeeName, EmployeeType.EXTERNAL)
                );
                countMap.put(EmployeeType.FIXED_TERM,
                             employeeRepository.countDistinctByUnitsInAndDisplayNameContainsIgnoreCaseAndTypeAndDeletedIsFalse(
                                     units, employeeName, EmployeeType.FIXED_TERM)
                );
            } else if (employeeTypes != null) {
                // GROUP_NAME + EMPLOYEE_TYPE
                employeeTypes.forEach(employeeType ->
                        countMap.put(employeeType, employeeRepository.countDistinctByUnitsInAndTypeAndDeletedIsFalse(units, employeeType))
                );
            } else {
                // GROUP_NAME
                countMap.put(EmployeeType.INTERNAL,
                             employeeRepository.countDistinctByUnitsInAndTypeAndDeletedIsFalse(units, EmployeeType.INTERNAL));
                countMap.put(EmployeeType.EXTERNAL,
                             employeeRepository.countDistinctByUnitsInAndTypeAndDeletedIsFalse(units, EmployeeType.EXTERNAL));
                countMap.put(EmployeeType.FIXED_TERM,
                             employeeRepository.countDistinctByUnitsInAndTypeAndDeletedIsFalse(units, EmployeeType.FIXED_TERM)
                );
            }
        } else {
            if (employeeName != null && employeeTypes != null) {
                // EMPLOYEE_NAME + EMPLOYEE_TYPE
                employeeTypes.forEach(employeeType -> countMap.put(employeeType,
                        employeeRepository.countAllByDisplayNameContainsIgnoreCaseAndTypeAndDeletedIsFalse(employeeName, employeeType))
                );
            } else if (employeeName != null) {
                // EMPLOYEE_NAME
                countMap.put(EmployeeType.INTERNAL,
                             employeeRepository.countAllByDisplayNameContainsIgnoreCaseAndTypeAndDeletedIsFalse(employeeName, EmployeeType.INTERNAL));
                countMap.put(EmployeeType.EXTERNAL,
                             employeeRepository.countAllByDisplayNameContainsIgnoreCaseAndTypeAndDeletedIsFalse(employeeName, EmployeeType.EXTERNAL));
                countMap.put(EmployeeType.FIXED_TERM,
                             employeeRepository.countAllByDisplayNameContainsIgnoreCaseAndTypeAndDeletedIsFalse(employeeName, EmployeeType.FIXED_TERM));
            } else {
                // EMPLOYEE_TYPE
                employeeTypes.forEach(employeeType -> countMap.put(employeeType, employeeRepository.countAllByTypeAndDeletedIsFalse(employeeType)));
            }
        }

        return new CountEmployeeDTO(countMap);
    }

    @Transactional
    public void countEmployeesAfterSyncData() {
        Set<Long> employeeIds = new HashSet<>();
        List<Unit> units = unitRepository.findAllByDeletedIsFalse();
        units.forEach(group -> {
            group.setTotalEmployeesOfEachLevel(countTotalEmployeesInUnitAndSubUnits(group, employeeIds, null));
            employeeIds.clear();
        });
        unitRepository.saveAll(units);
    }

    @Transactional(readOnly = true)
    public long countAll() {
        return employeeRepository.countAllByDeletedIsFalse();
    }

    public int countTotalEmployeesInUnitAndSubUnits(final Unit unit, final Set<Long> employeeIds, final EmployeeType employeeType) {

        employeeIds.addAll(
                employeeRepository.findAllByUnitsInAndDeletedIsFalse(List.of(unit))
                                  .stream()
                                  .filter(employee -> employeeType.equals(employee.getType()))
                                  .map(Employee::getId)
                                  .collect(Collectors.toList())
        );

        unitRepository.findAllByParentUnitAndDeletedIsFalse(unit).forEach(children -> countTotalEmployeesInUnitAndSubUnits(children, employeeIds, employeeType));


        return employeeIds.size();
    }

    private Employee syncEmployeeData(SyncEmployeeDTO.EmployeeDTO employeeDTO, Employee employeeToUpdate) {
        employeeToUpdate.setNtid(employeeDTO.getNtid());
        employeeToUpdate.setEmployeeId(employeeDTO.getEmployeeId());
        employeeToUpdate.setFirstName(employeeDTO.getFirstName());
        employeeToUpdate.setLastName(employeeDTO.getLastName());
        employeeToUpdate.setDisplayName(employeeDTO.getDisplayName());
        employeeToUpdate.setEmail(employeeDTO.getEmail());
        employeeToUpdate.setLdapCreatedDate(employeeDTO.getCreatedDate());
        employeeToUpdate.setLdapLastModifiedDate(employeeDTO.getLastModifiedDate());
        employeeToUpdate.setLdapSynchronized(true);
        employeeToUpdate.setDeleted(false);

        EmployeeType type = EmployeeType.INTERNAL;
        if (employeeDTO.getDisplayName() != null) {
            if (employeeDTO.getDisplayName().contains("FIXED-TERM")) {
                type = EmployeeType.FIXED_TERM;
            }
            if (employeeDTO.getDisplayName().contains("EXTERNAL")) {
                type = EmployeeType.EXTERNAL;
            }
        }
        employeeToUpdate.setType(type);

        return employeeToUpdate;
    }
}