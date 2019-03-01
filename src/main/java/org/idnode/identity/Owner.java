package org.idnode.identity;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.ethereum.crypto.ECKey;
import org.spongycastle.util.encoders.Base64;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bhadoria on 2/28/19.
 */

public class Owner {
    private static final String TAG = "Owner";
    private String user;

    private ECKey idKey;

    private ECKey encKey;

    private ObjectMapper mapper = new ObjectMapper();

    private Map<String, SequenceHistory> shardHistoryMap = new HashMap<>();

    private Provider provider;

    private static Owner owner;

    private Owner() {
    }

    public static Owner initialize(String user, Provider provider) {
        owner = new Owner();
        owner.user = user;
        owner.provider = provider;
        Log.d(TAG, "Initialized for user: " + user);
        return owner;
    }

    public static Owner instance() {
        return owner;
    }

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
        return idKey;
    }

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
                Log.e("Owner", "Failed to decode history from DB: " + e.toString());
                // build a new history sequence
                history = new SequenceHistory();
            }
        }
        setHistory(shardId, history);
        return history;
    }

    public void setHistory(String shardId, SequenceHistory history) {
        shardHistoryMap.put(shardId, history);
        saveHistory(shardId, history);
    }

    public void updateHistory(String shardId, String newTx) {
        SequenceHistory history = getHistory(shardId);
        history.update(newTx);
        saveHistory(shardId, history);
    }

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
}
