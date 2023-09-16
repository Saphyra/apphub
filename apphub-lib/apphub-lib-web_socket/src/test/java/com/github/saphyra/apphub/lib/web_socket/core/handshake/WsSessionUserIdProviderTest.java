package com.github.saphyra.apphub.lib.web_socket.core.handshake;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_util.converter.AccessTokenHeaderConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class WsSessionUserIdProviderTest {
    private static final String ENCODED_ACCESS_TOKEN = "encoded-access-token";
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private AccessTokenHeaderConverter accessTokenHeaderConverter;

    @InjectMocks
    private WsSessionUserIdProvider underTest;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Test
    void noHeader() {
        given(request.getHeaders()).willReturn(new HttpHeaders());

        Optional<UUID> result = underTest.findUserId(request);

        assertThat(result).isEmpty();
    }

    @Test
    void findUserId() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.put(Constants.ACCESS_TOKEN_HEADER, List.of(ENCODED_ACCESS_TOKEN));
        given(request.getHeaders()).willReturn(httpHeaders);

        given(accessTokenHeaderConverter.convertEntity(ENCODED_ACCESS_TOKEN)).willReturn(accessTokenHeader);
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);

        Optional<UUID> result = underTest.findUserId(request);

        assertThat(result).contains(USER_ID);
    }
}