package com.github.saphyra.apphub.service.skyxplore.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;

import com.github.saphyra.apphub.api.skyxplore.data.client.SkyXploreCharacterDataApiClient;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.LocaleProvider;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.config.access_token.AccessTokenHeaderConverter;

@RunWith(MockitoJUnitRunner.class)
public class SkyXplorePageControllerTest {
    private static final String ACCESS_TOKEN_HEADER = "access-token-header";
    private static final String LOCALE = "locale";

    @Mock
    private AccessTokenHeaderConverter accessTokenHeaderConverter;

    @Mock
    private LocaleProvider localeProvider;

    @Mock
    private SkyXploreCharacterDataApiClient characterClient;

    @InjectMocks
    private SkyXplorePageController underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Before
    public void setUp() {
        given(accessTokenHeaderConverter.convertDomain(accessTokenHeader)).willReturn(ACCESS_TOKEN_HEADER);
        given(localeProvider.getLocaleValidated()).willReturn(LOCALE);
    }

    @Test
    public void mainMenu_characterNotFound() {
        given(characterClient.isCharacterExistsForUser(ACCESS_TOKEN_HEADER, LOCALE)).willReturn(false);

        ModelAndView result = underTest.mainMenu(accessTokenHeader);

        assertThat(result.getViewName()).isEqualTo("character");
        assertThat(result.getModel().get("backUrl")).isEqualTo(Endpoints.MODULES_PAGE);
    }

    @Test
    public void mainMenu_characterFound() {
        given(characterClient.isCharacterExistsForUser(ACCESS_TOKEN_HEADER, LOCALE)).willReturn(true);

        ModelAndView result = underTest.mainMenu(accessTokenHeader);

        assertThat(result.getViewName()).isEqualTo("main_menu");
    }

    @Test
    public void character() {
        ModelAndView result = underTest.character();

        assertThat(result.getViewName()).isEqualTo("character");
        assertThat(result.getModel().get("backUrl")).isEqualTo(Endpoints.SKYXPLORE_MAIN_MENU_PAGE);
    }
}