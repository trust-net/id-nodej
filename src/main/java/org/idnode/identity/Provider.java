package org.idnode.identity;

import org.ethereum.crypto.ECKey;

/**
 * Created by bhadoria on 2/28/19.
 */

public interface Provider {
    // get a user's private key for identity
    String getECDSAKeyForUser(String user);

    // save a user's private key for identity
    void saveECDSAKeyForUser(String user, String key);

    // get a user's private key for encryption
    String getECIESKeyForUser(String user);

    // save a user's private key used for encryption
    void saveECIESKeyForUser(String user, String key);

    // get a user's sequence history
    String getSequenceHistoryForUser(String user, String shardId);

    // save a user's sequence history
    void saveSequenceHistoryForUser(String user, String shardId, String history);
}
