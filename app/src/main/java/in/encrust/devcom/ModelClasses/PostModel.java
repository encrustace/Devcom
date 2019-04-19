package in.encrust.devcom.ModelClasses;


public class PostModel {
    public String senderuid;
    public String receiveruid;
    public String time;
    public String postkey;
    public String postname;
    public String postdescription;
    public String postcatagory;
    public String postduration;
    public String postbudget;
    public String budgettag;
    public String workfile;

    public PostModel() {

    }

    public PostModel(String senderuid, String receiveruid, String time, String postkey
            , String postname, String postdescription, String postcatagory, String postduration
            , String postbudget, String budgettag, String workfile) {
        this.senderuid = senderuid;
        this.receiveruid = receiveruid;
        this.time = time;
        this.postkey = postkey;
        this.postname = postname;
        this.postdescription = postdescription;
        this.postcatagory = postcatagory;
        this.postduration = postduration;
        this.postbudget = postbudget;
        this.budgettag = budgettag;
        this.workfile = workfile;
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

    public String getPostkey() {
        return postkey;
    }

    public void setPostkey(String postkey) {
        this.postkey = postkey;
    }

    public String getPostname() {
        return postname;
    }

    public void setPostname(String postname) {
        this.postname = postname;
    }

    public String getPostdescription() {
        return postdescription;
    }

    public void setPostdescription(String postdescription) {
        this.postdescription = postdescription;
    }

    public String getPostcatagory() {
        return postcatagory;
    }

    public void setPostcatagory(String postcatagory) {
        this.postcatagory = postcatagory;
    }

    public String getPostduration() {
        return postduration;
    }

    public void setPostduration(String postduration) {
        this.postduration = postduration;
    }

    public String getPostbudget() {
        return postbudget;
    }

    public void setPostbudget(String postbudget) {
        this.postbudget = postbudget;
    }

    public String getBudgettag() {
        return budgettag;
    }

    public void setBudgettag(String budgettag) {
        this.budgettag = budgettag;
    }

    public String getWorkfile() {
        return workfile;
    }

    public void setWorkfile(String workfile) {
        this.workfile = workfile;
    }
}
