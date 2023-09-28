package com.bosch.digicore.clients.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoreSearchRes {
    @JsonProperty("data")
    private List<DataContent> dataContents;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DataContent {
        private int documentId;
        private String foundText;
        private String field;
        private String objectType;
        private String url;
    }
}
