package com.github.saphyra.apphub.service.skyxplore.data.character.dao;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class SkyXploreCharacterConverterTest {
    private static final String USER_ID_STRING = "user-id";
    private static final String CHARACTER_NAME = "character-name";
    private static final UUID USER_ID = UUID.randomUUID();
    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private SkyXploreCharacterConverter underTest;

    @Test
    public void convertEntity() {
        SkyXploreCharacterEntity entity = SkyXploreCharacterEntity.builder()
            .userId(USER_ID_STRING)
            .name(CHARACTER_NAME)
            .build();
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);

        SkyXploreCharacter result = underTest.convertEntity(entity);

        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getName()).isEqualTo(CHARACTER_NAME);
    }

    @Test
    public void convertDomain() {
        SkyXploreCharacter domain = SkyXploreCharacter.builder()
            .userId(USER_ID)
            .name(CHARACTER_NAME)
            .build();
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);

        SkyXploreCharacterEntity result = underTest.convertDomain(domain);

        assertThat(result.getUserId()).isEqualTo(USER_ID_STRING);
        assertThat(result.getName()).isEqualTo(CHARACTER_NAME);
    }
}