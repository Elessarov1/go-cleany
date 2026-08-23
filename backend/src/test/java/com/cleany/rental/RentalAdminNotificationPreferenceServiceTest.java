package com.cleany.rental;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.lang.reflect.RecordComponent;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.cleany.admin.AdminAccessService;
import com.cleany.admin.AdminNotAuthorizedException;

class RentalAdminNotificationPreferenceServiceTest {

    private static final Instant NOW = Instant.parse("2026-08-23T10:15:30Z");

    private final AdminAccessService accessService = Mockito.mock(AdminAccessService.class);
    private final RentalAdminNotificationPreferenceRepository repository =
            Mockito.mock(RentalAdminNotificationPreferenceRepository.class);
    private final AtomicLong currentAdmin = new AtomicLong(1001L);
    private final Map<Long, RentalAdminNotificationPreference> preferences =
            new ConcurrentHashMap<>();
    private final RentalAdminNotificationPreferenceService service =
            new RentalAdminNotificationPreferenceService(
                    accessService,
                    repository,
                    Clock.fixed(NOW, ZoneOffset.UTC)
            );

    @BeforeEach
    void configureRepository() {
        Mockito.when(accessService.requireCurrentAdmin()).thenAnswer(ignored -> currentAdmin.get());
        Mockito.when(repository.findById(Mockito.anyLong())).thenAnswer(invocation ->
                Optional.ofNullable(preferences.get(invocation.getArgument(0)))
        );
        Mockito.when(repository.save(Mockito.any())).thenAnswer(invocation -> {
            RentalAdminNotificationPreference preference = invocation.getArgument(0);
            preferences.put(preference.getAdminId(), preference);
            return preference;
        });
        Mockito.when(repository.findAllByAdminIdIn(Mockito.anyCollection())).thenAnswer(invocation -> {
            List<Long> ids = List.copyOf(invocation.getArgument(0));
            return ids.stream().map(preferences::get).filter(java.util.Objects::nonNull).toList();
        });
    }

    @Test
    void missingPreference_defaultsToEnabled() {
        Assertions.assertTrue(service.current().telegramEnabled());
        Assertions.assertEquals(List.of(1001L, 1002L), service.enabledAdminIds(
                List.of(1001L, 1002L)
        ));
    }

    @Test
    void eachAdmin_updatesOnlyOwnPreference() {
        service.update(new UpdateRentalAdminNotificationPreferenceRequest(false));
        currentAdmin.set(1002L);
        service.update(new UpdateRentalAdminNotificationPreferenceRequest(true));

        Assertions.assertAll(
                () -> Assertions.assertFalse(preferences.get(1001L).isTelegramEnabled()),
                () -> Assertions.assertTrue(preferences.get(1002L).isTelegramEnabled()),
                () -> Assertions.assertEquals(
                        List.of(1002L, 1003L),
                        service.enabledAdminIds(List.of(1001L, 1002L, 1003L))
                )
        );
    }

    @Test
    void unauthorizedActor_cannotReadOrUpdatePreference() {
        Mockito.when(accessService.requireCurrentAdmin())
                .thenThrow(new AdminNotAuthorizedException());

        Assertions.assertAll(
                () -> Assertions.assertThrows(
                        AdminNotAuthorizedException.class,
                        service::current
                ),
                () -> Assertions.assertThrows(
                        AdminNotAuthorizedException.class,
                        () -> service.update(
                                new UpdateRentalAdminNotificationPreferenceRequest(false)
                        )
                )
        );
        Mockito.verifyNoInteractions(repository);
    }

    @Test
    void selfServiceRequest_doesNotAcceptAnAdminIdentifier() {
        String[] components = java.util.Arrays.stream(
                        UpdateRentalAdminNotificationPreferenceRequest.class.getRecordComponents()
                )
                .map(RecordComponent::getName)
                .toArray(String[]::new);

        Assertions.assertArrayEquals(new String[]{"telegramEnabled"}, components);
    }
}
