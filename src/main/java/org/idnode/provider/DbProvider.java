package org.idnode.provider;

import org.idnode.identity.Provider;
import org.idnode.provider.entity.EcdsaKey;
import org.idnode.provider.entity.EciesKey;
import org.idnode.provider.entity.History;
import org.idnode.provider.repository.EcdsaKeyRepository;
import org.idnode.provider.repository.EciesKeyRepository;
import org.idnode.provider.repository.HistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DbProvider implements Provider {
    @Autowired
    private EcdsaKeyRepository ecdsaKeyRepository;

    @Autowired
    private EciesKeyRepository eciesKeyRepository;

    @Autowired
    private HistoryRepository historyRepository;

    @Override
    public String getECDSAKeyForUser(String user) {
        EcdsaKey entity = ecdsaKeyRepository.getEcdsaKeyByUser(user);
        if (entity != null) {
            return entity.key;
        } else {
            return null;
        }
    }

    @Override
    public void saveECDSAKeyForUser(String user, String key) {
        ecdsaKeyRepository.save(new EcdsaKey(user, key));
    }

    @Override
    public String getECIESKeyForUser(String user) {
        EciesKey entity = eciesKeyRepository.getEciesKeyByUser(user);
        if (entity != null) {
            return entity.key;
        } else {
            return null;
        }
    }

    @Override
    public void saveECIESKeyForUser(String user, String key) {
        eciesKeyRepository.save(new EciesKey(user, key));
    }

    @Override
    public String getSequenceHistoryForUser(String user, String shardId) {
        History entity = historyRepository.getHistoryByUserAndShard(user, shardId);
        if (entity != null) {
            return entity.history;
        }
        return null;
    }

    @Override
    public void saveSequenceHistoryForUser(String user, String shardId, String history) {
        historyRepository.save(new History(user, shardId, history));
    }
}
