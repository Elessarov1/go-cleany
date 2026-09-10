package com.cleany.communication;

import org.springframework.data.jpa.repository.JpaRepository;

interface CustomerNotificationPreferenceRepository
        extends JpaRepository<CustomerNotificationPreference, Long> {
}
