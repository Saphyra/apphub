package com.github.saphyra.apphub.lib.security.role;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.error_handler.service.translation.ErrorResponseFactory;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponseWrapper;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@RunWith(MockitoJUnitRunner.class)
public class RoleFilterTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String ROLE = "role";

    @Mock
    private ErrorResponseFactory errorResponseFactory;

    @Mock
    private MatchingRoleProvider matchingRoleProvider;

    @Mock
    private RequestHelper requestHelper;

    @Mock
    private RequiredRoleChecker requiredRoleChecker;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @InjectMocks
    private RoleFilter underTest;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private RoleSetting roleSetting;

    @Mock
    private ErrorResponseWrapper errorResponseWrapper;

    @Mock
    private ErrorResponse errorResponse;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Before
    public void setup() {
        given(matchingRoleProvider.getMatchingSettings(request)).willReturn(Arrays.asList(roleSetting));
    }

    @Test
    public void noSettings() throws ServletException, IOException {
        given(matchingRoleProvider.getMatchingSettings(request)).willReturn(Collections.emptyList());

        underTest.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void hasRole() throws ServletException, IOException {
        given(requiredRoleChecker.hasRequiredRoles(Arrays.asList(roleSetting))).willReturn(true);

        underTest.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void forbidden_rest() throws ServletException, IOException {
        given(requiredRoleChecker.hasRequiredRoles(Arrays.asList(roleSetting))).willReturn(false);
        given(requestHelper.isRestCall(request)).willReturn(true);
        given(errorResponseFactory.create(request, HttpStatus.FORBIDDEN, ErrorCode.MISSING_ROLE)).willReturn(errorResponseWrapper);
        given(errorResponseWrapper.getErrorResponse()).willReturn(errorResponse);
        given(errorResponseWrapper.getStatus()).willReturn(HttpStatus.FORBIDDEN);

        underTest.doFilterInternal(request, response, filterChain);

        verifyNoInteractions(filterChain);
        verify(requestHelper).sendRestError(response, HttpStatus.FORBIDDEN, errorResponse);
        verifyNoInteractions(response);
    }

    @Test
    public void forbidden_web() throws ServletException, IOException {
        given(requiredRoleChecker.hasRequiredRoles(Arrays.asList(roleSetting))).willReturn(false);
        given(requestHelper.isRestCall(request)).willReturn(false);
        given(accessTokenProvider.getOptional()).willReturn(Optional.of(accessTokenHeader));
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
        given(roleSetting.getRequiredRoles()).willReturn(List.of(ROLE));

        underTest.doFilterInternal(request, response, filterChain);

        verifyNoInteractions(filterChain);
        verify(requestHelper, times(0)).sendRestError(any(), any(), any());
        verify(response).sendRedirect(String.format("%s?error_code=%s&user_id=%s&required_roles=%s", Endpoints.ERROR_PAGE, ErrorCode.MISSING_ROLE.name(), USER_ID, ROLE));
    }
}