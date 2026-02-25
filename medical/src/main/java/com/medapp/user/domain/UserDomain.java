package com.medapp.user.domain;

import com.medapp.common.exception.NotFoundException;
import com.medapp.user.dto.AddressRequest;
import com.medapp.user.entity.User;
import com.medapp.user.entity.UserAddress;
import com.medapp.user.mapper.UserMapper;
import com.medapp.user.repository.UserAddressRepository;
import com.medapp.user.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UserDomain {

    private final UserRepository userRepository;
    private final UserAddressRepository userAddressRepository;

    public UserDomain(UserRepository userRepository, UserAddressRepository userAddressRepository) {
        this.userRepository = userRepository;
        this.userAddressRepository = userAddressRepository;
    }

    public User getActiveUser(UUID userId) {
        return userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    public UserAddress addAddress(User user, AddressRequest request) {
        if (request.defaultAddress()) {
            userAddressRepository.findByUserIdAndDeletedAtIsNull(user.getId())
                    .forEach(existing -> {
                        if (existing.isDefaultAddress()) {
                            existing.setDefaultAddress(false);
                            userAddressRepository.save(existing);
                        }
                    });
        }

        UserAddress entity = UserMapper.toAddressEntity(request, user);
        return userAddressRepository.save(entity);
    }

    public List<UserAddress> listAddresses(UUID userId) {
        return userAddressRepository.findByUserIdAndDeletedAtIsNull(userId);
    }

    public UserAddress getDefaultAddress(UUID userId) {
        return userAddressRepository.findByUserIdAndIsDefaultAddressTrueAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new NotFoundException("Default address not set"));
    }

    public User save(User user) {
        return userRepository.save(user);
    }
}
