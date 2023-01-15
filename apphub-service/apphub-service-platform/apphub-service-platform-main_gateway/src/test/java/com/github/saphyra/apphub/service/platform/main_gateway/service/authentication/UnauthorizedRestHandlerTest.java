package com.github.saphyra.apphub.service.platform.main_gateway.service.authentication;

import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponseWrapper;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UnauthorizedRestHandlerTest {
    private static final String SERIALIZED_PAYLOAD = "serialized-payload";

    @Mock
    private ErrorResponseWrapper errorResponseWrapper;

    @Mock
    private ObjectMapperWrapper objectMapperWrapper;

    @InjectMocks
    private UnauthorizedRestHandler underTest;

    @Mock
    private ServerWebExchange exchange;

    @Mock
    private GatewayFilterChain filterChain;

    @Mock
    private ServerHttpResponse response;

    @Mock
    private ErrorResponse errorResponse;

    @Mock
    private Mono<Void> mono;

    @Mock
    private HttpHeaders httpHeaders;

    @Mock
    private DataBufferFactory dataBufferFactory;

    @Mock
    private DataBuffer dataBuffer;

    @Test
    public void handle() {
        given(exchange.getResponse()).willReturn(response);
        given(errorResponseWrapper.getStatus()).willReturn(HttpStatus.BAD_REQUEST);
        given(response.getHeaders()).willReturn(httpHeaders);
        given(errorResponseWrapper.getErrorResponse()).willReturn(errorResponse);
        given(objectMapperWrapper.writeValueAsString(errorResponse)).willReturn(SERIALIZED_PAYLOAD);
        given(response.bufferFactory()).willReturn(dataBufferFactory);
        given(dataBufferFactory.wrap(SERIALIZED_PAYLOAD.getBytes())).willReturn(dataBuffer);
        given(response.writeAndFlushWith(any())).willReturn(mono);

        Mono<Void> result = underTest.handle(exchange, filterChain);

        verify(response).setStatusCode(HttpStatus.BAD_REQUEST);
        verify(httpHeaders).add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);

        assertThat(result).isEqualTo(mono);
    }
}