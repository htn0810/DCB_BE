package com.bosch.digicore.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class CreateUpdateOrgDTO {

    private Long id;

    @NotNull
    @NotBlank
    private String name;

    private String description;

    private String orgUrl;

    @NotNull
    @NotEmpty
    private List<EmployeeDTO> owners;

    private List<AssetDTO> assets;

    private List<UnitDTO> units;

    private List<EmployeeDTO> employees;

    @JsonIgnore
    public List<Long> getOwnerIds() {
        return this.owners.stream().map(EmployeeDTO::getId).collect(Collectors.toList());
    }

    @JsonIgnore
    public List<Long> getAssetIds() {
        return this.assets.stream().map(AssetDTO::getId).collect(Collectors.toList());
    }

    @JsonIgnore
    public List<Long> getUnitIds() {
        return this.units.stream().map(UnitDTO::getId).collect(Collectors.toList());
    }

    @JsonIgnore
    public List<Long> getEmployeeIds() {
        return this.employees.stream().map(EmployeeDTO::getId).collect(Collectors.toList());
    }

    @Data
    public static class AssetDTO {
        private Long id;
    }

    @Data
    public static class UnitDTO {
        private Long id;
    }

    @Data
    public static class EmployeeDTO {
        private Long id;
    }
}
