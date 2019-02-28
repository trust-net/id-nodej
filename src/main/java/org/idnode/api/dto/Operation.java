package org.idnode.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Identity Operation
 * <p>
 * A Trust-Net identity application operation request
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "op_code",
        "args"
})
public class Operation {

    /**
     * unsigned 64 bit integer specifying operation request type
     * (Required)
     */
    @JsonProperty("op_code")
    @JsonPropertyDescription("unsigned 64 bit integer specifying operation request type")
    private Integer opCode;
    /**
     * arguments for the requested operation, encoded as per specs for each specific op_code
     * (Required)
     */
    @JsonProperty("args")
    @JsonPropertyDescription("arguments for the requested operation, encoded as per specs for each specific op_code")
    private String args;

    public Operation() {}

    public Operation(Integer opCode, String args) {
        this.opCode = opCode;
        this.args = args;
    }

    /**
     * unsigned 64 bit integer specifying operation request type
     * (Required)
     */
    @JsonProperty("op_code")
    public Integer getOpCode() {
        return opCode;
    }

    /**
     * unsigned 64 bit integer specifying operation request type
     * (Required)
     */
    @JsonProperty("op_code")
    public void setOpCode(Integer opCode) {
        this.opCode = opCode;
    }

    /**
     * arguments for the requested operation, encoded as per specs for each specific op_code
     * (Required)
     */
    @JsonProperty("args")
    public String getArgs() {
        return args;
    }

    /**
     * arguments for the requested operation, encoded as per specs for each specific op_code
     * (Required)
     */
    @JsonProperty("args")
    public void setArgs(String args) {
        this.args = args;
    }

}