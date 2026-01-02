package com.github.saphyra.apphub.lib.common_util.converter;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.Base64Encoder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class AccessTokenHeaderConverterTest {
    private static final String INPUT = "input";
    private static final String DECODED = "decoded";

    @Mock
    private Base64Encoder base64Encoder;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AccessTokenHeaderConverter underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Test
    public void convert() {
        given(base64Encoder.decode(INPUT)).willReturn(DECODED);
        given(objectMapper.readValue(DECODED, AccessTokenHeader.class)).willReturn(accessTokenHeader);

        AccessTokenHeader result = underTest.convert(INPUT);

        assertThat(result).isEqualTo(accessTokenHeader);
    }
}