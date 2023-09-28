package com.bosch.digicore.services;

import com.bosch.digicore.constants.AssetStatus;
import com.bosch.digicore.controllers.ImageController;
import com.bosch.digicore.dtos.AssetDTO;
import com.bosch.digicore.dtos.CreateAssetDTO;
import com.bosch.digicore.dtos.EmployeeDTO;
import com.bosch.digicore.dtos.OrganizationDTO;
import com.bosch.digicore.entities.Asset;
import com.bosch.digicore.entities.AssetComment;
import com.bosch.digicore.entities.Organization;
import com.bosch.digicore.exceptions.BadRequestException;
import com.bosch.digicore.exceptions.ResourceNotFoundException;
import com.bosch.digicore.repositories.AssetCommentRepository;
import com.bosch.digicore.repositories.AssetRepository;
import com.bosch.digicore.repositories.EmployeeRepository;
import com.bosch.digicore.repositories.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final AssetRepository assetRepository;
    private final AssetCommentRepository assetCommentRepository;
    private final OrganizationRepository organizationRepository;
    private final EmployeeRepository employeeRepository;
    private final ImageService imageService;

    @Transactional(readOnly = true)
    public List<Asset> getAll() {
        return assetRepository.findAllByDeletedIsFalse();
    }

    @Transactional(readOnly = true)
    public List<Asset> getAllByOrgId(final Long orgId) {
        final Organization organization = organizationRepository.findByIdAndDeletedIsFalse(orgId)
                .orElseThrow(() -> new ResourceNotFoundException(Organization.class.getName(), "id", orgId));

        return assetRepository.findAllByOrganizationsInAndDeletedIsFalse(Collections.singletonList(organization));
    }

    @Transactional(readOnly = true)
    public List<Asset> getAllByStatus(final AssetStatus status) {
        return assetRepository.findAllByStatus(status);
    }

    @Transactional(readOnly = true)
    public Asset getById(final Long id) {
        return assetRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Asset", "id", id));
    }

    @Transactional
    public Asset create(final CreateAssetDTO createAssetDTO, final MultipartFile image) {
        List<Organization> organizations = organizationRepository.findAllByIdIn(createAssetDTO.getOrganizations().stream().map(OrganizationDTO::getId).collect(Collectors.toList()));
        if (organizations.isEmpty()) {
            throw new BadRequestException("Asset is not belong to specific organization");
        }
        final Asset asset = createAssetDTO.convertToAsset();
        asset.setUuid(UUID.randomUUID().toString());
        asset.setOrganizations(organizations);
        asset.setOwners(employeeRepository.findAllByIdIn(createAssetDTO.getOwners().stream().map(EmployeeDTO::getId).collect(Collectors.toList())));
        asset.setDevelopers(employeeRepository.findAllByIdIn(createAssetDTO.getDevelopers().stream().map(EmployeeDTO::getId).collect(Collectors.toList())));
        if (image != null) {
            String imageId = imageService.save(image).getId();
            String imageUrl = WebMvcLinkBuilder.linkTo(methodOn(ImageController.class).getImageById(imageId)).toString();
            asset.setImageUrl(imageUrl);
        }
        return assetRepository.save(asset);
    }

    @Transactional
    public Asset update(final AssetDTO assetDTO, final MultipartFile image) {
        Asset asset = getById(assetDTO.getId());
        asset.setName(assetDTO.getName());
        asset.setDescription(assetDTO.getDescription());
        asset.setAppUrl(assetDTO.getAppUrl());
        asset.setRepoUrl(assetDTO.getRepoUrl());
        asset.setOtherUrls(assetDTO.getOtherUrls().isEmpty() ? null : assetDTO.getOtherUrls().toString());
        asset.setType(assetDTO.getType());
        asset.setStatus(AssetStatus.DRAFT);
        asset.setOwners(employeeRepository.findAllByIdIn(
                assetDTO.getOwners().stream().map(EmployeeDTO::getId).collect(Collectors.toList())));
        asset.setDevelopers(employeeRepository.findAllByIdIn(
                assetDTO.getDevelopers().stream().map(EmployeeDTO::getId).collect(Collectors.toList())));

        if (image != null) {
            if (asset.getImageUrl() != null) {
                int i = asset.getImageUrl().lastIndexOf("/");
                String oldImageId = asset.getImageUrl().substring(i + 1);
                imageService.deleteById(oldImageId);
            }
            String imageId = imageService.save(image).getId();
            String imageUrl = WebMvcLinkBuilder.linkTo(methodOn(ImageController.class).getImageById(imageId)).toString();
            asset.setImageUrl(imageUrl);
        }
        return assetRepository.save(asset);
    }

    @Transactional
    public Asset publish(final Long id) {
        Asset asset = getById(id);
        asset.setStatus(AssetStatus.PUBLISHED);
        asset.setPublishedDate(Instant.now());
        return assetRepository.save(asset);
    }

    @Transactional
    public Asset reject(final Long id, final String comment) {
        Asset asset = getById(id);
        asset.setStatus(AssetStatus.REJECTED);

        final AssetComment assetComment = new AssetComment();
        assetComment.setComment(comment);
        assetComment.setAssetId(id);
        assetCommentRepository.save(assetComment);

        return assetRepository.save(asset);
    }

    @Transactional
    public void deleteById(final Long id) {
        Asset asset = getById(id);
        asset.setDeleted(true);
        assetRepository.save(asset);
    }

    @Transactional(readOnly = true)
    public long countAll() {
        return assetRepository.countAllByDeletedIsFalse();
    }
}
