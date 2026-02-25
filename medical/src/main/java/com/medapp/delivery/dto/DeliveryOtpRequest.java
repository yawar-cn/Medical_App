package com.medapp.delivery.dto;

import com.medapp.delivery.validation.ValidDeliveryOtp;

public record DeliveryOtpRequest(
        @ValidDeliveryOtp String otp
) {
}
