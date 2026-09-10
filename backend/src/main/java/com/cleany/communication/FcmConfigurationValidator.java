package com.cleany.communication;

import org.springframework.stereotype.Component;

import com.cleany.authentication.NativeAuthProperties;

@Component
class FcmConfigurationValidator {
    FcmConfigurationValidator(FcmProperties fcm, NativeAuthProperties nativeAuth) {
        if (fcm.enabled() && !nativeAuth.enabled()) {
            throw new IllegalArgumentException("FCM requires native-auth first-party sessions");
        }
    }
}
