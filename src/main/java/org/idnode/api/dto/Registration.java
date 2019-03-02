package org.idnode.api.dto;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * EcdsaKey attribute registration
 * <p>
 * A Trust-Net identity attribute registration request
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "name",
        "value",
        "revision",
        "proof"
})
public class Registration {

    /**
     * name of the attribute being registered
     * (Required)
     */
    @JsonProperty("name")
    @JsonPropertyDescription("name of the attribute being registered")
    private String name;
    /**
     * a base64 encoded value, as defined by each attribute
     * (Required)
     */
    @JsonProperty("value")
    @JsonPropertyDescription("a base64 encoded value, as defined by each attribute")
    private String value;
    /**
     * unsigned 64 bit revision number of the attribute
     * (Required)
     */
    @JsonProperty("revision")
    @JsonPropertyDescription("unsigned 64 bit revision number of the attribute")
    private Integer revision;
    /**
     * a base64 encoded proof of ownership, as defined by each attribute
     * (Required)
     */
    @JsonProperty("proof")
    @JsonPropertyDescription("a base64 encoded proof of ownership, as defined by each attribute")
    private String proof;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * name of the attribute being registered
     * (Required)
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * name of the attribute being registered
     * (Required)
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    /**
     * a base64 encoded value, as defined by each attribute
     * (Required)
     */
    @JsonProperty("value")
    public String getValue() {
        return value;
    }

    /**
     * a base64 encoded value, as defined by each attribute
     * (Required)
     */
    @JsonProperty("value")
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * unsigned 64 bit revision number of the attribute
     * (Required)
     */
    @JsonProperty("revision")
    public Integer getRevision() {
        return revision;
    }

    /**
     * unsigned 64 bit revision number of the attribute
     * (Required)
     */
    @JsonProperty("revision")
    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    /**
     * a base64 encoded proof of ownership, as defined by each attribute
     * (Required)
     */
    @JsonProperty("proof")
    public String getProof() {
        return proof;
    }

    /**
     * a base64 encoded proof of ownership, as defined by each attribute
     * (Required)
     */
    @JsonProperty("proof")
    public void setProof(String proof) {
        this.proof = proof;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}