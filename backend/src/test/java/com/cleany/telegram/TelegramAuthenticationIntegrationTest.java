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
                .andExpect(jsonPath("$.status").value("NEW"));

        var orders = orderRepository.findAll();
        Assertions.assertEquals(1, orders.size());
        Assertions.assertEquals(900001L, orders.getFirst().getTelegramUserId());
        Assertions.assertEquals("alex", orders.getFirst().getTelegramUsername());
        Assertions.assertEquals("Alex Cleaner", orders.getFirst().getCustomerName());
    }
}
