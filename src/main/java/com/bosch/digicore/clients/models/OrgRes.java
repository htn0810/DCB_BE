package com.bosch.digicore.clients.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrgRes {
    @JsonProperty("TypeId")
    private int typeId;
    @JsonProperty("Rows")
    private List<Row> rows;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Row {
        @JsonProperty("ElementGroups")
        private List<ElementGroup> elementGroups;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ElementGroup {
        @JsonProperty("ObjectContainer")
        private ObjectContainer objectContainer;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ObjectContainer {
        @JsonProperty("RawDataId")
        private int rawDataId;
        @JsonProperty("UniqueDataId")
        private String uniqueDataId;
        @JsonProperty("PreCalculatedSelectedProperties")
        private Map<String, String> preCalculatedSelectedProperties;
        @JsonProperty("DisplayValue")
        private String displayValue;
        @JsonProperty("DataKey")
        private String dataKey;
    }
}
