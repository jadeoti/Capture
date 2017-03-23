package com.jadeoti.capture.data.model;

import java.io.Serializable;

/**
 * Created by Morph-Deji on 22-Mar-17.
 */

public class BaseModel  implements Serializable{
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public long getDateModified() {
        return dateModified;
    }

    public void setDateModified(long dateModified) {
        this.dateModified = dateModified;
    }

    public long getDateSynced() {
        return dateSynced;
    }

    public void setDateSynced(long dateSynced) {
        this.dateSynced = dateSynced;
    }

    public boolean isSyncFailed() {
        return syncFailed;
    }

    public void setSyncFailed(boolean syncFailed) {
        this.syncFailed = syncFailed;
    }

    // unique identifier for each object
    public long id;

    // timestamp representing first time object was created
    public long dateCreated;

    // timestamp representing last time object was modified
    public long dateModified;

    // timestamp representing time object was pushed to server
    public long dateSynced;

    // boolean representing sync status true = failed, and false = success
    public boolean syncFailed;
}
