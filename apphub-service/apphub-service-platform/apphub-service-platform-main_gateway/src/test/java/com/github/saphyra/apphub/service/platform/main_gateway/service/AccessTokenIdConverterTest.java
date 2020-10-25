package com.github.saphyra.apphub.service.platform.main_gateway.service;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class AccessTokenIdConverterTest {
    private static final String INPUT = "input";
    private static final UUID ACCESS_TOKEN_ID = UUID.randomUUID();
    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private AccessTokenIdConverter underTest;

    @Test
    public void convert_error() {
        given(uuidConverter.convertEntity(Optional.of(INPUT))).willThrow(new RuntimeException());

        Optional<UUID> result = underTest.convertAccessTokenId(INPUT);

        assertThat(result).isEmpty();
    }

    @Test
    public void convert() {
        given(uuidConverter.convertEntity(Optional.of(INPUT))).willReturn(Optional.of(ACCESS_TOKEN_ID));

        Optional<UUID> result = underTest.convertAccessTokenId(INPUT);

        assertThat(result).contains(ACCESS_TOKEN_ID);
    }
}