package com.bosch.digicore.dtos;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class SyncUnitChildrenDTO {

    @NotNull
    private Long parentId;
    private List<Children> newChildren;
    private List<Children> updatedChildren;
    private List<Children> deletedChildren;

    @Data
    public static class Children {
        private Long id;
        private String name;
        private String description;
    }
}
