package com.github.saphyra.apphub.lib.encryption.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class PasswordServiceTest {
    private static final String PASSWORD = "password";
    private static final String FAKE_PASSWORD = "fake_password";
    private static final UUID USER_ID = UUID.randomUUID();

    @InjectMocks
    private PasswordService underTest;

    @Test
    public void testShouldHashAndReturnTrueWhenSamePassword() {
        //GIVEN
        String hashed = underTest.hashPassword(PASSWORD, USER_ID);
        //WHEN
        boolean result = underTest.authenticate(PASSWORD, USER_ID, hashed);
        //THEN
        assertThat(result).isTrue();
    }

    @Test
    public void testShouldHashAndReturnFalseWhenDifferentPassword() {
        //GIVEN
        String hashed = underTest.hashPassword(PASSWORD, USER_ID);
        //WHEN
        boolean result = underTest.authenticate(FAKE_PASSWORD, USER_ID, hashed);
        //THEN
        assertThat(result).isFalse();
    }
}