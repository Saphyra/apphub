package com.github.saphyra.apphub.service.skyxplore.data.character.service.creation;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.SkyXploreCharacter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class CharacterModelToCharacterConverterTest {
    private static final String CHARACTER_NAME = "character-name";
    private static final UUID USER_ID = UUID.randomUUID();

    @InjectMocks
    private CharacterModelToCharacterConverter underTest;

    @Test
    public void convert() {
        SkyXploreCharacterModel model = SkyXploreCharacterModel.builder()
            .name(CHARACTER_NAME)
            .build();

        SkyXploreCharacter result = underTest.convert(USER_ID, model);

        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getName()).isEqualTo(CHARACTER_NAME);
    }
}