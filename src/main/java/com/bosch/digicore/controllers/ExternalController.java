package com.bosch.digicore.controllers;

import com.bosch.digicore.clients.models.CoreSearchRes;
import com.bosch.digicore.dtos.LdapUserDTO;
import com.bosch.digicore.dtos.OrgManagerDTO;
import com.bosch.digicore.exceptions.BadRequestException;
import com.bosch.digicore.services.ExternalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/external")
@Tag(name = "External APIs", description = "APIs to get data from external servers")
@Slf4j
@RequiredArgsConstructor
public class ExternalController {

    private final ExternalService externalService;

    @Operation(summary = "Get an organization from Org Manager Server by orgName", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content()),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content())
    })
    @GetMapping("/org-manager")
    public ResponseEntity<OrgManagerDTO> getOrganizationByOrgName(@Parameter(description = "Organization's name") @RequestParam String orgName) {
        log.debug("GET - Get organization by orgName");
        return ResponseEntity.ok(externalService.getOrganizationByOrgName(orgName));
    }

    @Operation(summary = "Get all employees from LDAP Server by orgName or NTID. Only accept 1 parameter", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content()),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content()),
            @ApiResponse(responseCode = "408", description = "Request Timeout", content = @Content())
    })
    @GetMapping("/ldap")
    public ResponseEntity<List<LdapUserDTO>> getAllEmployeesByOrgName(
            @Parameter(description = "Organization's name") @RequestParam(required = false) String orgName,
            @Parameter(description = "NTID") @RequestParam(required = false) String ntid,
            @Parameter(description = "Email") @RequestParam(required = false) String email
    ) {
        if (orgName != null && ntid != null && email != null) {
            throw new BadRequestException("Only accept 1 parameter");
        }
        if (orgName != null) {
            log.debug("GET - Get all employees by orgName");
            return ResponseEntity.ok(externalService.getAllEmployeesByOrgName(orgName));
        }
        if (ntid != null) {
            log.debug("GET - Get an employee by NTID");
            return ResponseEntity.ok(externalService.getEmployeeByNtid(ntid));
        }
        if (email != null) {
            log.debug("GET - Get an employee by email");
            return ResponseEntity.ok(externalService.getEmployeeByEmail(email));
        }
        throw new BadRequestException("Need at least 1 parameter");
    }

    @Operation(summary = "Get results by core search engine", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content()),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/core-search")
    public ResponseEntity<CoreSearchRes> getDataSearch(@Parameter(description = "Keyword Query") @RequestParam String text) {
        log.debug("GET - Get results by text");
        return ResponseEntity.ok(externalService.getDataSearch(text));
    }
}
