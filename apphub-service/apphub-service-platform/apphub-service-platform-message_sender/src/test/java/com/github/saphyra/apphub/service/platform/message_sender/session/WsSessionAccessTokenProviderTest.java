package com.github.saphyra.apphub.service.platform.message_sender.session;

import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.RestException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class WsSessionAccessTokenProviderTest {
    private static final String ACCESS_TOKEN_ID_STRING = "access-token-id";
    private static final UUID ACCESS_TOKEN_ID = UUID.randomUUID();
    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private WsSessionAccessTokenProvider underTest;

    @Test(expected = RestException.class)
    public void getAccessTokenId_accessTokenCookieNotFound() {
        underTest.getAccessTokenId(new HashMap<>());
    }

    @Test
    public void getAccessTokenId() {
        Map<String, String> cookieMap = new HashMap<>();
        cookieMap.put(Constants.ACCESS_TOKEN_COOKIE, ACCESS_TOKEN_ID_STRING);

        given(uuidConverter.convertEntity(ACCESS_TOKEN_ID_STRING)).willReturn(ACCESS_TOKEN_ID);

        UUID result = underTest.getAccessTokenId(cookieMap);

        assertThat(result).isEqualTo(ACCESS_TOKEN_ID);
    }
}