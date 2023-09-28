package com.bosch.digicore.controllers;

import com.bosch.digicore.constants.AssetStatus;
import com.bosch.digicore.constants.Roles;
import com.bosch.digicore.dtos.AssetDTO;
import com.bosch.digicore.dtos.CreateAssetDTO;
import com.bosch.digicore.entities.Asset;
import com.bosch.digicore.services.AssetService;
import com.bosch.digicore.utils.ControllerUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/assets")
@Tag(name = "Asset APIs", description = "APIs to manage assets")
@Slf4j
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    @Operation(summary = "Get all assets", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content())
    })
    @GetMapping
    public ResponseEntity<List<AssetDTO>> getAll(
            @Parameter(description = "ID of organization that assets belong to") @RequestParam(required = false) Long orgId,
            @Parameter(description = "Asset's status") @RequestParam(required = false) AssetStatus status
    ) {
        ControllerUtil.checkParametersWhenOnlyNeedOne(orgId, status);
        if (orgId != null) {
            log.debug("GET - Get all assets by orgId {}", orgId);
            return ResponseEntity.ok(assetService.getAllByOrgId(orgId).stream().map(AssetDTO::new).collect(Collectors.toList()));
        }
        if (status != null) {
            log.debug("GET - Get all assets by status {}", status);
            return ResponseEntity.ok(assetService.getAllByStatus(status).stream().map(AssetDTO::new).collect(Collectors.toList()));
        }
        log.debug("GET - Get all assets");
        return ResponseEntity.ok(assetService.getAll().stream().map(AssetDTO::new).collect(Collectors.toList()));
    }

    @Operation(summary = "Get an asset by ID", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content())
    })
    @GetMapping("/{id}")
    public ResponseEntity<AssetDTO> getById(@PathVariable Long id) {
        log.debug("GET - Get an asset by ID {}", id);
        return ResponseEntity.ok(new AssetDTO(assetService.getById(id)));
    }

    @Operation(summary = "Create an asset", responses = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content())
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AssetDTO> create(
            @Parameter(description = "Asset to create") @RequestPart("asset") CreateAssetDTO assetDTO,
            @Parameter(description = "Image of asset") @RequestPart(value = "image", required = false) MultipartFile image)
            throws URISyntaxException {
		log.debug("POST - Create an asset");
        Asset asset = assetService.create(assetDTO, image);
        return ResponseEntity.created(new URI("/api/assets/" + asset.getId())).body(new AssetDTO(asset));
    }

    @Operation(summary = "Update an asset", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content())
    })
    @PutMapping
    public ResponseEntity<AssetDTO> update(
			@Parameter(description = "Asset to update") @RequestPart("asset") AssetDTO assetDTO,
            @Parameter(description = "Image of asset") @RequestPart(value = "image", required = false) MultipartFile image) {
        log.debug("PUT - Update an asset");
        return ResponseEntity.ok(new AssetDTO(assetService.update(assetDTO, image)));
    }

	@Operation(summary = "Publish an asset by ID", responses = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content())
	})
	@PutMapping("/publish")
	@PreAuthorize(Roles.HAS_ROLE_SUPER_ADMIN_OR_ORG_ADMIN)
	public ResponseEntity<AssetDTO> publish(@Parameter(description = "Asset's ID") @RequestParam Long id) {
		log.debug("PUT - Publish an asset by ID");
		return ResponseEntity.ok(new AssetDTO(assetService.publish(id)));
	}

    @Operation(summary = "Comment on asset by ID", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content())
    })
    @PutMapping("/reject")
    @PreAuthorize(Roles.HAS_ROLE_SUPER_ADMIN_OR_ORG_ADMIN)
    public ResponseEntity<AssetDTO> reject(@Parameter(description = "Asset's ID") @RequestParam Long id,
                                           @Parameter(description = "Comment") @RequestParam String comment) {
        log.debug("POST - Reject an asset by ID");
        return ResponseEntity.ok(new AssetDTO(assetService.reject(id, comment)));
    }

    @Operation(summary = "Delete an asset by ID", responses = {
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content())
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        log.debug("DELETE - Delete an asset by ID");
        assetService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
