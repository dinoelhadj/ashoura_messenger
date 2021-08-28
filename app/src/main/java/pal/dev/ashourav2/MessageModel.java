package pal.dev.ashourav2;

public class MessageModel {
    String senderID;
    Long timestamp;
    Boolean seen;
    Long seenTimeStamp;
    String value;

    public MessageModel(String senderID, Long timestamp, Boolean seen, Long seenTimeStamp, String value) {
        this.senderID = senderID;
        this.timestamp = timestamp;
        this.seen = seen;
        this.seenTimeStamp = seenTimeStamp;
        this.value = value;
    }

    public MessageModel(String senderID, Long timestamp, String value) {
        this.senderID = senderID;
        this.timestamp = timestamp;
        this.value = value;
        seen = false;
        seenTimeStamp = null;
    }

    public MessageModel(String senderID, String value) {
        this.senderID = senderID;
        this.timestamp = System.currentTimeMillis();
        this.value = value;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    public Long getSeenTimeStamp() {
        return seenTimeStamp;
    }

    public void setSeenTimeStamp(Long seenTimeStamp) {
        this.seenTimeStamp = seenTimeStamp;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

