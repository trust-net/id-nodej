package org.idnode.api.client;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.ethereum.crypto.ECKey;
import org.idnode.api.dto.Endorsement;
import org.idnode.api.dto.Operation;
import org.idnode.api.dto.Registration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.trustnet.api.dto.SubmitRequest;
import org.trustnet.api.dto.SubmitResult;
import org.trustnet.util.Submitter;

/**
 * Client library for trust-net Idnode poc application
 * Created by bhadoria on 02/27/2019.
 */

public class IdClient {
    private static final Logger logger = LoggerFactory.getLogger(IdClient.class);

    private RestTemplate restTemplate;
    private static String baseUrl;

    static private IdClient client;

    public static final String AppName = "trust-net-identity-poc";

    public static final byte[] ShardId = AppName.getBytes();

    static {
        // TBD: change below to read key from persisted DB
        Submitter.initialize(new ECKey());
    }

    static public void setBaseUrl(String url) { baseUrl = url; }

    static public String getBaseUrl() {
        return baseUrl;
    }

    static public IdClient instance() {
        if (client == null) {
            client = new IdClient();
        }
        return client;
    }

    private IdClient() {
        this.restTemplate = new RestTemplate();
        // Add the String message converter
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
    }

    public ResponseEntity<? extends Object> getRegistration(String id, String name) {
        try {
            return restTemplate.getForEntity(baseUrl + "/identity/" + id + "/registrations/" + name, Registration.class);
        } catch (Exception e) {
            return new ResponseEntity<Object>(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    public ResponseEntity<? extends Object> getEndorsement(String id, String name) {
        try {
            return restTemplate.getForEntity(baseUrl + "/identity/" + id + "/endorsements/" + name, Endorsement.class);
        } catch (Exception e) {
            return new ResponseEntity<Object>(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    public ResponseEntity<? extends Object> submitTransaction(Operation op) {
        try {
            // make base64 string of json serialized structure of operation
            ObjectMapper mapper = new ObjectMapper();
            String payload = Base64.toBase64String(mapper.writeValueAsString(op).getBytes());
            // create a transaction request
            SubmitRequest txRequest = Submitter.instance().newRequest(ShardId, payload);
            Log.d("Submitting Request", txRequest.toString());

            // submit transaction
            ResponseEntity<SubmitResult> response = restTemplate.postForEntity(baseUrl + "/transactions",
                    txRequest,
                    SubmitResult.class);
            logger.debug("Submit response: {}", response.toString());
            Log.d("Submit Response", response.toString());

            // for success case, update submitter client with result
            if (response.getStatusCode() == HttpStatus.CREATED || response.getStatusCode() == HttpStatus.OK) {
                Submitter.instance().success(response.getBody().getTxId());
            }

            // pass back result
            return response;
        } catch (Exception e) {
            return new ResponseEntity<Object>(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    public ResponseEntity<? extends Object> submitRegistration(Registration registration) {
        try {
            // serialize registration arguments as base64 string of json serialized structure
            ObjectMapper mapper = new ObjectMapper();
            String args = Base64.toBase64String(mapper.writeValueAsString(registration).getBytes());
            // submit transaction for registration op-code 0x01
            return submitTransaction(new Operation(0x01, args));
        } catch (Exception e) {
            return new ResponseEntity<Object>(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    public ResponseEntity<? extends Object> submitEndorsement(Endorsement endorsement) {
        try {
            // serialize endorsement arguments as base64 string of json serialized structure
            ObjectMapper mapper = new ObjectMapper();
            String args = Base64.toBase64String(mapper.writeValueAsString(endorsement).getBytes());
            // submit transaction for registration op-code 0x02
            return submitTransaction(new Operation(0x02, args));
        } catch (Exception e) {
            return new ResponseEntity<Object>(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
}
