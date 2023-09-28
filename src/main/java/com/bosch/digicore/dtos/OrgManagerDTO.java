package com.bosch.digicore.dtos;

import com.bosch.digicore.clients.models.OrgDetailRes;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrgManagerDTO {

    private int rawDataId;
    private String name;
    private String description;
    private EmployeeDTO manager;
    private List<EmployeeDTO> employees;
    private List<OrgManagerDTO> orgParent;
    private List<OrgManagerDTO> orgChildren;

    private OrgManagerDTO(int rawDataId, String name, String description) {
        this.rawDataId = rawDataId;
        this.name = name;
        this.description = description;
    }

    public OrgManagerDTO(OrgDetailRes orgDetail) {
        this.rawDataId = orgDetail.getRoot().getRawDataId();
        this.name = orgDetail.getRoot().getDisplayValue();
        this.description = orgDetail.getRoot().getPreCalculatedSelectedProperties().get("2420");
        this.orgParent = orgDetail.getRoot().getParents().stream()
                .map(root -> new OrgManagerDTO(root.getRawDataId(), root.getDisplayValue(), root.getPreCalculatedSelectedProperties().get("2420")))
                .collect(Collectors.toList());
        this.orgChildren = orgDetail.getRoot().getChildren().stream()
                .map(root -> new OrgManagerDTO(root.getRawDataId(), root.getDisplayValue(), root.getPreCalculatedSelectedProperties().get("2420")))
                .collect(Collectors.toList());

        orgDetail.getBoxesContents().stream()
                .filter(boxesContent -> boxesContent.getKey() == this.rawDataId)
                .forEach(boxesContent -> {
                    if (!boxesContent.getValue().get("2625").isEmpty()) {
                        this.manager = new EmployeeDTO(boxesContent.getValue().get("2625").get(0));
                    }
                    if (!boxesContent.getValue().get("2578").isEmpty()) {
                        this.employees = boxesContent.getValue().get("2578").stream().map(EmployeeDTO::new).collect(Collectors.toList());
                    }
                });
    }

    @Data
    public static class EmployeeDTO {
        private String ntid;
        private String employeeId;
        private String fullName;
        private String email;

        public EmployeeDTO(OrgDetailRes.BoxesContentValue value) {
            this.employeeId = value.getUniqueDataId();
            this.ntid = value.getSelectedProperties().get("2408");
            this.fullName = value.getDisplayValue();
            this.email = value.getSelectedProperties().get("2404").toLowerCase();
        }
    }
}
