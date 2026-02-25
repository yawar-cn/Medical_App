package com.medapp.common.logging;

public final class SensitiveDataMasker {

    private SensitiveDataMasker() {
    }

    public static String maskToken(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        int visible = Math.min(6, value.length());
        return value.substring(0, visible) + "***";
    }

    public static String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) {
            return "***";
        }
        return "***" + phone.substring(phone.length() - 4);
    }
}
