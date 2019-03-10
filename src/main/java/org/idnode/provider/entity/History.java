package org.idnode.provider.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by bhadoria on 2/28/19.
 */

//@Entity(primaryKeys = {"user", "shard"}, tableName = "history")
@IdClass(History.HistoryPk.class)
@Entity
@Table(name = "history")
public class History {
    @Id
    @Column(name="user")
    public String user;

    @Id
    @Column(name="shard")
    public String shard;

    @Column(name="history")
    public String history;

    public History(String user, String shard, String history) {
        this.user = user;
        this.shard = shard;
        this.history = history;
    }

    public static class HistoryPk implements Serializable {
        public String user;

        public String shard;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            HistoryPk historyPk = (HistoryPk) o;

            if (!user.equals(historyPk.user)) return false;
            return shard.equals(historyPk.shard);
        }

        @Override
        public int hashCode() {
            int result = user.hashCode();
            result = 31 * result + shard.hashCode();
            return result;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getShard() {
            return shard;
        }

        public void setShard(String shard) {
            this.shard = shard;
        }
    }
}
