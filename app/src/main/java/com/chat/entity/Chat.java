package com.chat.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by m on 21.09.2017.
 */

public class Chat {

    @SerializedName("objectId")
    @Expose
    private String objectId;
    @SerializedName("currentToken")
    @Expose
    private String currentToken;
    @SerializedName("companionToken")
    @Expose
    private String companionToken;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("changesCount")
    @Expose
    private String changesCount;
    @SerializedName("syncSend")
    @Expose
    private boolean syncSend;
    @SerializedName("lastUpdate")
    @Expose
    private long lastUpdate;

    public Chat() {
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getCurrentToken() {
        return currentToken;
    }

    public void setCurrentToken(String currentToken) {
        this.currentToken = currentToken;
    }

    public String getCompanionToken() {
        return companionToken;
    }

    public void setCompanionToken(String companionToken) {
        this.companionToken = companionToken;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSyncSend() {
        return syncSend;
    }

    public void setSyncSend(boolean syncSend) {
        this.syncSend = syncSend;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "objectId='" + objectId + '\'' +
                ", currentToken='" + currentToken + '\'' +
                ", companionToken='" + companionToken + '\'' +
                ", message='" + message + '\'' +
                ", syncSend=" + syncSend +
                ", lastUpdate=" + lastUpdate +
                '}';

    }
//       public  class Data {
//
//            @SerializedName("message")
//            @Expose
//            private String message;
//
//           public String getMessage() {
//               return message;
//           }
//
//           public void setMessage(String message) {
//               this.message = message;
//           }
//       }
}