package com.cleany.telegram;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.cleany.base.BaseIntegrationTest;
import com.cleany.customer.CustomerExternalIdentityRepository;
import com.cleany.customer.ExternalIdentityProvider;
import com.cleany.order.CleaningOrderRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class TelegramAuthenticationIntegrationTest extends BaseIntegrationTest {

    private static final String BOT_TOKEN = "123456789:test-token";
    private static final String USER_JSON = """
            {"id":900001,"first_name":"Alex","last_name":"Cleaner","username":"alex"}
            """.strip();

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CleaningOrderRepository orderRepository;

    @Autowired
    private CustomerExternalIdentityRepository identityRepository;

    @BeforeEach
    void cleanDatabase() {
        orderRepository.deleteAll();
    }

    @Test
    void configurationWithoutAuthentication_publicResponseReturned() throws Exception {
        mvc.perform(get("/api/v1/config"))
                .andExpect(status().isOk());
    }

    @Test
    void ordersWithoutAuthentication_unauthorizedResponseReturned() throws Exception {
        mvc.perform(get("/api/v1/orders"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("authentication_required"));
    }

    @Test
    void ordersWithTamperedInitData_unauthorizedResponseReturned() throws Exception {
        String validInitData = TelegramInitDataTestFactory.signed(BOT_TOKEN, Instant.now(), USER_JSON);
        String tamperedInitData = validInitData.replace("Alex", "Mallory");

        mvc.perform(get("/api/v1/orders")
                        .header("Authorization", "tma " + tamperedInitData))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("authentication_required"));
    }

    @Test
    void orderWithValidInitData_verifiedTelegramIdentityStored() throws Exception {
        String initData = TelegramInitDataTestFactory.signed(BOT_TOKEN, Instant.now(), USER_JSON);
        LocalDate requestedDate = LocalDate.now(ZoneId.of("Europe/Istanbul")).plusDays(1);
        String requestBody = """
                {
                  "area": "MAHMUTLAR",
                  "address": "Barbaros Cd. 24",
                  "apartmentType": "TWO_PLUS_ONE",
                  "duplex": false,
                  "cleaningType": "REGULAR",
                  "requestedDate": "%s",
                  "phone": "+90 555 123 45 67",
                  "comment": null
                }
                """.formatted(requestedDate);

        mvc.perform(post("/api/v1/orders")
                        .header("Authorization", "tma " + initData)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("NEW"))
                .andExpect(jsonPath("$.communicationIdentityId").isNumber());

        var orders = orderRepository.findAll();
        Assertions.assertEquals(1, orders.size());
        var order = orders.getFirst();
        var communicationIdentity = identityRepository.findById(order.getCommunicationIdentityId()).orElseThrow();
        Assertions.assertAll(
                () -> Assertions.assertEquals(order.getCustomerId(), communicationIdentity.getCustomerId()),
                () -> Assertions.assertEquals(ExternalIdentityProvider.TELEGRAM, communicationIdentity.getProvider()),
                () -> Assertions.assertEquals("900001", communicationIdentity.getExternalSubject()),
                () -> Assertions.assertEquals("Alex Cleaner", order.getCustomerName()),
                () -> Assertions.assertEquals("+905551234567", order.getPhone()),
                () -> Assertions.assertTrue(order.getCustomerId() > 0),
                () -> Assertions.assertEquals("1100.00", order.getBasePrice().toPlainString()),
                () -> Assertions.assertEquals("165.00", order.getBaseCommission().toPlainString())
        );

        mvc.perform(get("/api/v1/customers/me")
                        .header("Authorization", "tma " + initData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phone").value("+905551234567"));
    }

    @Test
    void orderWithInvalidLocalPhone_validationErrorAndOrderNotStored() throws Exception {
        String initData = TelegramInitDataTestFactory.signed(BOT_TOKEN, Instant.now(), USER_JSON);
        LocalDate requestedDate = LocalDate.now(ZoneId.of("Europe/Istanbul")).plusDays(1);
        String requestBody = """
                {
                  "area": "MAHMUTLAR",
                  "address": "Barbaros Cd. 24",
                  "apartmentType": "TWO_PLUS_ONE",
                  "duplex": false,
                  "cleaningType": "REGULAR",
                  "requestedDate": "%s",
                  "phone": "05551234567",
                  "comment": null
                }
                """.formatted(requestedDate);

        mvc.perform(post("/api/v1/orders")
                        .header("Authorization", "tma " + initData)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("invalid_phone_number"))
                .andExpect(jsonPath("$.fieldErrors.phone").exists());

        Assertions.assertTrue(orderRepository.findAll().isEmpty());
    }
}
