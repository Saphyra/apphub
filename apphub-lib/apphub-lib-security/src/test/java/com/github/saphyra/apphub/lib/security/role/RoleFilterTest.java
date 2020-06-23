package com.github.saphyra.apphub.lib.security.role;

import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.RequestHelper;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.error_handler.service.ErrorResponseFactory;
import com.github.saphyra.apphub.lib.error_handler.service.ErrorResponseWrapper;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@RunWith(MockitoJUnitRunner.class)
public class RoleFilterTest {
    @Mock
    private ErrorResponseFactory errorResponseFactory;

    @Mock
    private MatchingRoleProvider matchingRoleProvider;

    @Mock
    private RequestHelper requestHelper;

    @Mock
    private RequiredRoleChecker requiredRoleChecker;

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
        given(errorResponseFactory.create(request, HttpStatus.FORBIDDEN, ErrorCode.MISSING_ROLE.name())).willReturn(errorResponseWrapper);
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

        underTest.doFilterInternal(request, response, filterChain);

        verifyNoInteractions(filterChain);
        verify(requestHelper, times(0)).sendRestError(any(), any(), any());
        verify(response).sendRedirect(String.format("%s?error_code=%s", Endpoints.ERROR_PAGE, ErrorCode.MISSING_ROLE.name()));
    }
}