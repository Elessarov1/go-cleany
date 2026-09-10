package com.cleany.customer;

import org.springframework.data.jpa.repository.JpaRepository;

interface SecurityAuditEventRepository extends JpaRepository<SecurityAuditEvent, Long> {
}
