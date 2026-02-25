package com.medapp.user.mapper;

import com.medapp.user.dto.AddressDto;
import com.medapp.user.dto.AddressRequest;
import com.medapp.user.dto.UserProfileDto;
import com.medapp.user.entity.User;
import com.medapp.user.entity.UserAddress;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserProfileDto toProfile(User user) {
        return new UserProfileDto(
                user.getId(),
                user.getPhone(),
                user.getEmail(),
                user.getFullName(),
                user.getRole()
        );
    }

    public static UserAddress toAddressEntity(AddressRequest request, User user) {
        UserAddress address = new UserAddress();
        address.setUser(user);
        address.setLabel(request.label());
        address.setLine1(request.line1());
        address.setLine2(request.line2());
        address.setCity(request.city());
        address.setState(request.state());
        address.setPincode(request.pincode());
        address.setLatitude(request.latitude());
        address.setLongitude(request.longitude());
        address.setDefaultAddress(request.defaultAddress());
        return address;
    }

    public static AddressDto toAddressDto(UserAddress address) {
        return new AddressDto(
                address.getId(),
                address.getLabel(),
                address.getLine1(),
                address.getLine2(),
                address.getCity(),
                address.getState(),
                address.getPincode(),
                address.getLatitude(),
                address.getLongitude(),
                address.isDefaultAddress()
        );
    }
}
