package com.github.saphyra.apphub.service.community.common;

import com.github.saphyra.apphub.api.user.client.AccountClient;
import com.github.saphyra.apphub.api.user.model.response.AccountResponse;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class AccountClientProxyTest {
    private static final String QUERY_STRING = "query-string";
    private static final String LOCALE = "locale";
    private static final String ACCESS_TOKEN = "access-token";
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private LocaleProvider localeProvider;

    @Mock
    private AccountClient accountClient;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @InjectMocks
    private AccountClientProxy underTest;

    @Mock
    private AccountResponse accountResponse;

    @Before
    public void setUp() {
        given(localeProvider.getLocaleValidated()).willReturn(LOCALE);
    }

    @Test
    public void search() {
        given(accessTokenProvider.getAsString()).willReturn(ACCESS_TOKEN);
        given(accountClient.searchAccount(new OneParamRequest<>(QUERY_STRING), ACCESS_TOKEN, LOCALE)).willReturn(List.of(accountResponse));

        List<AccountResponse> result = underTest.search(QUERY_STRING);

        assertThat(result).containsExactly(accountResponse);
    }

    @Test
    public void getAccount() {
        given(accountClient.getAccount(USER_ID, LOCALE)).willReturn(accountResponse);

        AccountResponse result = underTest.getAccount(USER_ID);

        assertThat(result).isEqualTo(accountResponse);
    }

    @Test
    public void userExists() {
        given(accountClient.userExists(USER_ID, LOCALE)).willReturn(true);

        boolean result = underTest.userExists(USER_ID);

        assertThat(result).isTrue();
    }
}