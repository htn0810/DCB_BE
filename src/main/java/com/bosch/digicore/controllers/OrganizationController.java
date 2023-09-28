package com.bosch.digicore.controllers;

import com.bosch.digicore.constants.Roles;
import com.bosch.digicore.dtos.CreateUpdateOrgDTO;
import com.bosch.digicore.dtos.OrganizationDTO;
import com.bosch.digicore.entities.Organization;
import com.bosch.digicore.security.UserPrincipal;
import com.bosch.digicore.services.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/organizations")
@Tag(name = "Organization APIs", description = "APIs to manage organizations")
@Slf4j
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @Operation(summary = "Get all organizations", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content())
    })
    @GetMapping
    public ResponseEntity<List<OrganizationDTO>> getAllOrganizations(
            @Parameter(description = "Organization's Name") @RequestParam(required = false) String name) {
        log.debug("GET - Get all organizations");
        final List<OrganizationDTO> organizationDTOS = (name != null ? organizationService.getAllByName(name) : organizationService.getAll())
                .stream().map(OrganizationDTO::new).collect(Collectors.toList());
        return ResponseEntity.ok(organizationDTOS);
    }

    @Operation(summary = "Get organization by UUID", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content())
    })
    @GetMapping("/uuid/{uuid}")
    public ResponseEntity<OrganizationDTO> getOrganizationByUuid(@PathVariable String uuid) {
        log.debug("GET - Get organization by UUID {}", uuid);
        return ResponseEntity.ok(new OrganizationDTO(organizationService.getByUuid(uuid)));
    }

    @Operation(summary = "Get organization by ID", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content())
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrganizationDTO> getOrganizationById(@PathVariable Long id) {
        log.debug("GET - Get organization by id {}", id);
        return ResponseEntity.ok(new OrganizationDTO(organizationService.getById(id)));
    }

    @Operation(summary = "Create an organization. Need role: " + Roles.ROLE_SUPER_ADMIN, responses = {
            @ApiResponse(responseCode = "201", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content()),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content())
    })
    @PostMapping
    @PreAuthorize(Roles.HAS_ROLE_SUPER_ADMIN)
    public ResponseEntity<OrganizationDTO> createOrganization(@RequestBody @Valid CreateUpdateOrgDTO organizationDTO)
            throws URISyntaxException {
        log.debug("POST - Create an organization {}", organizationDTO.getName());
        final Organization organization = organizationService.create(organizationDTO);
        return ResponseEntity.created(new URI("/api/organizations/" + organization.getId())).body(new OrganizationDTO(organization));
    }

    @Operation(summary = "Update an organization. Need role: " + Roles.ROLE_SUPER_ADMIN + " or " + Roles.ROLE_ORG_ADMIN, responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content()),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content())
    })
    @PutMapping
    @PreAuthorize(Roles.HAS_ROLE_SUPER_ADMIN_OR_ORG_ADMIN)
    public ResponseEntity<OrganizationDTO> updateOrganization(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                              @RequestBody @Valid CreateUpdateOrgDTO organizationDTO) {
        log.debug("PUT - Update an organization : {}", organizationDTO.getId());
        return ResponseEntity.ok(new OrganizationDTO(organizationService.update(userPrincipal, organizationDTO)));
    }

    @Operation(summary = "Delete organization by ID. Need role: " + Roles.ROLE_SUPER_ADMIN + " or " + Roles.ROLE_ORG_ADMIN, responses = {
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content()),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content()),
            @ApiResponse(responseCode = "424", description = "There are assets belong to this Organization", content = @Content())
    })
    @DeleteMapping("/{id}")
    @PreAuthorize(Roles.HAS_ROLE_SUPER_ADMIN_OR_ORG_ADMIN)
    public ResponseEntity<Void> deleteOrganizationById(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                       @PathVariable Long id) {
        log.debug("DELETE - Delete organization by id {}", id);
        organizationService.deleteById(userPrincipal, id);
        return ResponseEntity.noContent().build();
    }
}
