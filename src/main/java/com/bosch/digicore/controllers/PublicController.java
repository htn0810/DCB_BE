package com.bosch.digicore.controllers;

import com.bosch.digicore.dtos.OrganizationDTO;
import com.bosch.digicore.dtos.publicdtos.EmployeePublicDTO;
import com.bosch.digicore.dtos.publicdtos.OrganizationPublicDTO;
import com.bosch.digicore.dtos.publicdtos.StatisticDTO;
import com.bosch.digicore.dtos.publicdtos.UnitPublicDTO;
import com.bosch.digicore.entities.Employee;
import com.bosch.digicore.services.AssetService;
import com.bosch.digicore.services.EmployeeService;
import com.bosch.digicore.services.OrganizationService;
import com.bosch.digicore.services.UnitService;
import com.bosch.digicore.utils.ControllerUtil;
import com.bosch.digicore.utils.PaginationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public")
@Tag(name = "Public APIs", description = "Public APIs won't need authentication")
@Slf4j
@RequiredArgsConstructor
public class PublicController {

    private final OrganizationService organizationService;
    private final AssetService assetService;
    private final UnitService unitService;
    private final EmployeeService employeeService;

    @Operation(summary = "Get all public organizations", responses = {
            @ApiResponse(responseCode = "200", description = "OK")
    })
    @GetMapping("/organizations")
    public ResponseEntity<List<OrganizationPublicDTO>> getAllOrganizations(
            @Parameter(description = "Organization's Name") @RequestParam(required = false) String name) {
        log.debug("GET - Get all public organizations");
        final List<OrganizationPublicDTO> organizationDTOS = (name != null ? organizationService.getAllByName(name) : organizationService.getAll())
                .stream().map(OrganizationPublicDTO::new).collect(Collectors.toList());
        return ResponseEntity.ok(organizationDTOS);
    }

    @Operation(summary = "Get all public units (by parameters). Only accept 1 parameter", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
    })
    @GetMapping
    public ResponseEntity<List<UnitPublicDTO>> getAllUnits(
            @Parameter(description = "Organization's ID") @RequestParam(required = false) Long orgId,
            @Parameter(description = "Unit's Name") @RequestParam(required = false) String name
    ) {
        log.debug("GET - Get all public units");
        ControllerUtil.checkParametersWhenOnlyNeedOne(orgId, name);
        if (orgId != null) {
            return ResponseEntity.ok(unitService.getAllByOrgId(orgId).stream().map(UnitPublicDTO::new).collect(Collectors.toList()));
        }
        if (name != null) {
            return ResponseEntity.ok(unitService.getAllByName(name).stream().map(UnitPublicDTO::new).collect(Collectors.toList()));
        }
        return ResponseEntity.ok(unitService.getAll().stream().map(UnitPublicDTO::new).collect(Collectors.toList()));
    }

    @Operation(summary = "Get all public employees by parameters", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
    })
    @GetMapping("/employees")
    public ResponseEntity<List<EmployeePublicDTO>> getAllEmployees(
            @Parameter(description = "Unit's Name") @RequestParam(required = false) List<String> unitName,
            @Parameter(description = "Employee's Name") @RequestParam(required = false) String employeeName,
            Pageable pageable
    ) {
        Page<Employee> page;
        if (unitName == null && employeeName == null) {
            log.debug("GET - Get all public employees");
            page = employeeService.getAll(pageable);
        } else {
            log.debug("GET - Get all public employees by parameters");
            page = employeeService.getAllByParameters(unitName, employeeName, null, pageable);
        }
        final HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        final List<EmployeePublicDTO> list = page.stream().map(EmployeePublicDTO::new).collect(Collectors.toList());
        return ResponseEntity.ok().headers(headers).body(list);
    }

    @Operation(summary = "Get an public employee by NTID", responses = {
            @ApiResponse(responseCode = "200", description = "OK")
    })
    @GetMapping("/employees/{ntid}")
    public ResponseEntity<EmployeePublicDTO> getEmployeeByNtid(
            @Parameter(description = "Employee's NTID") @PathVariable String ntid
    ) {
        log.debug("GET - Get an public employee by NTID");
        return ResponseEntity.ok(new EmployeePublicDTO(employeeService.getByNtid(ntid)));
    }

    @Operation(summary = "Get an public employee by email", responses = {
            @ApiResponse(responseCode = "200", description = "OK")
    })
    @GetMapping("/employees/email/{email}")
    public ResponseEntity<EmployeePublicDTO> getEmployeeByEmail(
            @Parameter(description = "Employee's Email") @PathVariable String email
    ) {
        log.debug("GET - Get an public employee by email");
        return ResponseEntity.ok(new EmployeePublicDTO(employeeService.getByEmail(email)));
    }

    @Operation(summary = "Get statistic number", responses = {
            @ApiResponse(responseCode = "200", description = "OK")
    })
    @GetMapping("/statistics")
    public ResponseEntity<StatisticDTO> getStatistics() {
        log.debug("GET - Get statistic number");
        final StatisticDTO statisticDTO = new StatisticDTO(organizationService.countAll(), assetService.countAll(),
                employeeService.countAll());
        return ResponseEntity.ok(statisticDTO);
    }

    @Operation(summary = "Get organization by UUID", responses = {
            @ApiResponse(responseCode = "200", description = "OK")
    })
    @GetMapping("/organizations/{uuid}")
    public ResponseEntity<OrganizationPublicDTO> getOrganizationByUuid(@PathVariable String uuid) {
        log.debug("GET - Get organization by UUID {}", uuid);
        return ResponseEntity.ok(new OrganizationPublicDTO(organizationService.getByUuid(uuid)));
    }
}
