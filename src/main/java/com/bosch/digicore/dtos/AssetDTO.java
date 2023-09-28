package com.bosch.digicore.dtos;

import com.bosch.digicore.constants.AssetStatus;
import com.bosch.digicore.constants.AssetType;
import com.bosch.digicore.entities.Asset;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class AssetDTO {

    private Long id;
    private String uuid;
    private String name;
    private String description;
    private String imageUrl;
    private List<EmployeeDTO> owners;
    private List<EmployeeDTO> developers;
    private String appUrl;
    private String repoUrl;
    private Map<String, String> otherUrls;
    private Instant publishedDate;
    private AssetStatus status;
    private AssetType type;
    private List<OrganizationDTO> organizations;

    public AssetDTO(final Asset asset) {
        this.id = asset.getId();
        this.uuid = asset.getUuid();
        this.name = asset.getName();
        this.description = asset.getDescription();
        this.imageUrl = asset.getImageUrl();
        this.owners = asset.getOwners().stream().map(EmployeeDTO::new).collect(Collectors.toList());
        this.developers = asset.getDevelopers().stream().map(EmployeeDTO::new).collect(Collectors.toList());
        this.appUrl = asset.getAppUrl();
        this.repoUrl = asset.getRepoUrl();
        this.otherUrls = new HashMap<>();
        if (asset.getOtherUrls() != null && !asset.getOtherUrls().isEmpty()) {
            Arrays.stream(asset.getOtherUrls().split(",")).forEach(s -> {
                int index = s.indexOf("=");
                this.otherUrls.put(s.substring(0, index), s.substring(index + 1));
            });
        }
        this.publishedDate = asset.getPublishedDate();
        this.status = asset.getStatus();
        this.type = asset.getType();
        this.organizations = asset.getOrganizations().stream()
                .map(organization -> new OrganizationDTO(organization.getId(), organization.getName()))
                .collect(Collectors.toList());
    }

    @Data
    @NoArgsConstructor
    static class OrganizationDTO {
        private Long id;
        private String name;

        public OrganizationDTO(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
