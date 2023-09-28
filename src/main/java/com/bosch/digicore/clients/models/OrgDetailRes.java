package com.bosch.digicore.clients.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrgDetailRes {
    @JsonProperty("Root")
    private Root root;
    @JsonProperty("DynamicSizeListBoxesContent")
    private List<BoxesContent> boxesContents;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Root {
        @JsonProperty("RawDataId")
        private int rawDataId;
        @JsonProperty("UniqueDataId")
        private String uniqueDataId;
        @JsonProperty("PreCalculatedSelectedProperties")
        private Map<String, String> preCalculatedSelectedProperties;
        @JsonProperty("Parents")
        private List<Root> parents;
        @JsonProperty("Children")
        private List<Root> children;
        @JsonProperty("DisplayValue")
        private String displayValue;
        @JsonProperty("DataKey")
        private String dataKey;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BoxesContent {
        @JsonProperty("Key")
        private int key;
        @JsonProperty("Value")
        private Map<String, List<BoxesContentValue>> value;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BoxesContentValue {
        @JsonProperty("RawDataId")
        private int rawDataId;
        @JsonProperty("UniqueDataId")
        private String uniqueDataId;
        @JsonProperty("PreCalculatedSelectedProperties")
        private Map<String, String> selectedProperties;
        @JsonProperty("DisplayValue")
        private String displayValue;
        @JsonProperty("DataKey")
        private String dataKey;
    }
}
