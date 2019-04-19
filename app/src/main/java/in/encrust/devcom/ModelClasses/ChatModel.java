package in.encrust.devcom.ModelClasses;

public class ChatModel {
    String senderuid;
    String receiveruid;
    String time;
    String message;

    public ChatModel() {
    }

    public ChatModel(String senderuid, String receiveruid, String time, String message) {
        this.senderuid = senderuid;
        this.receiveruid = receiveruid;
        this.time = time;
        this.message = message;
    }

    public String getSenderuid() {
        return senderuid;
    }

    public void setSenderuid(String senderuid) {
        this.senderuid = senderuid;
    }

    public String getReceiveruid() {
        return receiveruid;
    }

    public void setReceiveruid(String receiveruid) {
        this.receiveruid = receiveruid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
