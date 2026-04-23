package com.kavindu.smartcampus.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class DiscoveryResponse {
    private String apiName;
    private String version;
    private String description;
    private String specVersion;
    private Map<String, String> contact = new LinkedHashMap<>();
    private Map<String, Object> resources = new LinkedHashMap<>();
    private Map<String, String> server = new LinkedHashMap<>();

    public DiscoveryResponse() {
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSpecVersion() {
        return specVersion;
    }

    public void setSpecVersion(String specVersion) {
        this.specVersion = specVersion;
    }

    public Map<String, String> getContact() {
        return contact;
    }

    public void setContact(Map<String, String> contact) {
        this.contact = contact;
    }

    public Map<String, Object> getResources() {
        return resources;
    }

    public void setResources(Map<String, Object> resources) {
        this.resources = resources;
    }

    public Map<String, String> getServer() {
        return server;
    }

    public void setServer(Map<String, String> server) {
        this.server = server;
    }
}
