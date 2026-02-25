package com.medapp.pharmacy.mapper;

import com.medapp.pharmacy.dto.PharmacyCreateRequest;
import com.medapp.pharmacy.dto.PharmacyDto;
import com.medapp.pharmacy.dto.PharmacyUpdateRequest;
import com.medapp.pharmacy.entity.Pharmacy;
import com.medapp.pharmacy.entity.PharmacyStatus;
import com.medapp.user.entity.User;

public final class PharmacyMapper {

    private PharmacyMapper() {
    }

    public static Pharmacy toEntity(PharmacyCreateRequest request, User owner) {
        Pharmacy pharmacy = new Pharmacy();
        pharmacy.setOwnerUser(owner);
        pharmacy.setStoreName(request.storeName());
        pharmacy.setLicenseNumber(request.licenseNumber());
        pharmacy.setKycDocumentPath(request.kycDocumentPath());
        pharmacy.setAddress(request.address());
        pharmacy.setLatitude(request.latitude());
        pharmacy.setLongitude(request.longitude());
        pharmacy.setOpensAt(request.opensAt());
        pharmacy.setClosesAt(request.closesAt());
        pharmacy.setStatus(PharmacyStatus.PENDING);
        return pharmacy;
    }

    public static void updateEntity(Pharmacy pharmacy, PharmacyUpdateRequest request) {
        pharmacy.setStoreName(request.storeName());
        pharmacy.setAddress(request.address());
        pharmacy.setLatitude(request.latitude());
        pharmacy.setLongitude(request.longitude());
        pharmacy.setOpensAt(request.opensAt());
        pharmacy.setClosesAt(request.closesAt());
    }

    public static PharmacyDto toDto(Pharmacy pharmacy) {
        return new PharmacyDto(
                pharmacy.getId(),
                pharmacy.getOwnerUser().getId(),
                pharmacy.getStoreName(),
                pharmacy.getLicenseNumber(),
                pharmacy.getAddress(),
                pharmacy.getLatitude(),
                pharmacy.getLongitude(),
                pharmacy.getOpensAt(),
                pharmacy.getClosesAt(),
                pharmacy.getStatus(),
                pharmacy.getRejectionReason()
        );
    }
}
