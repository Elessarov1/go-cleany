package com.cleany.rental;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalAdminNotificationPreferenceRepository
        extends JpaRepository<RentalAdminNotificationPreference, Long> {

    List<RentalAdminNotificationPreference> findAllByAdminIdIn(Collection<Long> adminIds);
}
