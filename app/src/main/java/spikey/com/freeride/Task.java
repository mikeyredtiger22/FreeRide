package spikey.com.freeride;

import org.joda.time.LocalDateTime;

public class Task {

    private String taskId;
    private Integer locationCount;
    private Boolean areLocationsOrdered;
    private Double[] locationLats;
    private Double[] locationLongs;
    private String[] locationInstructions;
    private String[] locationAddresses;
    private Boolean[] verified;
    private LocalDateTime creationLocalDateTime;
    private LocalDateTime expirationLocalDateTime;
    private String title;
    private String description;
    private String state;
    private String user;
    private Integer incentive;
    private Boolean hasDirections;
    private String directionsPath;
    private String directionsDistance;
    private String directionsDuration;

    public Task(Integer locationCount, Boolean areLocationsOrdered, Double[] locationLats, Double[] locationLongs,
                String[] locationInstructions, String[] locationAddresses, Boolean[] verified,
                LocalDateTime creationLocalDateTime, LocalDateTime expirationLocalDateTime,
                String title, String description, String state, String user, Integer incentive,
                Boolean hasDirections, String directionsPath, String directionsDistance, String directionsDuration) {
        this.locationCount = locationCount;
        this.areLocationsOrdered = areLocationsOrdered;
        this.locationLats = locationLats;
        this.locationLongs = locationLongs;
        this.locationInstructions = locationInstructions;
        this.locationAddresses = locationAddresses;
        this.verified = verified;
        this.creationLocalDateTime = creationLocalDateTime;
        this.expirationLocalDateTime = expirationLocalDateTime;
        this.title = title;
        this.description = description;
        this.state = state;
        this.user = user;
        this.incentive = incentive;
        this.hasDirections = hasDirections;
        this.directionsPath = directionsPath;
        this.directionsDistance = directionsDistance;
        this.directionsDuration = directionsDuration;
    }

    public Task() {

    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Integer getLocationCount() {
        return locationCount;
    }

    public void setLocationCount(Integer locationCount) {
        this.locationCount = locationCount;
    }

    public Boolean getAreLocationsOrdered() {
        return areLocationsOrdered;
    }

    public void setAreLocationsOrdered(Boolean areLocationsOrdered) {
        this.areLocationsOrdered = areLocationsOrdered;
    }

    public Double[] getLocationLats() {
        return locationLats;
    }

    public void setLocationLats(Double[] locationLats) {
        this.locationLats = locationLats;
    }

    public Double[] getLocationLongs() {
        return locationLongs;
    }

    public void setLocationLongs(Double[] locationLongs) {
        this.locationLongs = locationLongs;
    }

    public String[] getLocationInstructions() {
        return locationInstructions;
    }

    public void setLocationInstructions(String[] locationInstructions) {
        this.locationInstructions = locationInstructions;
    }

    public String[] getLocationAddresses() {
        return locationAddresses;
    }

    public void setLocationAddresses(String[] locationAddresses) {
        this.locationAddresses = locationAddresses;
    }

    public Boolean[] getVerified() {
        return verified;
    }

    public void setVerified(Boolean[] verified) {
        this.verified = verified;
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

    public Integer getIncentive() {
        return incentive;
    }

    public void setIncentive(Integer incentive) {
        this.incentive = incentive;
    }

    public Boolean getHasDirections() {
        return hasDirections;
    }

    public void setHasDirections(Boolean hasDirections) {
        this.hasDirections = hasDirections;
    }

    public String getDirectionsPath() {
        return directionsPath;
    }

    public void setDirectionsPath(String directionsPath) {
        this.directionsPath = directionsPath;
    }

    public String getDirectionsDistance() {
        return directionsDistance;
    }

    public void setDirectionsDistance(String directionsDistance) {
        this.directionsDistance = directionsDistance;
    }

    public String getDirectionsDuration() {
        return directionsDuration;
    }

    public void setDirectionsDuration(String directionsDuration) {
        this.directionsDuration = directionsDuration;
    }
}
