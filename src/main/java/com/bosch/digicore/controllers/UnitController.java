package com.bosch.digicore.controllers;

import com.bosch.digicore.dtos.CreateUnitDTO;
import com.bosch.digicore.dtos.UnitDTO;
import com.bosch.digicore.dtos.SyncUnitChildrenDTO;
import com.bosch.digicore.dtos.UpdateManagerDTO;
import com.bosch.digicore.entities.Unit;
import com.bosch.digicore.services.UnitService;
import com.bosch.digicore.utils.ControllerUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/units")
@Tag(name = "Unit APIs", description = "APIs to manage units")
@Slf4j
@RequiredArgsConstructor
public class UnitController {

    private final UnitService unitService;

    @Operation(summary = "Get all units (by parameters). Only accept 1 parameter", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content())
    })
    @GetMapping
    public ResponseEntity<List<UnitDTO>> getAllUnits(
            @Parameter(description = "Organization's ID") @RequestParam(required = false) Long orgId,
            @Parameter(description = "Parent Unit's ID") @RequestParam(required = false) Long parentId,
            @Parameter(description = "Unit's Name") @RequestParam(required = false) String name
    ) {
        log.debug("GET - Get all units");
        ControllerUtil.checkParametersWhenOnlyNeedOne(orgId, parentId, name);
        if (orgId != null) {
            return ResponseEntity.ok(unitService.getAllByOrgId(orgId).stream().map(UnitDTO::new).collect(Collectors.toList()));
        }
        if (parentId != null) {
            return ResponseEntity.ok(unitService.getAllByParentId(parentId).stream().map(UnitDTO::new).collect(Collectors.toList()));
        }
        if (name != null) {
            return ResponseEntity.ok(unitService.getAllByName(name).stream().map(UnitDTO::new).collect(Collectors.toList()));
        }

        return ResponseEntity.ok(unitService.getAll().stream().map(UnitDTO::new).collect(Collectors.toList()));
    }

    @Operation(summary = "Get an unit by ID", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content())
    })
    @GetMapping("/{id}")
    public ResponseEntity<UnitDTO> getUnitById(
            @Parameter(description = "Unit's ID") @PathVariable Long id,
            @Parameter(description = "If it 'true', unit's children will be respond") @RequestParam(defaultValue = "false") boolean getChildren
    ) {
        log.debug("GET - Get an unit by ID {}", id);
        if (getChildren) {
            return ResponseEntity.ok(new UnitDTO(unitService.getById(id), unitService.getAllByParentId(id)));
        }
        return ResponseEntity.ok(new UnitDTO(unitService.getById(id)));
    }

    @Operation(summary = "Search units by name", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content())
    })
    @GetMapping("/search")
    public ResponseEntity<List<String>> searchUnitByName(@Parameter(description = "Unit's Name") @RequestParam String name) {
        log.debug("GET - Search units by unit's name {}", name);
        return ResponseEntity.ok(unitService.getAllByName(name).stream().map(Unit::getName).collect(Collectors.toList()));
    }

    @Operation(summary = "Get parent unit and sibling units by self-Id", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content())
    })
    @GetMapping("/family-groups")
    public ResponseEntity<UnitDTO> getFamilyGroupsBySelfId(@Parameter(description = "Unit's ID") @RequestParam Long id) {
        log.debug("GET - Get parent/siblings unit by self-ID {}", id);
        return ResponseEntity.ok(unitService.getParentUnitAndSiblingUnitsBySelfId(id));
    }

    @Operation(summary = "Get all parent units by unit's ID", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content())
    })
    @GetMapping("/parent")
    public ResponseEntity<List<UnitDTO>> getAllParentUnitsByGroupId(@Parameter(description = "Unit's ID") @RequestParam Long id) {
        log.debug("GET - Get all parent groups by unit's ID {}", id);
        return ResponseEntity.ok(unitService.getAllParentUnitsByUnitId(id).stream().map(UnitDTO::new).collect(Collectors.toList()));
    }

    @Operation(summary = "Create an unit", responses = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content()),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content())
    })
    @PostMapping
    public ResponseEntity<UnitDTO> createUnit(@RequestBody CreateUnitDTO unitDTO) {
        log.debug("POST - Create an unit");
        return ResponseEntity.ok(new UnitDTO(unitService.save(unitDTO)));
    }

    @Operation(summary = "Synchronize unit children", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content()),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content())
    })
    @PostMapping("/sync-children")
    public ResponseEntity<String> syncUnitChildren(@Valid @RequestBody SyncUnitChildrenDTO syncUnitChildrenDTO) {
        log.debug("POST - Synchronize unit children");
        unitService.syncUnitChildren(syncUnitChildrenDTO);
        return ResponseEntity.ok("Unit Children have been synchronized");
    }

    @Operation(summary = "Update manager of a unit", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content()),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content())
    })
    @PutMapping("/manager")
    public ResponseEntity<UnitDTO> updateDirectManager(@RequestBody UpdateManagerDTO updateManagerDTO) {
        log.debug("PUT - Update manager of an unit");
        return ResponseEntity.ok(new UnitDTO(unitService.updateUnitManager(updateManagerDTO)));
    }

    @Operation(summary = "Delete an unit by ID", responses = {
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content())
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUnitById(@Parameter(description = "Unit's ID") @PathVariable Long id) {
        log.debug("DELETE - Delete an unit by ID {}", id);
        unitService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
