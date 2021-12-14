package com.github.saphyra.apphub.service.platform.web_content.error_page;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.error_handler.service.translation.LocalizedMessageProvider;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import com.github.saphyra.apphub.test.common.TestConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ErrorControllerTest {
    private static final String LOCALIZED_MESSAGE = "localized-message";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String REQUIRED_ROLES = "required-roles";
    private static final String DESCRIPTION = "description";

    @Mock
    private LocaleProvider localeProvider;

    @Mock
    private LocalizedMessageProvider localizedMessageProvider;

    @Mock
    private UserBannedDescriptionResolver userBannedDescriptionResolver;

    @InjectMocks
    private ErrorController underTest;

    @Test
    public void errorPage() {
        given(localeProvider.getLocaleValidated()).willReturn(TestConstants.DEFAULT_LOCALE);
        given(localizedMessageProvider.getLocalizedMessage(TestConstants.DEFAULT_LOCALE, ErrorCode.MISSING_ROLE)).willReturn(LOCALIZED_MESSAGE);
        given(userBannedDescriptionResolver.resolve(USER_ID, REQUIRED_ROLES)).willReturn(DESCRIPTION);

        ModelAndView result = underTest.errorPage(ErrorCode.MISSING_ROLE.name(), USER_ID, REQUIRED_ROLES);

        assertThat(result.getViewName()).isEqualTo("err");
        assertThat(result.getModel().get("message")).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(result.getModel().get("error_code")).isEqualTo(ErrorCode.MISSING_ROLE);
        assertThat(result.getModel().get("description")).isEqualTo(DESCRIPTION);
    }

    @Test
    public void errorPage_noErrorCode() {
        given(localeProvider.getLocaleValidated()).willReturn(TestConstants.DEFAULT_LOCALE);
        given(localizedMessageProvider.getLocalizedMessage(TestConstants.DEFAULT_LOCALE, ErrorCode.UNKNOWN_ERROR)).willReturn(LOCALIZED_MESSAGE);

        ModelAndView result = underTest.errorPage(null, null, null);

        assertThat(result.getViewName()).isEqualTo("err");
        assertThat(result.getModel().get("message")).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(result.getModel().get("error_code")).isEqualTo(ErrorCode.UNKNOWN_ERROR);
    }
}