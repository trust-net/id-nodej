package org.idnode.identity;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.ethereum.crypto.ECKey;
import org.idnode.api.client.IdClient;
import org.idnode.api.dto.Registration;
import org.idnode.attributes.StandardAttributes;
import org.spongycastle.util.encoders.Base64;
import org.spongycastle.util.encoders.Hex;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.trustnet.api.dto.SubmitResult;
import org.trustnet.util.Submitter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * An identity owner for Trust-Net's Idnode application
 * Created by bhadoria on 2/28/19.
 */

public class Owner {
    private static final String TAG = "Owner";
    private String user;

    private ECKey idKey;

    private String publicId;

    private ECKey encKey;

    private static ObjectMapper mapper = new ObjectMapper();

    private static ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);

    private Map<String, SequenceHistory> shardHistoryMap = new HashMap<>();

    private Provider provider;

    private static Owner owner;

    private Owner() {
    }

    /**
     * initialize the singleton owner based on specified user, by fetching user's keys and history from persistence provider
     */
    public static Owner initialize(String user, Provider provider) {
        owner = new Owner();
        owner.user = user;
        owner.provider = provider;
        // initialize submitter after reading owner state from persisted DB
        Submitter.initialize(owner.getIdKey(), owner.getHistory(IdClient.AppName).getNextSeq(), owner.getHistory(IdClient.AppName).getLastTx());
        Log.d(TAG, "Initialized for user: " + user);
        return owner;
    }

    public static Owner instance() {
        return owner;
    }

    /**
     * get reference to identity owner's identity key, loads the key from persistence provider is not cached already
     *
     * @return reference to identity private key
     */
    public ECKey getIdKey() {
        if (idKey != null) return idKey;
        // read key from DB
        String base64 = provider.getECDSAKeyForUser(user);
        if (StringUtils.isEmpty(base64)) {
            // no key in DB, create one and save
            idKey = new ECKey();
            Log.d(TAG, "Created new ECDSA identity key");
            provider.saveECDSAKeyForUser(user, Base64.toBase64String(idKey.getPrivKeyBytes()));
        } else {
            // parsing the base64 key read from DB
            idKey = ECKey.fromPrivate(Base64.decode(base64));
            Log.d(TAG, "Read ECDSA identity key from DB");
        }
        // initialize public address
        publicId = Hex.toHexString(idKey.getPubKey());
        return idKey;
    }

    /**
     * get public network identity of the owner
     *
     * @return hex encoded public network identity string
     */
    public String getPublicId() {
        if (StringUtils.isEmpty(publicId)) getIdKey();
        return publicId;
    }

    /**
     * get encryption key for the identity owner, loads key from persistence provider if not cached already
     *
     * @return reference to private ECIES key
     */
    public ECKey getEncKey() {
        if (encKey != null) return encKey;
        // read key from DB
        String base64 = provider.getECIESKeyForUser(user);
        if (StringUtils.isEmpty(base64)) {
            // no key in DB, create one and save
            encKey = new ECKey();
            provider.saveECIESKeyForUser(user, Base64.toBase64String(encKey.getPrivKeyBytes()));
            Log.d(TAG, "Created new ECIES encryption key");
        } else {
            // parsing the base64 key read from DB
            encKey = ECKey.fromPrivate(Base64.decode(base64));
            Log.d(TAG, "Read ECIES encryption key from DB");
        }
        return encKey;
    }

    /**
     * get reference to owner's sequence history for specified shard
     *
     * @param shardId fetch history specific to the shard
     * @return owner's sequence history for specified shard
     */
    public final SequenceHistory getHistory(String shardId) {
        if (shardHistoryMap.containsKey(shardId)) return shardHistoryMap.get(shardId);

        // try reading history from DB
        String encoded = provider.getSequenceHistoryForUser(user, shardId);

        // deserialize json encoded history
        SequenceHistory history;
        if (StringUtils.isEmpty(encoded)) {
            Log.d(TAG, "No prior history for user");
            history = new SequenceHistory();
        } else {
            try {
                history = mapper.readValue(encoded, SequenceHistory.class);
                Log.d(TAG, "Read history from DB: " + history);
            } catch (IOException e) {
                Log.e(TAG, "Failed to decode history from DB: " + e.toString());
                // build a new history sequence
                history = new SequenceHistory();
            }
        }
        setHistory(shardId, history);
        return history;
    }

    /**
     * reset identity owner's history
     */
    public void resetHistory() {
        setHistory(IdClient.AppName, new SequenceHistory());
    }

    /**
     * set owner's sequence history explicitly
     *
     * @param shardId shard id that is being updated
     * @param history new history values
     */
    public void setHistory(String shardId, SequenceHistory history) {
        shardHistoryMap.put(shardId, history);
        saveHistory(shardId, history);
    }

    /**
     * update the identity owner's sequence history after a successful transaction
     *
     * @param shardId shard id that is being updated
     * @param newTx   transaction ID of the last successful transaction
     */
    public void updateHistory(String shardId, String newTx) {
        SequenceHistory history = getHistory(shardId);
        history.update(newTx);
        saveHistory(shardId, history);
    }

    /**
     * persist identity owner's history
     *
     * @param shardId shard id being saved
     * @param history sequence history being saved
     */
    private void saveHistory(String shardId, SequenceHistory history) {
        // encode into json serialized string
        String encoded = null;
        try {
            encoded = mapper.writeValueAsString(history);
            shardHistoryMap.put(shardId, history);
            provider.saveSequenceHistoryForUser(user, shardId, encoded);
            Log.d(TAG, "Saved history to DB: " + history);
        } catch (JsonProcessingException e) {
            Log.e(TAG, "Failed to save history: " + e.getMessage());
        }
    }

    /**
     * private utility method to fetch revision for an attribute's registration update
     *
     * @param attribute registration attribute
     * @return revision to use for next update
     */
    private int nextRegisteredRevision(String attribute) {
        ResponseEntity<? extends Object> response = IdClient.instance().getRegistration(getPublicId(), attribute);
        int revision = 0x01;
        if (response.getStatusCode() == HttpStatus.OK) {
            // bump up the revision
            revision = ((Registration) response.getBody()).getRevision() + 1;
        }
        return revision;
    }

    /**
     * check if owner's current encryption key is already registered with the network
     *
     * @return
     */
    public boolean isEncKeyRegistered() {
        ResponseEntity<? extends Object> result = IdClient.instance().getRegistration(getPublicId(), StandardAttributes.PublicSECP256K1);
        if (result.getStatusCode() != HttpStatus.OK) {
            // no key registered
            return false;
        }
        // compare registered key with current key
        return ((Registration) result.getBody()).getValue().equals(Base64.toBase64String(getEncKey().getPubKey()));
    }

    /**
     * add or update registration for owner's encryption key
     *
     * @return true/false for success/failure
     */
    public boolean registerEncKey() {
        ResponseEntity<? extends Object> response = IdClient.instance().submitRegistration(
                Owner.instance().getPublicSECP256K1Payload(
                        nextRegisteredRevision(StandardAttributes.PublicSECP256K1)));
        if (response.getStatusCode() != HttpStatus.CREATED) {
            Log.e(TAG, "Failed to register encryption key: " + response.getBody());
            return false;
        } else {
            // update the submitter sequence history
            SubmitResult result = (SubmitResult) response.getBody();
            updateHistory(IdClient.AppName, result.getTxId());
        }
        return true;
    }

    /**
     * private utility method to create attribute registration payload for PublicSECP256K1
     * as per the specs @ https://github.com/trust-net/id-node-go/blob/master/docs/id-app-specs.md#publicsecp256k1-registration-proof
     *
     * @param rev revision number for registration update
     * @return registration payload
     */
    private Registration getPublicSECP256K1Payload(Integer rev) {
        Registration payload = new Registration();
        payload.setName(StandardAttributes.PublicSECP256K1);
        payload.setValue(Base64.toBase64String(getEncKey().getPubKey()));
        payload.setRevision(rev);
        // build serialized bytes for proof as per protocol schema
        // ref: https://github.com/trust-net/id-node-go/blob/master/docs/id-app-specs.md#publicsecp256k1-registration-proof
        byte[] id = getIdKey().getPubKey();
        byte[] bytes = new byte[id.length + 8];
        int startPos = 0;

        // start with public ID of the identity owner
        System.arraycopy(id, 0, bytes, startPos, id.length);
        startPos += id.length;
        Log.d(TAG, "Copied id bytes length: " + id.length);

        // follow that with 8 byte revision
        buffer.clear();
        buffer.putLong(rev);
        byte[] longBytes = buffer.array();
        System.arraycopy(longBytes, 0, bytes, startPos, longBytes.length);
        Log.d(TAG, "Copied revision bytes length: " + longBytes.length);

        // compute SHA256 digest of the bytes
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Failed to get SHA-256 digest");
            return null;
        }
        // get signature using ECIES private key
        payload.setProof(getEncKey().sign(digest.digest(bytes)).toBase64());
        return payload;
    }

    /**
     * get identity owner's registered first name attribute from network
     *
     * @return first name, if registered, or null
     */
    public String getRegsiteredFirstName() {
        ResponseEntity<? extends Object> result = IdClient.instance().getRegistration(getPublicId(), StandardAttributes.PreferredFirstName);
        if (result.getStatusCode() != HttpStatus.OK) {
            return null;
        }
        return ((Registration) result.getBody()).getValue();
    }

    /**
     * add or update first name registration for the identity owner
     *
     * @param firstName value to register
     * @return success/failure
     */
    public boolean registerFirstName(String firstName) {
        // argument validation
        if (StringUtils.isEmpty(firstName)) {
            Log.d(TAG, "invalid firstName: " + firstName);
            return false;
        }
        ResponseEntity<? extends Object> response = IdClient.instance().submitRegistration(
                Owner.instance().getPreferredFirstNamePayload(firstName,
                        nextRegisteredRevision(StandardAttributes.PreferredFirstName)));
        if (response.getStatusCode() != HttpStatus.CREATED) {
            Log.e(TAG, "Failed to register first name: " + response.getBody());
            return false;
        } else {
            // update the submitter sequence history
            SubmitResult result = (SubmitResult) response.getBody();
            updateHistory(IdClient.AppName, result.getTxId());
        }
        return true;
    }

    /**
     * private utility method to create registration payload for first name registration
     *
     * @param firstName
     * @param rev
     * @return payload
     */
    private Registration getPreferredFirstNamePayload(String firstName, Integer rev) {
        Registration payload = new Registration();
        payload.setName(StandardAttributes.PreferredFirstName);
        payload.setValue(firstName);
        payload.setRevision(rev);
        payload.setProof(null);
        return payload;
    }

    /**
     * get identity owner's registered last name from the network
     *
     * @return last name, if registered, or null
     */
    public String getRegisteredLastName() {
        ResponseEntity<? extends Object> result = IdClient.instance().getRegistration(getPublicId(), StandardAttributes.PreferredLastName);
        if (result.getStatusCode() != HttpStatus.OK) {
            return null;
        }
        return ((Registration) result.getBody()).getValue();
    }

    /**
     * add or update last name registration for the identity owner
     *
     * @param lastName value to register
     * @return success/failure
     */
    public boolean registerLastName(String lastName) {
        // argument validation
        if (StringUtils.isEmpty(lastName)) {
            Log.d(TAG, "invalid lastname: " + lastName);
            return false;
        }
        ResponseEntity<? extends Object> response = IdClient.instance().submitRegistration(
                Owner.instance().getPreferredLastNamePayload(lastName,
                        nextRegisteredRevision(StandardAttributes.PreferredLastName)));
        if (response.getStatusCode() != HttpStatus.CREATED) {
            Log.e(TAG, "Failed to register last name: " + response.getBody());
            return false;
        } else {
            // update the submitter sequence history
            SubmitResult result = (SubmitResult) response.getBody();
            updateHistory(IdClient.AppName, result.getTxId());
        }
        return true;
    }

    /**
     * private utility method to create registration payload for last name attribute registration
     *
     * @param lastName
     * @param rev
     * @return payload
     */
    private Registration getPreferredLastNamePayload(String lastName, Integer rev) {
        Registration payload = new Registration();
        payload.setName(StandardAttributes.PreferredLastName);
        payload.setValue(lastName);
        payload.setRevision(rev);
        payload.setProof(null);
        return payload;
    }
}
