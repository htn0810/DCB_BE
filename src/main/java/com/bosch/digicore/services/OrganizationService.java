package com.bosch.digicore.services;

import com.bosch.digicore.constants.Roles;
import com.bosch.digicore.dtos.CreateUpdateOrgDTO;
import com.bosch.digicore.entities.Asset;
import com.bosch.digicore.entities.Employee;
import com.bosch.digicore.entities.Organization;
import com.bosch.digicore.entities.Unit;
import com.bosch.digicore.exceptions.BadRequestException;
import com.bosch.digicore.exceptions.FailedDependencyException;
import com.bosch.digicore.exceptions.ResourceNotFoundException;
import com.bosch.digicore.repositories.AssetRepository;
import com.bosch.digicore.repositories.EmployeeRepository;
import com.bosch.digicore.repositories.OrganizationRepository;
import com.bosch.digicore.repositories.UnitRepository;
import com.bosch.digicore.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final AssetRepository assetRepository;
    private final UnitRepository unitRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional(readOnly = true)
    public List<Organization> getAll() {
        return organizationRepository.findAllByDeletedIsFalse();
    }

    @Transactional(readOnly = true)
    public List<Organization> getAllByName(final String name) {
        return organizationRepository.findAllByNameContainingIgnoreCaseAndDeletedIsFalse(name);
    }

    @Transactional(readOnly = true)
    public Organization getById(final Long id) {
        final Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Organization.class.getName(), "id", id));
        if (organization.isDeleted()) {
            throw new BadRequestException("This organization has been deleted");
        }
        return organization;
    }

    @Transactional(readOnly = true)
    public Organization getByUuid(final String uuid) {
        return organizationRepository.findByUuidAndDeletedIsFalse(uuid)
                .orElseThrow(() -> new ResourceNotFoundException(Organization.class.getName(), "uuid", uuid));
    }

    @Transactional
    public Organization create(final CreateUpdateOrgDTO organizationDTO) {
        final String uuid = UUID.randomUUID().toString();
        final List<Employee> owners = validateOwnerList(organizationDTO.getOwnerIds());

        final Organization organization = new Organization(uuid, organizationDTO.getName(), organizationDTO.getDescription(),
                organizationDTO.getOrgUrl(), owners);
        final Organization newOrganization = organizationRepository.save(organization);

        /* ASSETS */
        List<Long> assetIds = organizationDTO.getAssetIds();
        if (!assetIds.isEmpty()) {
            List<Asset> assets = assetRepository.findAllByIdIn(assetIds);
            if (!assets.isEmpty()) {
                assets.forEach(asset -> {
                    List<Organization> organizations = asset.getOrganizations();
                    organizations.add(organization);
                    asset.setOrganizations(organizations);
                });
                assetRepository.saveAll(assets);
            }
        }

        /* UNITS */
        List<Long> unitIds = organizationDTO.getUnitIds();
        if (!unitIds.isEmpty()) {
            List<Unit> units = unitRepository.findAllByIdIn(unitIds);
            if (!units.isEmpty()) {
                units.forEach(unit -> {
                    List<Organization> organizations = unit.getOrganizations();
                    organizations.add(organization);
                    unit.setOrganizations(organizations);
                });
                unitRepository.saveAll(units);
            }
        }

        /* EMPLOYEES */
        List<Long> employeesIds = organizationDTO.getEmployeeIds();
        if (!employeesIds.isEmpty()) {
            List<Employee> employees = employeeRepository.findAllByIdIn(employeesIds);
            if (!employees.isEmpty()) {
                employees.forEach(employee -> {
                    List<Organization> organizations = employee.getOrganizations();
                    organizations.add(organization);
                    employee.setOrganizations(organizations);
                });
                employeeRepository.saveAll(employees);
            }
        }

        return newOrganization;
    }

    @Transactional
    public Organization update(final UserPrincipal userPrincipal, final CreateUpdateOrgDTO organizationDTO) {
        final Organization organization = getById(organizationDTO.getId());

        if (isSuperAdminOrOrganizationOwner(userPrincipal, organization)) {
            final List<Employee> owners = validateOwnerList(organizationDTO.getOwnerIds());

            organization.setName(organizationDTO.getName());
            organization.setDescription(organizationDTO.getDescription());
            organization.setOrgUrl(organizationDTO.getOrgUrl());
            organization.setOwners(owners);

            /* ASSETS */
            List<Asset> assets = new ArrayList<>();
            List<Long> checkedAssetId = new ArrayList<>();
            // Case: Removed
            assetRepository.findAllByOrganizationsInAndDeletedIsFalse(List.of(organization)).forEach(asset -> {
                if (!organizationDTO.getAssetIds().contains(asset.getId())) {
                    List<Organization> organizations = asset.getOrganizations().stream()
                            .filter(org -> !org.getId().equals(organization.getId()))   // remove mapping asset_organization
                            .collect(Collectors.toList());
                    asset.setOrganizations(organizations);
                    assets.add(asset);
                } else {
                    checkedAssetId.add(asset.getId());
                }
            });
            // Case: Added
            assetRepository.findAllByIdIn(organizationDTO.getAssetIds()).forEach(asset -> {
                if (!checkedAssetId.contains(asset.getId())) {
                    List<Organization> organizations = asset.getOrganizations();
                    organizations.add(organization);
                    asset.setOrganizations(organizations);
                    assets.add(asset);
                }
            });
            assetRepository.saveAll(assets);

            /* UNITS */
            List<Unit> units = new ArrayList<>();
            List<Long> checkedUnitId = new ArrayList<>();
            // Case: Removed
            unitRepository.findAllByOrganizationsInAndDeletedIsFalse(List.of(organization)).forEach(unit -> {
                if (!organizationDTO.getUnitIds().contains(unit.getId())) {
                    List<Organization> organizations = unit.getOrganizations().stream()
                            .filter(org -> !org.getId().equals(organization.getId()))   // remove mapping unit_organization
                            .collect(Collectors.toList());
                    unit.setOrganizations(organizations);
                    units.add(unit);
                } else {
                    checkedUnitId.add(unit.getId());
                }
            });
            // Case: Added
            unitRepository.findAllByIdIn(organizationDTO.getUnitIds()).forEach(unit -> {
                if (!checkedUnitId.contains(unit.getId())) {
                    List<Organization> organizations = unit.getOrganizations();
                    organizations.add(organization);
                    unit.setOrganizations(organizations);
                    units.add(unit);
                }
            });
            unitRepository.saveAll(units);

            /* EMPLOYEES */
            List<Employee> employees = new ArrayList<>();
            List<Long> checkedEmployeeId = new ArrayList<>();
            // Case: Removed
            employeeRepository.findAllByOrganizationsInAndDeletedIsFalse(List.of(organization)).forEach(employee -> {
                if (!organizationDTO.getEmployeeIds().contains(employee.getId())) {
                    List<Organization> organizations = employee.getOrganizations().stream()
                            .filter(org -> !org.getId().equals(organization.getId()))   // remove mapping employee_organization
                            .collect(Collectors.toList());
                    employee.setOrganizations(organizations);
                    employees.add(employee);
                } else {
                    checkedEmployeeId.add(employee.getId());
                }
            });
            // Case: Added
            employeeRepository.findAllByIdIn(organizationDTO.getEmployeeIds()).forEach(employee -> {
                if (!checkedEmployeeId.contains(employee.getId())) {
                    List<Organization> organizations = employee.getOrganizations();
                    organizations.add(organization);
                    employee.setOrganizations(organizations);
                    employees.add(employee);
                }
            });
            employeeRepository.saveAll(employees);

            return organizationRepository.save(organization);
        }
        throw new BadRequestException("You do not have permission to update this organization");
    }

    @Transactional
    public void deleteById(final UserPrincipal userPrincipal, final Long id) {
        final Organization organization = getById(id);
        if (assetRepository.existsByOrganizationsInAndDeletedIsFalse(Collections.singletonList(organization))) {
            throw new FailedDependencyException("There are assets belong to this Organization.");
        }
        if (isSuperAdminOrOrganizationOwner(userPrincipal, organization)) {
            organization.setDeleted(true);
            organizationRepository.save(organization);
        } else {
            throw new BadRequestException("You do not have permission to delete this organization");
        }
    }

    @Transactional(readOnly = true)
    public long countAll() {
        return organizationRepository.countAllByDeletedIsFalse();
    }

    private List<Employee> validateOwnerList(final List<Long> ownerIds) {
        if (ownerIds.isEmpty()) {
            throw new BadRequestException("Organization must have owners");
        }
        final List<Employee> employees = employeeRepository.findAllByIdIn(ownerIds);
        if (employees.isEmpty()) {
            throw new BadRequestException("IDs are not valid");
        }
        return employees;
    }

    private boolean isSuperAdminOrOrganizationOwner(final UserPrincipal userPrincipal, final Organization organization) {
        if (userPrincipal.getAuthorities().contains(new SimpleGrantedAuthority(Roles.ROLE_SUPER_ADMIN))) {
            return true;
        }
        String ownerNTID = userPrincipal.getUsername().substring(0, 6).toUpperCase();
        return organization.getOwners().stream().map(Employee::getNtid).collect(Collectors.toList()).contains(ownerNTID);
    }
}
