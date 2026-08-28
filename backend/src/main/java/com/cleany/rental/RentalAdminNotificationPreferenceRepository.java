package com.cleany.rental;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalAdminNotificationPreferenceRepository
        extends JpaRepository<RentalAdminNotificationPreference, Long> {

}
