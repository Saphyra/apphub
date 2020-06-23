package com.github.saphyra.apphub.lib.security.role;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class MatchingRoleProviderTest {
    private static final String REQUEST_METHOD = RequestMethod.POST.name();
    private static final String REQUEST_URI = "request-uri";

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

    @Before
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
}