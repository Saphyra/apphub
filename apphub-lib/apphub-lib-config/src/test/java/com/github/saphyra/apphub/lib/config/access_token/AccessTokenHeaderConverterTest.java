package com.github.saphyra.apphub.lib.config.access_token;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.Base64Encoder;
import com.github.saphyra.apphub.lib.common_util.CustomObjectMapperWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class AccessTokenHeaderConverterTest {
    private static final String INPUT = "input";
    private static final String DECODED = "decoded";

    @Mock
    private Base64Encoder base64Encoder;

    @Mock
    private CustomObjectMapperWrapper objectMapperWrapper;

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