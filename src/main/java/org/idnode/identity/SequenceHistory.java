package org.idnode.identity;

/**
 * Created by bhadoria on 2/28/19.
 */

class SequenceHistory {
    public static final String FIRST_TX = "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
    public static final long FIRST_SEQ = 0x01;

    String lastTx;

    long nextSeq;

    public SequenceHistory() {
        lastTx = FIRST_TX;
        nextSeq = FIRST_SEQ;
    }

    public SequenceHistory(String lastTx, long nextSeq) {
        this.lastTx = lastTx;
        this.nextSeq = nextSeq;
    }

    public String getLastTx() {
        return lastTx;
    }

    public void update(String lastTx) {
        this.lastTx = lastTx;
        this.nextSeq++;
    }

    public long getNextSeq() {
        return nextSeq;
    }

    @Override
    public String toString() {
        return "SequenceHistory{" +
                "lastTx='" + lastTx + '\'' +
                ", nextSeq=" + nextSeq +
                '}';
    }
}
