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
 * Identity attribute endorsement
 * <p>
 * A Trust-Net identity attribute endorsement request
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "name",
        "endorser_id",
        "enc_secret",
        "enc_value",
        "revision",
        "endorsement"
})
public class Endorsement {

    /**
     * name of the attribute being endorsed
     * (Required)
     */
    @JsonProperty("name")
    @JsonPropertyDescription("name of the attribute being endorsed")
    private String name;
    /**
     * a base64 encoded [65]byte ECDSA public id/key of the endorsing identity partner
     * (Required)
     */
    @JsonProperty("endorser_id")
    @JsonPropertyDescription("a base64 encoded [65]byte ECDSA public id/key of the endorsing identity partner")
    private String endorserId;
    /**
     * a base64 encoded AES256 secret key, encrypted using identity owner's PublicSECP256K1 key
     * (Required)
     */
    @JsonProperty("enc_secret")
    @JsonPropertyDescription("a base64 encoded AES256 secret key, encrypted using identity owner's PublicSECP256K1 key")
    private String encSecret;
    /**
     * a base64 encoded attribute value as defined by each attribute encrypted as cipher text using the secret key above
     * (Required)
     */
    @JsonProperty("enc_value")
    @JsonPropertyDescription("a base64 encoded attribute value as defined by each attribute encrypted as cipher text using the secret key above")
    private String encValue;
    /**
     * unsigned 64 bit revision number of the attribute
     * (Required)
     */
    @JsonProperty("revision")
    @JsonPropertyDescription("unsigned 64 bit revision number of the attribute")
    private Integer revision;
    /**
     * a base64 encoded endorsement proof, as defined by each attribute
     * (Required)
     */
    @JsonProperty("endorsement")
    @JsonPropertyDescription("a base64 encoded endorsement proof, as defined by each attribute")
    private String endorsement;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * name of the attribute being endorsed
     * (Required)
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * name of the attribute being endorsed
     * (Required)
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    /**
     * a base64 encoded [65]byte ECDSA public id/key of the endorsing identity partner
     * (Required)
     */
    @JsonProperty("endorser_id")
    public String getEndorserId() {
        return endorserId;
    }

    /**
     * a base64 encoded [65]byte ECDSA public id/key of the endorsing identity partner
     * (Required)
     */
    @JsonProperty("endorser_id")
    public void setEndorserId(String endorserId) {
        this.endorserId = endorserId;
    }

    /**
     * a base64 encoded AES256 secret key, encrypted using identity owner's PublicSECP256K1 key
     * (Required)
     */
    @JsonProperty("enc_secret")
    public String getEncSecret() {
        return encSecret;
    }

    /**
     * a base64 encoded AES256 secret key, encrypted using identity owner's PublicSECP256K1 key
     * (Required)
     */
    @JsonProperty("enc_secret")
    public void setEncSecret(String encSecret) {
        this.encSecret = encSecret;
    }

    /**
     * a base64 encoded attribute value as defined by each attribute encrypted as cipher text using the secret key above
     * (Required)
     */
    @JsonProperty("enc_value")
    public String getEncValue() {
        return encValue;
    }

    /**
     * a base64 encoded attribute value as defined by each attribute encrypted as cipher text using the secret key above
     * (Required)
     */
    @JsonProperty("enc_value")
    public void setEncValue(String encValue) {
        this.encValue = encValue;
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
     * a base64 encoded endorsement proof, as defined by each attribute
     * (Required)
     */
    @JsonProperty("endorsement")
    public String getEndorsement() {
        return endorsement;
    }

    /**
     * a base64 encoded endorsement proof, as defined by each attribute
     * (Required)
     */
    @JsonProperty("endorsement")
    public void setEndorsement(String endorsement) {
        this.endorsement = endorsement;
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