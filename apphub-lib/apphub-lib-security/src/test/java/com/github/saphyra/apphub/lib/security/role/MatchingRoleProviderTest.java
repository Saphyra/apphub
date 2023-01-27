package com.github.saphyra.apphub.lib.security.role;

import com.github.saphyra.apphub.lib.common_domain.WhiteListedEndpoint;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class MatchingRoleProviderTest {
    private static final String REQUEST_METHOD = RequestMethod.POST.name();
    private static final String REQUEST_URI = "request-uri";
    private static final String WHITELISTED_PATTERN = "whitelisted-pattern";

    @Mock
    private AntPathMatcher antPathMatcher;

    @Mock
    private RoleFilterSettingRegistry roleFilterSettingRegistry;

    @InjectMocks
    private MatchingRoleProvider underTest;

    @Mock
    private HttpServletRequest request;

    @Mock
    private RoleSetting roleSetting;

    @BeforeEach
    public void setup() {
        given(request.getMethod()).willReturn(REQUEST_METHOD);
        given(request.getRequestURI()).willReturn(REQUEST_URI);
    }

    @Test
    public void methodNotMatching() {
        given(roleFilterSettingRegistry.getSettings()).willReturn(Arrays.asList(roleSetting));
        given(roleSetting.getMethods()).willReturn(Arrays.asList(REQUEST_METHOD));
        given(roleSetting.getPattern()).willReturn(RequestMethod.GET.name());

        List<RoleSetting> result = underTest.getMatchingSettings(request);

        assertThat(result).isEmpty();
    }

    @Test
    public void uriNotMatching() {
        given(roleFilterSettingRegistry.getSettings()).willReturn(Arrays.asList(roleSetting));
        given(roleSetting.getMethods()).willReturn(Arrays.asList(REQUEST_METHOD));
        given(roleSetting.getPattern()).willReturn(REQUEST_URI);
        given(antPathMatcher.match(REQUEST_URI, REQUEST_URI)).willReturn(false);

        List<RoleSetting> result = underTest.getMatchingSettings(request);

        assertThat(result).isEmpty();
    }

    @Test
    public void matching() {
        given(roleFilterSettingRegistry.getSettings()).willReturn(Arrays.asList(roleSetting));
        given(roleSetting.getMethods()).willReturn(Arrays.asList(REQUEST_METHOD));
        given(roleSetting.getPattern()).willReturn(REQUEST_URI);
        given(antPathMatcher.match(REQUEST_URI, REQUEST_URI)).willReturn(true);

        List<RoleSetting> result = underTest.getMatchingSettings(request);

        assertThat(result).containsExactly(roleSetting);
    }

    @Test
    public void whitelisted() {
        given(roleFilterSettingRegistry.getSettings()).willReturn(Arrays.asList(roleSetting));
        given(roleSetting.getMethods()).willReturn(Arrays.asList(REQUEST_METHOD));
        given(roleSetting.getPattern()).willReturn(REQUEST_URI);
        given(antPathMatcher.match(REQUEST_URI, REQUEST_URI)).willReturn(true);
        given(roleSetting.getWhitelistedEndpoints()).willReturn(List.of(WhiteListedEndpoint.builder().method(REQUEST_METHOD).pattern(WHITELISTED_PATTERN).build()));
        given(antPathMatcher.match(WHITELISTED_PATTERN, REQUEST_URI)).willReturn(true);

        List<RoleSetting> result = underTest.getMatchingSettings(request);

        assertThat(result).isEmpty();
    }
}