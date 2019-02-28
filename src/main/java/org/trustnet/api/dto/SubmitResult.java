package org.trustnet.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by bhadoria on 1/27/19.
 */

public class SubmitResult {
    @JsonProperty("tx_id")
    String txId;

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    @Override
    public String toString() {
        return "SubmitResult{" +
                "txId='" + txId + '\'' +
                '}';
    }
}
