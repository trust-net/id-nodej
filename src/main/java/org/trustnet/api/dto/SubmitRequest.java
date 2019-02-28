package org.trustnet.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by bhadoria on 2/1/19.
 * Ref: https://github.com/trust-net/dag-lib-go/blob/iter_8/docs/SpendrApp.md#op-submit-transaction
 */

public class SubmitRequest {
    @JsonProperty("payload")
    String payload;

    @JsonProperty("shard_id")
    String shardId;

    @JsonProperty("submitter_id")
    String submitterId;

    @JsonProperty("last_tx")
    String lastTx;

    @JsonProperty("submitter_seq")
    long submitterSeq;

    @JsonProperty("padding")
    long padding;

    @JsonProperty("signature")
    String signature;

    public SubmitRequest() {}

    public SubmitRequest(String payload, String shardId, String submitterId, String lastTx, long submitterSeq, long padding, String signature) {
        this.payload = payload;
        this.shardId = shardId;
        this.submitterId = submitterId;
        this.lastTx = lastTx;
        this.submitterSeq = submitterSeq;
        this.padding = padding;
        this.signature = signature;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getShardId() {
        return shardId;
    }

    public void setShardId(String shardId) {
        this.shardId = shardId;
    }

    public String getSubmitterId() {
        return submitterId;
    }

    public void setSubmitterId(String submitterId) {
        this.submitterId = submitterId;
    }

    public String getLastTx() {
        return lastTx;
    }

    public void setLastTx(String lastTx) {
        this.lastTx = lastTx;
    }

    public long getSubmitterSeq() {
        return submitterSeq;
    }

    public void setSubmitterSeq(long submitterSeq) {
        this.submitterSeq = submitterSeq;
    }

    public long getPadding() {
        return padding;
    }

    public void setPadding(long padding) {
        this.padding = padding;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public String toString() {
        return "SubmitRequest{" +
                "shardId='" + shardId + '\'' +
                ", lastTx='" + lastTx + '\'' +
                ", submitterSeq=" + submitterSeq +
                '}';
    }
}
