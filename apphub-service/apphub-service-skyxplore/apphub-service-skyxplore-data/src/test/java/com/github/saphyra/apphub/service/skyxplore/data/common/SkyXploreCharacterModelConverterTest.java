package com.github.saphyra.apphub.service.skyxplore.data.common;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.SkyXploreCharacter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class SkyXploreCharacterModelConverterTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String CHARACTER_NAME = "character.name";

    @InjectMocks
    private SkyXploreCharacterModelConverter underTest;

    @Test
    public void convertCharacter() {
        SkyXploreCharacter character = SkyXploreCharacter.builder()
            .userId(USER_ID)
            .name(CHARACTER_NAME)
            .build();

        SkyXploreCharacterModel result = underTest.convertEntity(character);

        assertThat(result.getId()).isEqualTo(USER_ID);
        assertThat(result.getName()).isEqualTo(CHARACTER_NAME);
    }

    @Test
    public void convertModel() {
        SkyXploreCharacterModel model = SkyXploreCharacterModel.builder()
            .id(USER_ID)
            .name(CHARACTER_NAME)
            .build();

        SkyXploreCharacter result = underTest.convertDomain(model);

        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getName()).isEqualTo(CHARACTER_NAME);
    }
}