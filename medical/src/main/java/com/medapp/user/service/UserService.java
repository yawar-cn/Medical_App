package com.medapp.user.service;

import com.medapp.user.domain.UserDomain;
import com.medapp.user.dto.AddressDto;
import com.medapp.user.dto.AddressRequest;
import com.medapp.user.dto.UpdateProfileRequest;
import com.medapp.user.dto.UserProfileDto;
import com.medapp.common.dto.PageResponse;
import com.medapp.order.dto.OrderDto;
import com.medapp.order.service.OrderService;
import com.medapp.user.entity.User;
import com.medapp.user.mapper.UserMapper;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserDomain userDomain;
    private final OrderService orderService;

    public UserService(UserDomain userDomain, OrderService orderService) {
        this.userDomain = userDomain;
        this.orderService = orderService;
    }

    @Transactional
    public UserProfileDto updateProfile(UUID userId, UpdateProfileRequest request) {
        User user = userDomain.getActiveUser(userId);
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        return UserMapper.toProfile(userDomain.save(user));
    }

    public UserProfileDto getProfile(UUID userId) {
        return UserMapper.toProfile(userDomain.getActiveUser(userId));
    }

    @Transactional
    public AddressDto addAddress(UUID userId, AddressRequest request) {
        User user = userDomain.getActiveUser(userId);
        return UserMapper.toAddressDto(userDomain.addAddress(user, request));
    }

    public List<AddressDto> getAddresses(UUID userId) {
        return userDomain.listAddresses(userId).stream().map(UserMapper::toAddressDto).toList();
    }

    public PageResponse<OrderDto> getOrderHistory(UUID userId, int page, int size) {
        return orderService.history(userId, page, size);
    }
}
