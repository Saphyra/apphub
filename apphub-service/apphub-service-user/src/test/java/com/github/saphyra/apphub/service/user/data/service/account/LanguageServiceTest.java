package com.github.saphyra.apphub.service.user.data.service.account;

import com.github.saphyra.apphub.api.user.model.response.LanguageResponse;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.exception.BadRequestException;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class LanguageServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String LANGUAGE_1 = "language-1";
    private static final String LANGUAGE_2 = "language-2";

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

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(exception.getErrorMessage().getParams().get("value")).isEqualTo("language must not be null");
    }

    @Test
    public void changeLanguage_localeNotSupported() {
        given(commonConfigProperties.getSupportedLocales()).willReturn(Collections.emptyList());

        Throwable ex = catchThrowable(() -> underTest.changeLanguage(USER_ID, LANGUAGE_1));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(exception.getErrorMessage().getParams().get("value")).isEqualTo("language not supported");
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
    public void getLanguages() {
        given(userDao.findByIdValidated(USER_ID)).willReturn(user);
        given(user.getLanguage()).willReturn(LANGUAGE_1);
        given(commonConfigProperties.getSupportedLocales()).willReturn(Arrays.asList(LANGUAGE_1, LANGUAGE_2));

        List<LanguageResponse> result = underTest.getLanguages(USER_ID);

        assertThat(result).containsExactlyInAnyOrder(
            LanguageResponse.builder().language(LANGUAGE_1).actual(true).build(),
            LanguageResponse.builder().language(LANGUAGE_2).actual(false).build()
        );
    }

    @Test
    public void getLanguage() {
        given(userDao.findByIdValidated(USER_ID)).willReturn(user);
        given(user.getLanguage()).willReturn(LANGUAGE_1);

        String result = underTest.getLanguage(USER_ID);

        assertThat(result).isEqualTo(LANGUAGE_1);
    }
}