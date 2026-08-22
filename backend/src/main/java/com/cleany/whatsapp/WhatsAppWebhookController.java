package com.cleany.whatsapp;

import tools.jackson.databind.ObjectMapper;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@ConditionalOnProperty(prefix = "whatsapp", name = "enabled", havingValue = "true")
@RestController
@RequestMapping("/api/v1/whatsapp/webhook")
public class WhatsAppWebhookController {

    static final String SIGNATURE_HEADER = "X-Hub-Signature-256";

    private final ObjectMapper objectMapper;
    private final WhatsAppWebhookAuthenticator authenticator;
    private final WhatsAppWebhookService webhookService;

    public WhatsAppWebhookController(
            ObjectMapper objectMapper,
            WhatsAppWebhookAuthenticator authenticator,
            WhatsAppWebhookService webhookService
    ) {
        this.objectMapper = objectMapper;
        this.authenticator = authenticator;
        this.webhookService = webhookService;
    }

    @GetMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> verify(
            @RequestParam(name = "hub.mode", required = false) String mode,
            @RequestParam(name = "hub.verify_token", required = false) String verifyToken,
            @RequestParam(name = "hub.challenge", required = false) String challenge
    ) {
        if (challenge == null
                || challenge.isBlank()
                || !authenticator.isValidVerificationRequest(mode, verifyToken)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(challenge);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> receive(
            @RequestHeader(name = SIGNATURE_HEADER, required = false) String signature,
            @RequestBody byte[] payload
    ) {
        authenticator.validateSignature(signature, payload);
        webhookService.handle(objectMapper.readValue(payload, WhatsAppWebhookUpdate.class));
        return ResponseEntity.ok().build();
    }
}
