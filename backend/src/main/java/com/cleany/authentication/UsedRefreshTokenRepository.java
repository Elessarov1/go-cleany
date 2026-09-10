package com.cleany.authentication;

import org.springframework.data.jpa.repository.JpaRepository;

interface UsedRefreshTokenRepository extends JpaRepository<UsedRefreshToken, String> {
}
