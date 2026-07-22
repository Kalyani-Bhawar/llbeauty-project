package com.llbeauty.dto;

public class MatrimonySearchFilterDTO {

    private Integer minAge;
    private Integer maxAge;
    private String gender;
    private String city;
    private String religion;
    private String caste;
    private String education;
    private String occupation;

    public MatrimonySearchFilterDTO() {
    }

    public MatrimonySearchFilterDTO(Integer minAge, Integer maxAge, String gender, String city,
                                    String religion, String caste, String education, String occupation) {
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.gender = gender;
        this.city = city;
        this.religion = religion;
        this.caste = caste;
        this.education = education;
        this.occupation = occupation;
    }

    public Integer getMinAge() {
        return minAge;
    }

    public void setMinAge(Integer minAge) {
        this.minAge = minAge;
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public String getCaste() {
        return caste;
    }

    public void setCaste(String caste) {
        this.caste = caste;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }
}
