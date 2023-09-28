package com.bosch.digicore.controllers;

import com.bosch.digicore.constants.EmployeeType;
import com.bosch.digicore.dtos.CountEmployeeDTO;
import com.bosch.digicore.dtos.CreateEmployeeDTO;
import com.bosch.digicore.dtos.EmployeeDTO;
import com.bosch.digicore.dtos.SyncEmployeeDTO;
import com.bosch.digicore.entities.Employee;
import com.bosch.digicore.services.EmployeeService;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/employees")
@Tag(name = "Employee APIs", description = "APIs to manage employees")
@Slf4j
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @Operation(summary = "Get all employees by parameters", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content())
    })
    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees(
            @Parameter(description = "Unit's Name") @RequestParam(required = false) List<String> unitName,
            @Parameter(description = "Employee's Name") @RequestParam(required = false) String employeeName,
            @Parameter(description = "Employee's Type") @RequestParam(required = false) List<EmployeeType> employeeTypes,
            @Parameter(description = "If it 'true', employee's unit will be respond")
            @RequestParam(defaultValue = "false") boolean getUnit,
            Pageable pageable
    ) {
        Page<Employee> page;
        if (unitName == null && employeeName == null && employeeTypes == null) {
            log.debug("GET - Get all employees");
            page = employeeService.getAll(pageable);
        } else {
            log.debug("GET - Get all employees by parameters");
            page = employeeService.getAllByParameters(unitName, employeeName, employeeTypes, pageable);
        }
        final HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        final List<EmployeeDTO> list = page.stream().map(employee -> new EmployeeDTO(employee, getUnit)).collect(Collectors.toList());
        return ResponseEntity.ok().headers(headers).body(list);
    }

    @Operation(summary = "Get all employees by unit's ID", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content())
    })
    @GetMapping("/units/{unitId}")
    public ResponseEntity<List<EmployeeDTO>> getAllEmployeesByUnitId(@PathVariable Long unitId, Pageable pageable) {
        log.debug("GET - Get all employees by unit's ID");
        Page<EmployeeDTO> page = employeeService.getAllByUnitId(unitId, pageable).map(EmployeeDTO::new);
        final HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @Operation(summary = "Get all employees by organization's ID", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content())
    })
    @GetMapping("/organizations/{orgId}")
    public ResponseEntity<List<EmployeeDTO>> getAllEmployeesByOrgId(@PathVariable Long orgId, Pageable pageable) {
        log.debug("GET - Get all employees by organization's ID");
        Page<EmployeeDTO> page = employeeService.getAllByOrgId(orgId, pageable).map(EmployeeDTO::new);
        final HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @Operation(summary = "Get an employee by NTID", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content())
    })
    @GetMapping("/{ntid}")
    public ResponseEntity<EmployeeDTO> getEmployeeByNtid(
            @Parameter(description = "Employee's NTID") @PathVariable String ntid,
            @Parameter(description = "If it 'true', employee's unit will be respond") @RequestParam(defaultValue = "false") boolean getUnit
    ) {
        log.debug("GET - Get an employee by NTID");
        return ResponseEntity.ok(new EmployeeDTO(employeeService.getByNtid(ntid), getUnit));
    }

    @Operation(summary = "Get an employee by email", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content())
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<EmployeeDTO> getEmployeeByEmail(
            @Parameter(description = "Employee's Email") @PathVariable String email,
            @Parameter(description = "If it 'true', employee's unit will be respond") @RequestParam(defaultValue = "false") boolean getUnit
    ) {
        log.debug("GET - Get an employee by email");
        return ResponseEntity.ok(new EmployeeDTO(employeeService.getByEmail(email), getUnit));
    }

    @Operation(summary = "Get number of employees", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content())
    })
    @GetMapping("/number")
    public ResponseEntity<CountEmployeeDTO> getNumberOfEmployees(
            @Parameter(description = "Employee's Unit") @RequestParam(required = false) List<String> unitName,
            @Parameter(description = "Employee's Name") @RequestParam(required = false) String employeeName,
            @Parameter(description = "Employee's Type") @RequestParam(required = false) List<EmployeeType> employeeTypes
    ) {
        if (unitName == null && employeeName == null && employeeTypes == null) {
            log.debug("GET - Get number of employees");
            return ResponseEntity.ok(employeeService.countAllActiveEmployees());
        }
        log.debug("GET - Get number of employees by parameter(s)");
        return ResponseEntity.ok(employeeService.countAllEmployeesByParameters(unitName, employeeName, employeeTypes));
    }

    @Operation(summary = "Save an employee", responses = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content()),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content())
    })
    @PostMapping
    public ResponseEntity<EmployeeDTO> saveEmployee(@RequestBody CreateEmployeeDTO employeeDTO) throws URISyntaxException {
        log.debug("POST - Save an employee");
        Employee employee = employeeService.save(employeeDTO);
        return ResponseEntity.created(new URI("/api/employees/" + employee.getId())).body(new EmployeeDTO(employee));
    }

    @Operation(summary = "Synchronize list of employees", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content()),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content())
    })
    @PostMapping("/sync")
    public ResponseEntity<String> syncEmployees(@RequestBody SyncEmployeeDTO employeeDTOs) {
        log.debug("POST - Synchronize list of employees");
        employeeService.syncEmployeesInUnit(employeeDTOs);
        return ResponseEntity.ok("Employees have been synchronized");
    }

    @Operation(summary = "Update employee's avatar", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content()),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content())
    })
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, value = "/avatar")
    public ResponseEntity<EmployeeDTO> updateEmployeeAvatar(
            @Parameter(description = "Employee's NTID") @RequestPart("ntid") String ntid,
            @Parameter(description = "Employee's avatar") @RequestPart(value = "image") MultipartFile image
    ) {
        log.debug("PUT - Update employee's avatar");
        return ResponseEntity.ok(new EmployeeDTO(employeeService.updateEmployeeAvatar(ntid, image)));
    }

    @Operation(summary = "Delete an employee by NTID", responses = {
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content())
    })
    @DeleteMapping("/{ntid}")
    public ResponseEntity<Void> deleteEmployeeByNtid(@PathVariable String ntid) {
        log.debug("DELETE - Delete an employee by NTID");
        employeeService.deleteByNtid(ntid);
        return ResponseEntity.noContent().build();
    }
}
