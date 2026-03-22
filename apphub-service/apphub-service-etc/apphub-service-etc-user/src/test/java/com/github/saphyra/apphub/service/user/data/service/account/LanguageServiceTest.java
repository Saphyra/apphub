package com.github.saphyra.apphub.service.user.data.service.account;

import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LanguageServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String LANGUAGE_1 = "language-1";

    @Mock
    private CommonConfigProperties commonConfigProperties;

    @Mock
    private UserDao userDao;

    @InjectMocks
    private LanguageService underTest;

    @Mock
    private User user;

    @Test
    public void changeLanguage_nullLanguage() {
        Throwable ex = catchThrowable(() -> underTest.changeLanguage(USER_ID, null));

        ExceptionValidator.validateInvalidParam(ex, "value", "must not be null");
    }

    @Test
    public void changeLanguage_localeNotSupported() {
        given(commonConfigProperties.getSupportedLocales()).willReturn(Collections.emptyList());

        Throwable ex = catchThrowable(() -> underTest.changeLanguage(USER_ID, LANGUAGE_1));

        ExceptionValidator.validateInvalidParam(ex, "value", "not supported");
    }

    @Test
    public void changeLanguage() {
        given(commonConfigProperties.getSupportedLocales()).willReturn(Arrays.asList(LANGUAGE_1));
        given(userDao.findByIdValidated(USER_ID)).willReturn(user);

        underTest.changeLanguage(USER_ID, LANGUAGE_1);

        verify(user).setLanguage(LANGUAGE_1);
        verify(userDao).save(user);
    }

    @Test
    public void getLanguage() {
        given(userDao.findByIdValidated(USER_ID)).willReturn(user);
        given(user.getLanguage()).willReturn(LANGUAGE_1);

        String result = underTest.getLanguage(USER_ID);

        assertThat(result).isEqualTo(LANGUAGE_1);
    }
}