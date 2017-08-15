package spikey.com.freeride;

public class Task {

    private double startLocationLatitude;
    private double startLocationLongitude;
    private double endLocationLatitude;
    private double endLocationLongitude;
    private String creationLocalDateTime;
    private String expirationLocalDateTime;
    private String title;
    private String description;
    private String state;
    private String user;
    private double incentive;

    public Task(double startLocationLatitude, double startLocationLongitude,
                double endLocationLatitude, double endLocationLongitude,
                String creationLocalDateTime, String expirationLocalDateTime,
                String title, String description, String state, String user, double incentive) {
        this.startLocationLatitude = startLocationLatitude;
        this.startLocationLongitude = startLocationLongitude;
        this.endLocationLatitude = endLocationLatitude;
        this.endLocationLongitude = endLocationLongitude;
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

    public double getStartLocationLatitude() {
        return startLocationLatitude;
    }

    public void setStartLocationLatitude(double startLocationLatitude) {
        this.startLocationLatitude = startLocationLatitude;
    }

    public double getStartLocationLongitude() {
        return startLocationLongitude;
    }

    public void setStartLocationLongitude(double startLocationLongitude) {
        this.startLocationLongitude = startLocationLongitude;
    }

    public double getEndLocationLatitude() {
        return endLocationLatitude;
    }

    public void setEndLocationLatitude(double endLocationLatitude) {
        this.endLocationLatitude = endLocationLatitude;
    }

    public double getEndLocationLongitude() {
        return endLocationLongitude;
    }

    public void setEndLocationLongitude(double endLocationLongitude) {
        this.endLocationLongitude = endLocationLongitude;
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

    @Override
    public String toString() {
        return String.valueOf(
                startLocationLatitude + ", " +
                startLocationLongitude + ", " +
                endLocationLatitude + ", " +
                endLocationLongitude + ", " +
                creationLocalDateTime + ", " +
                expirationLocalDateTime + ", " +
                title + ", " +
                description + ", " +
                state  + ", " +
                user  + ", " +
                incentive);
    }
}
