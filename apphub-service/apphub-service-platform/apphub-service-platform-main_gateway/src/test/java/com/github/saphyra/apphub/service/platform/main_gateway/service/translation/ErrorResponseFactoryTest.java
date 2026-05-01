package com.github.saphyra.apphub.service.platform.main_gateway.service.translation;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponseWrapper;
import com.github.saphyra.apphub.service.platform.main_gateway.service.ErrorResponseFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ErrorResponseFactoryTest {
    @InjectMocks
    private ErrorResponseFactory underTest;

    @Test
    public void create() {
        ErrorResponseWrapper result = underTest.create(HttpStatus.BAD_REQUEST, ErrorCode.LOCALE_NOT_FOUND, new HashMap<>());

        assertThat(result.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getErrorResponse().getErrorCode()).isEqualTo(ErrorCode.LOCALE_NOT_FOUND);
        assertThat(result.getErrorResponse().getParams()).isNotNull();
    }
}