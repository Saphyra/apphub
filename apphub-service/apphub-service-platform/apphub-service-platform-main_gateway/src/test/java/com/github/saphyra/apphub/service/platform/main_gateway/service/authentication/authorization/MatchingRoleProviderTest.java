package com.github.saphyra.apphub.service.platform.main_gateway.service.authentication.authorization;

import com.github.saphyra.apphub.lib.common_domain.WhiteListedEndpoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMethod;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MatchingRoleProviderTest {
    private static final String REQUEST_URI = "request-uri";
    private static final String WHITELISTED_PATTERN = "whitelisted-pattern";

    @Mock
    private AntPathMatcher antPathMatcher;

    @Mock
    private RoleProperties roleProperties;

    @InjectMocks
    private MatchingRoleProvider underTest;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private RoleSetting roleSetting;

    @BeforeEach
    public void setup() {
        given(request.getMethod()).willReturn(HttpMethod.POST);
        given(request.getURI()).willReturn(URI.create(REQUEST_URI));
    }

    @Test
    public void methodNotMatching() {
        given(roleProperties.getSettings()).willReturn(Arrays.asList(roleSetting));
        given(roleSetting.getMethods()).willReturn(Arrays.asList(HttpMethod.POST.name()));
        given(roleSetting.getPattern()).willReturn(RequestMethod.GET.name());

        List<RoleSetting> result = underTest.getMatchingSettings(request);

        assertThat(result).isEmpty();
    }

    @Test
    public void uriNotMatching() {
        given(roleProperties.getSettings()).willReturn(Arrays.asList(roleSetting));
        given(roleSetting.getMethods()).willReturn(Arrays.asList(HttpMethod.POST.name()));
        given(roleSetting.getPattern()).willReturn(REQUEST_URI);
        given(antPathMatcher.match(REQUEST_URI, REQUEST_URI)).willReturn(false);

        List<RoleSetting> result = underTest.getMatchingSettings(request);

        assertThat(result).isEmpty();
    }

    @Test
    public void matching() {
        given(roleProperties.getSettings()).willReturn(Arrays.asList(roleSetting));
        given(roleSetting.getMethods()).willReturn(Arrays.asList(HttpMethod.POST.name()));
        given(roleSetting.getPattern()).willReturn(REQUEST_URI);
        given(antPathMatcher.match(REQUEST_URI, REQUEST_URI)).willReturn(true);

        List<RoleSetting> result = underTest.getMatchingSettings(request);

        assertThat(result).containsExactly(roleSetting);
    }

    @Test
    public void whitelisted() {
        given(roleProperties.getSettings()).willReturn(Arrays.asList(roleSetting));
        given(roleSetting.getMethods()).willReturn(Arrays.asList(HttpMethod.POST.name()));
        given(roleSetting.getPattern()).willReturn(REQUEST_URI);
        given(antPathMatcher.match(REQUEST_URI, REQUEST_URI)).willReturn(true);
        given(roleSetting.getWhitelistedEndpoints()).willReturn(List.of(WhiteListedEndpoint.builder().method(HttpMethod.POST.name()).pattern(WHITELISTED_PATTERN).build()));
        given(antPathMatcher.match(WHITELISTED_PATTERN, REQUEST_URI)).willReturn(true);

        List<RoleSetting> result = underTest.getMatchingSettings(request);

        assertThat(result).isEmpty();
    }

}