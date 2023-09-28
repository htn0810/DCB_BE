package com.bosch.digicore.dtos;

import com.bosch.digicore.constants.AssetType;
import com.bosch.digicore.entities.Asset;
import com.bosch.digicore.entities.Organization;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Data
public class CreateAssetDTO {

    @NotNull
    private String name;

    private String description;

    @NotNull
    private List<EmployeeDTO> owners;

    @NotNull
    private List<EmployeeDTO> developers;

    private String appUrl;

    private String repoUrl;

    private Map<String, String> otherUrls;

    private AssetType type;

    private List<OrganizationDTO> organizations;

    public Asset convertToAsset() {
        Asset asset = new Asset();
        asset.setName(this.name);
        asset.setDescription(this.description);
        asset.setAppUrl(this.appUrl);
        asset.setRepoUrl(this.repoUrl);
        asset.setOtherUrls(this.otherUrls.isEmpty() ? null : this.otherUrls.toString());
        asset.setType(this.type);
        return asset;
    }
}
