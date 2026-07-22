package com.llbeauty.dto;

import com.llbeauty.entity.MatrimonyProfile;

public class MatrimonyProfileMapper {

    public static MatrimonyProfileDTO toDTO(MatrimonyProfile profile) {
        if (profile == null) {
            return null;
        }
        MatrimonyProfileDTO dto = new MatrimonyProfileDTO();
        dto.setId(profile.getId());
        dto.setFullName(profile.getFullName());
        dto.setDateOfBirth(profile.getDateOfBirth());
        dto.setGender(profile.getGender());
        dto.setHeight(profile.getHeight());
        dto.setMotherTongue(profile.getMotherTongue());
        dto.setCity(profile.getCity());
        dto.setState(profile.getState());
        dto.setCountry(profile.getCountry());
        dto.setReligion(profile.getReligion());
        dto.setCaste(profile.getCaste());
        dto.setSubCaste(profile.getSubCaste());
        dto.setEducation(profile.getEducation());
        dto.setOccupation(profile.getOccupation());
        dto.setCompanyName(profile.getCompanyName());
        dto.setAnnualIncome(profile.getAnnualIncome());
        dto.setFatherOccupation(profile.getFatherOccupation());
        dto.setMotherOccupation(profile.getMotherOccupation());
        dto.setBrotherCount(profile.getBrotherCount());
        dto.setSisterCount(profile.getSisterCount());
        dto.setFamilyType(profile.getFamilyType());
        dto.setAboutMe(profile.getAboutMe());
        dto.setProfilePhoto(profile.getProfilePhoto());
        return dto;
    }

    public static MatrimonyProfile toEntity(MatrimonyProfileDTO dto) {
        if (dto == null) {
            return null;
        }
        MatrimonyProfile profile = new MatrimonyProfile();
        profile.setId(dto.getId());
        profile.setFullName(dto.getFullName());
        profile.setDateOfBirth(dto.getDateOfBirth());
        profile.setGender(dto.getGender());
        profile.setHeight(dto.getHeight());
        profile.setMotherTongue(dto.getMotherTongue());
        profile.setCity(dto.getCity());
        profile.setState(dto.getState());
        profile.setCountry(dto.getCountry());
        profile.setReligion(dto.getReligion());
        profile.setCaste(dto.getCaste());
        profile.setSubCaste(dto.getSubCaste());
        profile.setEducation(dto.getEducation());
        profile.setOccupation(dto.getOccupation());
        profile.setCompanyName(dto.getCompanyName());
        profile.setAnnualIncome(dto.getAnnualIncome());
        profile.setFatherOccupation(dto.getFatherOccupation());
        profile.setMotherOccupation(dto.getMotherOccupation());
        profile.setBrotherCount(dto.getBrotherCount());
        profile.setSisterCount(dto.getSisterCount());
        profile.setFamilyType(dto.getFamilyType());
        profile.setAboutMe(dto.getAboutMe());
        profile.setProfilePhoto(dto.getProfilePhoto());
        return profile;
    }
}
