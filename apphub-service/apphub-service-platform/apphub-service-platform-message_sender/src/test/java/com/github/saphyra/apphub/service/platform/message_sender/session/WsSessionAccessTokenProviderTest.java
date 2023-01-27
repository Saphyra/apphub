package com.github.saphyra.apphub.service.platform.message_sender.session;

import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.LoggedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class WsSessionAccessTokenProviderTest {
    private static final String ACCESS_TOKEN_ID_STRING = "access-token-id";
    private static final UUID ACCESS_TOKEN_ID = UUID.randomUUID();

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private WsSessionAccessTokenProvider underTest;

    @Test
    public void getAccessTokenId_accessTokenCookieNotFound() {
        Throwable ex = catchThrowable(() -> underTest.getAccessTokenId(new HashMap<>()));

        assertThat(ex).isInstanceOf(LoggedException.class);
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