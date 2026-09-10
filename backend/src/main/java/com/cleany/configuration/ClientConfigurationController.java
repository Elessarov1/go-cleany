package com.cleany.configuration;

import java.util.List;
import java.util.Map;
import java.util.Collections;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ClientConfigurationController {
    private final ClientConfigurationProperties properties;

    @GetMapping("/api/v1/client/configuration")
    public ClientConfigurationResponse configuration() {
        return ClientConfigurationResponse.from(properties);
    }

    @GetMapping(value = "/.well-known/apple-app-site-association",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> appleAppSiteAssociation() {
        requireAppleLinks();
        String appId = properties.appleTeamId() + "." + properties.iosApplicationId();
        return Map.of("applinks", Map.of("apps", Collections.emptyList(), "details", List.of(Map.of(
                "appID", appId,
                "components", List.of(Map.of("/", "/*", "comment", "Loco Place universal links"))
        ))));
    }

    @GetMapping(value = "/.well-known/assetlinks.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> androidAssetLinks() {
        if (properties.androidSha256Fingerprints().isEmpty()) {
            throw new IllegalStateException("Android App Links fingerprints are not configured");
        }
        return List.of(Map.of(
                "relation", List.of("delegate_permission/common.handle_all_urls"),
                "target", Map.of("namespace", "android_app",
                        "package_name", properties.androidApplicationId(),
                        "sha256_cert_fingerprints", properties.androidSha256Fingerprints())
        ));
    }

    private void requireAppleLinks() {
        if (properties.appleTeamId() == null) {
            throw new IllegalStateException("Apple App Links team id is not configured");
        }
    }
}
