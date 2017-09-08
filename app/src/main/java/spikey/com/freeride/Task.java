package spikey.com.freeride;

public class Task {

    private String taskId;
    private double startLat;
    private double startLong;
    private double endLat;
    private double endLong;
    private String creationLocalDateTime;
    private String expirationLocalDateTime;
    private String title;
    private String description;
    private String state;
    private String user;
    private double incentive;

    public Task(String taskId,
                double startLat, double startLong,
                double endLat, double endLong,
                String creationLocalDateTime, String expirationLocalDateTime,
                String title, String description, String state, String user, double incentive) {
        this.taskId = taskId;
        this.startLat = startLat;
        this.startLong = startLong;
        this.endLat = endLat;
        this.endLong = endLong;
        this.creationLocalDateTime = creationLocalDateTime;
        this.expirationLocalDateTime = expirationLocalDateTime;
        this.title = title;
        this.description = description;
        this.state = state;
        this.user = user;
        this.incentive = incentive;
    }

    public Task() {

    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public double getStartLat() {
        return startLat;
    }

    public void setStartLat(double startLat) {
        this.startLat = startLat;
    }

    public double getStartLong() {
        return startLong;
    }

    public void setStartLong(double startLong) {
        this.startLong = startLong;
    }

    public double getEndLat() {
        return endLat;
    }

    public void setEndLat(double endLat) {
        this.endLat = endLat;
    }

    public double getEndLong() {
        return endLong;
    }

    public void setEndLong(double endLong) {
        this.endLong = endLong;
    }

    public String getCreationLocalDateTime() {
        return creationLocalDateTime;
    }

    public void setCreationLocalDateTime(String creationLocalDateTime) {
        this.creationLocalDateTime = creationLocalDateTime;
    }

    public String getExpirationLocalDateTime() {
        return expirationLocalDateTime;
    }

    public void setExpirationLocalDateTime(String expirationLocalDateTime) {
        this.expirationLocalDateTime = expirationLocalDateTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public double getIncentive() {
        return incentive;
    }

    public void setIncentive(double incentive) {
        this.incentive = incentive;
    }
//
//    public LatLng getStartLatLng(){
//        return new LatLng(startLat, startLong);
//    }
//
//    public LatLng getEndLatLng(){
//        return new LatLng(endLat, endLong);
//    }
}
