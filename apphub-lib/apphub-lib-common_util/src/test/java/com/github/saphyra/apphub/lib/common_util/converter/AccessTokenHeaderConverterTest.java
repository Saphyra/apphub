package com.github.saphyra.apphub.lib.common_util.converter;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.Base64Encoder;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class AccessTokenHeaderConverterTest {
    private static final String INPUT = "input";
    private static final String DECODED = "decoded";

    @Mock
    private Base64Encoder base64Encoder;

    @Mock
    private ObjectMapperWrapper objectMapperWrapper;

    @InjectMocks
    private AccessTokenHeaderConverter underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Test
    public void convert() {
        given(base64Encoder.decode(INPUT)).willReturn(DECODED);
        given(objectMapperWrapper.readValue(DECODED, AccessTokenHeader.class)).willReturn(accessTokenHeader);

        AccessTokenHeader result = underTest.convert(INPUT);

        assertThat(result).isEqualTo(accessTokenHeader);
    }
}