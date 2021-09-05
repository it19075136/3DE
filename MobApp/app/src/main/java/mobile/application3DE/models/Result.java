package mobile.application3DE.models;

public class Result {

    private String type,timestamp,SingleTask,dualTask,impairement,difference;

    public Result(String type, String timestamp, String singleTask, String dualTask, String impairement, String difference) {
        this.type = type;
        this.timestamp = timestamp;
        SingleTask = singleTask;
        this.dualTask = dualTask;
        this.impairement = impairement;
        this.difference = difference;
    }

    public String getSingleTask() {
        return SingleTask;
    }

    public void setSingleTask(String singleTask) {
        SingleTask = singleTask;
    }

    public String getDualTask() {
        return dualTask;
    }

    public void setDualTask(String dualTask) {
        this.dualTask = dualTask;
    }

    public String getImpairement() {
        return impairement;
    }

    public void setImpairement(String impairement) {
        this.impairement = impairement;
    }

    public String getDifference() {
        return difference;
    }

    public void setDifference(String difference) {
        this.difference = difference;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
