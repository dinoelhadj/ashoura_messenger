package pal.dev.ashourav2;

public class ConversationModel {
    String Name;
    String senderID;
    String lastMessage;
    String conversationID;
    boolean seen;
    Long timestamp;

    public ConversationModel(String conversationID, String senderID, String name, String lastMessage, boolean seen, Long timestamp) {
        Name = name;
        this.senderID = senderID;
        this.lastMessage = lastMessage;
        this.conversationID = conversationID;
        this.seen = seen;
        this.timestamp = timestamp;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getConversationID() {
        return conversationID;
    }

    public void setConversationID(String conversationID) {
        this.conversationID = conversationID;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
