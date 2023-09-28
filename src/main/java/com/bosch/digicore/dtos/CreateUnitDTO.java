package com.bosch.digicore.dtos;

import lombok.Data;

@Data
public class CreateUnitDTO {

    private String name;

    private String description;

    private Long parentId;

    private Long orgId;
}
