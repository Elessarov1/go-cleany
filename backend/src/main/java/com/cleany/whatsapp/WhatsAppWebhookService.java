package com.cleany.whatsapp;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.cleany.configuration.WhatsAppProperties;
import com.cleany.customer.CustomerAccountService;
import com.cleany.customer.ExternalIdentityProvider;

@ConditionalOnProperty(prefix = "whatsapp", name = "enabled", havingValue = "true")
@Service
public class WhatsAppWebhookService {

    private static final Logger log = LoggerFactory.getLogger(WhatsAppWebhookService.class);

    private final CustomerAccountService customerAccountService;
    private final WhatsAppCloudApiClient cloudApiClient;
    private final String businessAccountId;
    private final String phoneNumberId;
    private final boolean testReplyEnabled;

    public WhatsAppWebhookService(
            CustomerAccountService customerAccountService,
            WhatsAppCloudApiClient cloudApiClient,
            WhatsAppProperties properties
    ) {
        this.customerAccountService = customerAccountService;
        this.cloudApiClient = cloudApiClient;
        businessAccountId = properties.businessAccountId();
        phoneNumberId = properties.phoneNumberId();
        testReplyEnabled = properties.testReplyEnabled();
    }

    public void handle(WhatsAppWebhookUpdate update) {
        if (update == null || !"whatsapp_business_account".equals(update.object())) {
            return;
        }
        update.entry().stream()
                .filter(entry -> businessAccountId.equals(entry.id()))
                .flatMap(entry -> entry.changes().stream())
                .filter(change -> "messages".equals(change.field()))
                .map(WhatsAppWebhookUpdate.Change::value)
                .filter(this::isExpectedPhoneNumber)
                .forEach(this::handleValue);
    }

    private boolean isExpectedPhoneNumber(WhatsAppWebhookUpdate.Value value) {
        return value != null
                && value.metadata() != null
                && phoneNumberId.equals(value.metadata().phoneNumberId());
    }

    private void handleValue(WhatsAppWebhookUpdate.Value value) {
        Map<String, WhatsAppWebhookUpdate.Contact> contacts = value.contacts().stream()
                .filter(contact -> contact.waId() != null)
                .collect(Collectors.toMap(
                        WhatsAppWebhookUpdate.Contact::waId,
                        Function.identity(),
                        (first, ignored) -> first
                ));
        value.messages().stream()
                .filter(Objects::nonNull)
                .forEach(message -> handleMessage(message, contacts.get(message.from())));
    }

    private void handleMessage(
            WhatsAppWebhookUpdate.Message message,
            WhatsAppWebhookUpdate.Contact contact
    ) {
        if (message == null || message.from() == null || !message.from().matches("[0-9]{7,15}")) {
            log.warn("Ignoring WhatsApp webhook message with invalid sender");
            return;
        }
        String displayName = contact == null || contact.profile() == null
                ? null
                : contact.profile().name();
        customerAccountService.savePhoneForExternalIdentity(
                ExternalIdentityProvider.WHATSAPP,
                message.from(),
                null,
                displayName,
                null,
                "+" + message.from()
        );
        if (testReplyEnabled && isPing(message)) {
            cloudApiClient.sendText(message.from(), "go-cleany WhatsApp Cloud API: pong");
        }
    }

    private static boolean isPing(WhatsAppWebhookUpdate.Message message) {
        if (!"text".equals(message.type()) || message.text() == null || message.text().body() == null) {
            return false;
        }
        String body = message.text().body().trim();
        return "ping".equalsIgnoreCase(body) || "/ping".equalsIgnoreCase(body);
    }
}
