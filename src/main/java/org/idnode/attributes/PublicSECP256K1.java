package org.idnode.attributes;

import org.ethereum.crypto.ECKey;
import org.idnode.api.dto.Registration;
import org.spongycastle.util.encoders.Base64;

/**
 * A Trust-Net Identity's public ECIES key attribute
 * Created by bhadoria on 2/27/19.
 */


public class PublicSECP256K1 {
    private Integer revision;

    private ECKey key;

    private PublicSECP256K1() {
    }

    public static PublicSECP256K1 fromRegistration(Registration reg) {
        PublicSECP256K1 attr = new PublicSECP256K1();
        attr.revision = reg.getRevision();
        // convert the base64 encodes value to bytes
        attr.key = ECKey.fromPublicOnly(Base64.decode(reg.getValue()));
        return attr;
    }

    public Integer getRevision() {
        return revision;
    }

    public ECKey getKey() {
        return key;
    }
}
