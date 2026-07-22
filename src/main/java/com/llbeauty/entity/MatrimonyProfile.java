package com.llbeauty.entity;

import com.llbeauty.enums.ProfileStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "matrimony_profiles", indexes = {
    @Index(name = "idx_mat_profile_user", columnList = "user_id"),
    @Index(name = "idx_mat_profile_status", columnList = "status"),
    @Index(name = "idx_mat_profile_city", columnList = "city")
})
public class MatrimonyProfile extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Personal Information
    @Column(name = "full_name")
    private String fullName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    private String gender;
    private Double height;

    @Column(name = "mother_tongue")
    private String motherTongue;

    private String city;
    private String state;
    private String country;

    // Religion
    private String religion;
    private String caste;

    @Column(name = "sub_caste")
    private String subCaste;

    // Career
    private String education;
    private String occupation;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "annual_income", precision = 15, scale = 2)
    private BigDecimal annualIncome;

    // Family
    @Column(name = "father_occupation")
    private String fatherOccupation;

    @Column(name = "mother_occupation")
    private String motherOccupation;

    @Column(name = "brother_count")
    private Integer brotherCount;

    @Column(name = "sister_count")
    private Integer sisterCount;

    @Column(name = "family_type")
    private String familyType;

    // About
    @Column(name = "about_me", length = 1000)
    private String aboutMe;

    // Photos
    @Column(name = "profile_photo")
    private String profilePhoto;

    // Status
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private ProfileStatus status;

    public MatrimonyProfile() {
    }

    public MatrimonyProfile(User user, String fullName, LocalDate dateOfBirth, String gender, Double height,
                            String motherTongue, String city, String state, String country, String religion,
                            String caste, String subCaste, String education, String occupation, String companyName,
                            BigDecimal annualIncome, String fatherOccupation, String motherOccupation,
                            Integer brotherCount, Integer sisterCount, String familyType, String aboutMe,
                            String profilePhoto, ProfileStatus status) {
        this.user = user;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.height = height;
        this.motherTongue = motherTongue;
        this.city = city;
        this.state = state;
        this.country = country;
        this.religion = religion;
        this.caste = caste;
        this.subCaste = subCaste;
        this.education = education;
        this.occupation = occupation;
        this.companyName = companyName;
        this.annualIncome = annualIncome;
        this.fatherOccupation = fatherOccupation;
        this.motherOccupation = motherOccupation;
        this.brotherCount = brotherCount;
        this.sisterCount = sisterCount;
        this.familyType = familyType;
        this.aboutMe = aboutMe;
        this.profilePhoto = profilePhoto;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public String getMotherTongue() {
        return motherTongue;
    }

    public void setMotherTongue(String motherTongue) {
        this.motherTongue = motherTongue;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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

    public String getSubCaste() {
        return subCaste;
    }

    public void setSubCaste(String subCaste) {
        this.subCaste = subCaste;
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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public BigDecimal getAnnualIncome() {
        return annualIncome;
    }

    public void setAnnualIncome(BigDecimal annualIncome) {
        this.annualIncome = annualIncome;
    }

    public String getFatherOccupation() {
        return fatherOccupation;
    }

    public void setFatherOccupation(String fatherOccupation) {
        this.fatherOccupation = fatherOccupation;
    }

    public String getMotherOccupation() {
        return motherOccupation;
    }

    public void setMotherOccupation(String motherOccupation) {
        this.motherOccupation = motherOccupation;
    }

    public Integer getBrotherCount() {
        return brotherCount;
    }

    public void setBrotherCount(Integer brotherCount) {
        this.brotherCount = brotherCount;
    }

    public Integer getSisterCount() {
        return sisterCount;
    }

    public void setSisterCount(Integer sisterCount) {
        this.sisterCount = sisterCount;
    }

    public String getFamilyType() {
        return familyType;
    }

    public void setFamilyType(String familyType) {
        this.familyType = familyType;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public ProfileStatus getStatus() {
        return status;
    }

    public void setStatus(ProfileStatus status) {
        this.status = status;
    }
}
