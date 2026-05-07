package com.github.saphyra.apphub.service.platform.main_gateway.service.authorization.authentication;

import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponseWrapper;
import com.github.saphyra.apphub.service.platform.main_gateway.service.ErrorResponseFactory;
import com.github.saphyra.apphub.service.platform.main_gateway.service.authorization.AuthResultHandler;
import com.github.saphyra.apphub.service.platform.main_gateway.service.authorization.AuthResultHandlerFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    private static final String REDIRECT_URL = "redirect-url";

    @Mock
    private  MatchingRoleProvider matchingRoleProvider;

    @Mock
    private  RequiredRoleChecker requiredRoleChecker;

    @Mock
    private  AuthResultHandlerFactory authResultHandlerFactory;

    @Mock
    private  ErrorResponseFactory errorResponseFactory;

    @Mock
    private  RedirectUrlProvider redirectUrlProvider;

    @InjectMocks
    private AuthenticationService underTest;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private AccessToken accessToken;

    @Mock
    private RoleSetting roleSetting;

    @Mock
    private AuthResultHandler authResultHandler;

    @Mock
    private HttpHeaders httpHeaders;

    @Mock
    private ErrorResponseWrapper errorResponseWrapper;

    @Test
    void noMatchingSettings(){
        given(matchingRoleProvider.getMatchingSettings(request)).willReturn(List.of());

        StepVerifier.create(underTest.authenticate(request, accessToken))
            .expectNextCount(0)
            .verifyComplete();
    }

    @Test
    void hasRequiredRole(){
        given(matchingRoleProvider.getMatchingSettings(request)).willReturn(List.of(roleSetting));
        given(requiredRoleChecker.hasRequiredRoles(List.of(roleSetting), accessToken)).willReturn(true);

        StepVerifier.create(underTest.authenticate(request, accessToken))
            .expectNextCount(0)
            .verifyComplete();
    }

    @Test
    void authenticationFailed(){
        given(matchingRoleProvider.getMatchingSettings(request)).willReturn(List.of(roleSetting));
        given(requiredRoleChecker.hasRequiredRoles(List.of(roleSetting), accessToken)).willReturn(false);
        given(request.getHeaders()).willReturn(httpHeaders);
        given(errorResponseFactory.create(HttpStatus.FORBIDDEN, ErrorCode.MISSING_ROLE)).willReturn(errorResponseWrapper);
        given(redirectUrlProvider.getRedirectUrl(List.of(roleSetting), accessToken)).willReturn(REDIRECT_URL);
        given(authResultHandlerFactory.authenticationFailed(httpHeaders, errorResponseWrapper, REDIRECT_URL)).willReturn(authResultHandler);

        StepVerifier.create(underTest.authenticate(request, accessToken))
            .expectNext(authResultHandler)
            .verifyComplete();
    }
}