package com.github.saphyra.apphub.service.platform.main_gateway.service.authentication.authorization;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponseWrapper;
import com.github.saphyra.apphub.service.platform.main_gateway.service.authentication.AuthResultHandler;
import com.github.saphyra.apphub.service.platform.main_gateway.service.authentication.AuthResultHandlerFactory;
import com.github.saphyra.apphub.service.platform.main_gateway.service.translation.ErrorResponseFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {
    private static final String LOCALE = "locale";
    private static final String REDIRECT_URL = "redurect-url";

    @Mock
    private MatchingRoleProvider matchingRoleProvider;

    @Mock
    private RequiredRoleChecker requiredRoleChecker;

    @Mock
    private AuthResultHandlerFactory authResultHandlerFactory;

    @Mock
    private ErrorResponseFactory errorResponseFactory;

    @Mock
    private RedirectUrlProvider redirectUrlProvider;

    @InjectMocks
    private AuthorizationService underTest;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private RoleSetting roleSetting;

    @Mock
    private HttpHeaders headers;

    @Mock
    private AuthResultHandler authResultHandler;

    @Mock
    private ErrorResponseWrapper errorResponseWrapper;

    @Test
    void authorized() {
        given(matchingRoleProvider.getMatchingSettings(request)).willReturn(List.of(roleSetting));
        given(requiredRoleChecker.hasRequiredRoles(List.of(roleSetting), accessTokenHeader)).willReturn(true);

        Optional<AuthResultHandler> result = underTest.authorize(request, accessTokenHeader, LOCALE);

        assertThat(result).isEmpty();
    }

    @Test
    void unauthorized() {
        given(matchingRoleProvider.getMatchingSettings(request)).willReturn(List.of(roleSetting));
        given(requiredRoleChecker.hasRequiredRoles(List.of(roleSetting), accessTokenHeader)).willReturn(false);
        given(request.getHeaders()).willReturn(headers);
        given(redirectUrlProvider.getRedirectUrl(List.of(roleSetting), accessTokenHeader)).willReturn(REDIRECT_URL);
        given(errorResponseFactory.create(LOCALE, HttpStatus.FORBIDDEN, ErrorCode.MISSING_ROLE, new HashMap<>())).willReturn(errorResponseWrapper);
        given(authResultHandlerFactory.unauthorized(headers, errorResponseWrapper, REDIRECT_URL)).willReturn(authResultHandler);

        Optional<AuthResultHandler> result = underTest.authorize(request, accessTokenHeader, LOCALE);

        assertThat(result).contains(authResultHandler);

        verify(authResultHandlerFactory).unauthorized(headers, errorResponseWrapper, REDIRECT_URL);
    }
}