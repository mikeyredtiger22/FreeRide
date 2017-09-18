package spikey.com.freeride;
import com.google.maps.model.DirectionsLeg;
import org.joda.time.LocalDateTime;

public class Task {

    private String taskId;
    private Double startLat;
    private Double startLong;
    private Double endLat;
    private Double endLong;
    private LocalDateTime creationLocalDateTime;
    private LocalDateTime expirationLocalDateTime;
    private String title;
    private String description;
    private String state;
    private String user;
    private String directionsPath;
    private DirectionsLeg routeData;
    private Integer incentive;

    public Task(Double startLat, Double startLong,
                Double endLat, Double endLong,
                LocalDateTime creationLocalDateTime, LocalDateTime expirationLocalDateTime,
                String title, String description, String state, String user,
                String directionsPath, DirectionsLeg routeData, Integer incentive) {
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
        this.directionsPath = directionsPath;
        this.routeData = routeData;
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

    public Double getStartLat() {
        return startLat;
    }

    public void setStartLat(Double startLat) {
        this.startLat = startLat;
    }

    public Double getStartLong() {
        return startLong;
    }

    public void setStartLong(Double startLong) {
        this.startLong = startLong;
    }

    public Double getEndLat() {
        return endLat;
    }

    public void setEndLat(Double endLat) {
        this.endLat = endLat;
    }

    public Double getEndLong() {
        return endLong;
    }

    public void setEndLong(Double endLong) {
        this.endLong = endLong;
    }

    public LocalDateTime getCreationLocalDateTime() {
        return creationLocalDateTime;
    }

    public void setCreationLocalDateTime(LocalDateTime creationLocalDateTime) {
        this.creationLocalDateTime = creationLocalDateTime;
    }

    public LocalDateTime getExpirationLocalDateTime() {
        return expirationLocalDateTime;
    }

    public void setExpirationLocalDateTime(LocalDateTime expirationLocalDateTime) {
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

    public DirectionsLeg getRouteData() {
        return routeData;
    }

    public void setRouteData(DirectionsLeg routeData) {
        this.routeData = routeData;
    }

    public String getDirectionsPath() {
        return directionsPath;
    }

    public void setDirectionsPath(String directionsPath) {
        this.directionsPath = directionsPath;
    }

    public Integer getIncentive() {
        return incentive;
    }

    public void setIncentive(Integer incentive) {
        this.incentive = incentive;
    }
}
