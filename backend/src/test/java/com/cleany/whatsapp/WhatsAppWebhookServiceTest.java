package com.cleany.whatsapp;

import java.net.URI;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.cleany.configuration.WhatsAppProperties;
import com.cleany.customer.CustomerAccountService;
import com.cleany.customer.ExternalIdentityProvider;

class WhatsAppWebhookServiceTest {

    @Test
    void pingFromExpectedWabaAndPhone_resolvesCustomerAndReplies() {
        CustomerAccountService accountService = Mockito.mock(CustomerAccountService.class);
        WhatsAppCloudApiClient cloudApiClient = Mockito.mock(WhatsAppCloudApiClient.class);
        var service = new WhatsAppWebhookService(accountService, cloudApiClient, properties(true));

        service.handle(update("1580401900215347", "1239590005912301", "/ping"));

        Mockito.verify(accountService).savePhoneForExternalIdentity(
                ExternalIdentityProvider.WHATSAPP,
                "905551234567",
                null,
                "Alex",
                null,
                "+905551234567"
        );
        Mockito.verify(cloudApiClient).sendText(
                "905551234567",
                "go-cleany WhatsApp Cloud API: pong"
        );
    }

    @Test
    void eventForDifferentWabaOrPhone_ignored() {
        CustomerAccountService accountService = Mockito.mock(CustomerAccountService.class);
        WhatsAppCloudApiClient cloudApiClient = Mockito.mock(WhatsAppCloudApiClient.class);
        var service = new WhatsAppWebhookService(accountService, cloudApiClient, properties(true));

        service.handle(update("999", "1239590005912301", "/ping"));
        service.handle(update("1580401900215347", "999", "/ping"));

        Mockito.verifyNoInteractions(accountService, cloudApiClient);
    }

    @Test
    void testReplyDisabled_stillResolvesCustomerWithoutSending() {
        CustomerAccountService accountService = Mockito.mock(CustomerAccountService.class);
        WhatsAppCloudApiClient cloudApiClient = Mockito.mock(WhatsAppCloudApiClient.class);
        var service = new WhatsAppWebhookService(accountService, cloudApiClient, properties(false));

        service.handle(update("1580401900215347", "1239590005912301", "ping"));

        Mockito.verify(accountService).savePhoneForExternalIdentity(
                ExternalIdentityProvider.WHATSAPP,
                "905551234567",
                null,
                "Alex",
                null,
                "+905551234567"
        );
        Mockito.verifyNoInteractions(cloudApiClient);
    }

    private static WhatsAppWebhookUpdate update(String wabaId, String phoneId, String text) {
        var contact = new WhatsAppWebhookUpdate.Contact(
                new WhatsAppWebhookUpdate.Profile("Alex"),
                "905551234567"
        );
        var message = new WhatsAppWebhookUpdate.Message(
                "905551234567",
                "wamid.test",
                "1770000000",
                "text",
                new WhatsAppWebhookUpdate.Text(text)
        );
        var value = new WhatsAppWebhookUpdate.Value(
                new WhatsAppWebhookUpdate.Metadata("+1 555-662-9055", phoneId),
                List.of(contact),
                List.of(message)
        );
        return new WhatsAppWebhookUpdate(
                "whatsapp_business_account",
                List.of(new WhatsAppWebhookUpdate.Entry(
                        wabaId,
                        List.of(new WhatsAppWebhookUpdate.Change("messages", value))
                ))
        );
    }

    private static WhatsAppProperties properties(boolean testReplyEnabled) {
        return new WhatsAppProperties(
                true,
                URI.create("https://graph.facebook.com"),
                "v25.0",
                "1070289752200337",
                "1438307131692197",
                "1580401900215347",
                "1239590005912301",
                "access-token",
                "app-secret",
                "test_verify_token_1234567890123456",
                testReplyEnabled
        );
    }
}
