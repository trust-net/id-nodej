package org.trustnet.util;

import android.util.Log;

import org.ethereum.crypto.ECKey;
import org.spongycastle.util.encoders.Base64;
import org.spongycastle.util.encoders.Hex;
import org.trustnet.api.dto.SubmitRequest;

import java.nio.ByteBuffer;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by bhadoria on 1/26/19.
 */

public class Submitter {
    private ECKey key = new ECKey();

    private byte[] pubKey = key.getPubKey();
    private String pubKeyHex = Hex.toHexString(pubKey);

    private long nextSeq = 1;

    private String lastTx = Hex.toHexString(new byte[64]);

    // default instance
    private static Submitter instance = new Submitter();

    private static ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);

    private Submitter(){}

    // make constructor private
    private Submitter(ECKey key, long nextSeq, String lastTx) {
        key = key;
        pubKey = key.getPubKey();
        pubKeyHex = Hex.toHexString(pubKey);
        this.nextSeq = nextSeq;
        this.lastTx = lastTx;
        Hex.toHexString(new byte[64]);
    }

    // initialize submitter with private key of the identity
    public static boolean initialize(ECKey key) {
        // TBD: change below to actually initialize either from DB, or take from arguments to initialize
        Submitter.instance = new Submitter(key, 1, Hex.toHexString(new byte[64]));

        // actually all of the below should be done by the application client
        // and then call this initializer, since same Java code may get used
        // in different implementation, e.g. Android vs Service

        // fetch application's private key from android keystore
        // TBD

        // lookup encrypted and persisted submitter key from android DB
        // TBD

        // decrypt submitter key using application's private key from android keystore
        // TBD

        // generate ECKey instance from decrypted value
        // TBD

        return false;
    }

    // use a provider pattern
    public static Submitter instance() {
        return instance;
    }

    // get a hex encoded public ID
    public String getHexPublicId() {
        return Hex.toHexString(key.getPubKey());
    }

    // get a base64 encoded private key
    public String getBase64PrivateKey() {
        return Base64.toBase64String(key.getPrivKeyBytes());
    }

    // get submitter's sequence for next transaction
    public long getNextSeq() {
        return nextSeq;
    }

    // set submitter's last transaction id
    public String getLastTx() {
        return lastTx;
    }

    // update submitter after successful transaction submission
    public void success(String txId) {
        lastTx = txId;
        nextSeq++;
    }

    // create a new transaction request for payload using correct submitter sequence
    public SubmitRequest newRequest(byte[] shardId, String payload) throws NoSuchAlgorithmException {
        // decode base64 payload to bytes
        byte[] payloadBytes = Base64.decode(payload);

        // build serialized bytes as per protocol schema
        // ref: https://github.com/trust-net/dag-lib-go/blob/iter_8/docs/Transaction.md#request-signature
        byte[] bytes = new byte[payloadBytes.length+shardId.length+145];
        int startPos = 0;

        Log.d("SubmitRequest", "creating byte sequence for signature");

        // start with payload of the request
        System.arraycopy(payloadBytes, 0, bytes, startPos, payloadBytes.length);
        startPos += payloadBytes.length;

        Log.d("SubmitRequest", "copied payload bytes");

        // follow that with shard ID for this request
        System.arraycopy(shardId, 0, bytes, startPos, shardId.length);
        startPos += shardId.length;
        Log.d("SubmitRequest", "copied shard_id bytes");

        // add submitter's last transaction ID
        byte[] lastTxBytes = Hex.decode(lastTx);
        System.arraycopy(lastTxBytes, 0, bytes, startPos, lastTxBytes.length);
        startPos += lastTxBytes.length;
        Log.d("SubmitRequest", "copied last_tx bytes");

        // then the submitter's public ID itself
        System.arraycopy(pubKey, 0, bytes, startPos, pubKey.length);
        startPos += pubKey.length;
        Log.d("SubmitRequest", "copied submitter_id bytes");

        // add submiter's sequence for this transaction request
        buffer.clear();
        buffer.putLong(nextSeq);
        byte[] longBytes = buffer.array();
        System.arraycopy(longBytes, 0, bytes, startPos, longBytes.length);
        startPos += longBytes.length;
        Log.d("SubmitRequest", "copied submitter_seq bytes");

        // finally the padding to meet PoW needs
        long padding = 0;
        buffer.clear();
        buffer.putLong(padding);
        longBytes = buffer.array();
        System.arraycopy(longBytes, 0, bytes, startPos, longBytes.length);
        startPos += longBytes.length;
        Log.d("SubmitRequest", "copied padding bytes");

        // compute SHA256 digest of the bytes
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        // get signature using private key
        String signature = key.sign(digest.digest(bytes)).toBase64();
        Log.d("SubmitRequest", "created signature: " + signature);

        // build the transaction request with parameters and return
        return new SubmitRequest(payload, Hex.toHexString(shardId), pubKeyHex, lastTx, nextSeq, padding, signature);
    }
}
