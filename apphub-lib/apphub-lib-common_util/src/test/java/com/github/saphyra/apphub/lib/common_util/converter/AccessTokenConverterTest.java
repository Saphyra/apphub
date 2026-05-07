package com.github.saphyra.apphub.lib.common_util.converter;

import com.github.saphyra.apphub.lib.common_domain.AccessToken;
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
public class AccessTokenConverterTest {
    private static final String INPUT = "input";
    private static final String DECODED = "decoded";

    @Mock
    private Base64Encoder base64Encoder;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AccessTokenHeaderConverter underTest;

    @Mock
    private AccessToken accessToken;

    @Test
    public void convert() {
        given(base64Encoder.decode(INPUT)).willReturn(DECODED);
        given(objectMapper.readValue(DECODED, AccessToken.class)).willReturn(accessToken);

        AccessToken result = underTest.convert(INPUT);

        assertThat(result).isEqualTo(accessToken);
    }
}