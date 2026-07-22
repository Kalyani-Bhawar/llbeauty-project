package com.llbeauty.dto;

public class MatrimonyPreferenceDTO {

    private Integer minimumAge;
    private Integer maximumAge;
    private String preferredCity;
    private String preferredEducation;
    private String preferredReligion;
    private String preferredOccupation;

    public MatrimonyPreferenceDTO() {
    }

    public MatrimonyPreferenceDTO(Integer minimumAge, Integer maximumAge, String preferredCity,
                                  String preferredEducation, String preferredReligion, String preferredOccupation) {
        this.minimumAge = minimumAge;
        this.maximumAge = maximumAge;
        this.preferredCity = preferredCity;
        this.preferredEducation = preferredEducation;
        this.preferredReligion = preferredReligion;
        this.preferredOccupation = preferredOccupation;
    }

    public Integer getMinimumAge() {
        return minimumAge;
    }

    public void setMinimumAge(Integer minimumAge) {
        this.minimumAge = minimumAge;
    }

    public Integer getMaximumAge() {
        return maximumAge;
    }

    public void setMaximumAge(Integer maximumAge) {
        this.maximumAge = maximumAge;
    }

    public String getPreferredCity() {
        return preferredCity;
    }

    public void setPreferredCity(String preferredCity) {
        this.preferredCity = preferredCity;
    }

    public String getPreferredEducation() {
        return preferredEducation;
    }

    public void setPreferredEducation(String preferredEducation) {
        this.preferredEducation = preferredEducation;
    }

    public String getPreferredReligion() {
        return preferredReligion;
    }

    public void setPreferredReligion(String preferredReligion) {
        this.preferredReligion = preferredReligion;
    }

    public String getPreferredOccupation() {
        return preferredOccupation;
    }

    public void setPreferredOccupation(String preferredOccupation) {
        this.preferredOccupation = preferredOccupation;
    }
}
