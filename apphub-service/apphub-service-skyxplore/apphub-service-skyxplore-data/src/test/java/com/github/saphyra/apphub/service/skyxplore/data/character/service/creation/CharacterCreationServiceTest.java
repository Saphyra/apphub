package com.github.saphyra.apphub.service.skyxplore.data.character.service.creation;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.CharacterDao;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.SkyXploreCharacter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CharacterCreationServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private CharacterDao characterDao;

    @Mock
    private CharacterCreationValidator validator;

    @Mock
    private CharacterModelToCharacterConverter converter;

    @InjectMocks
    private CharacterCreationService underTest;

    @Mock
    private SkyXploreCharacter character;

    @Test
    public void create() {
        SkyXploreCharacterModel model = new SkyXploreCharacterModel();

        given(converter.convert(USER_ID, model)).willReturn(character);

        underTest.create(USER_ID, model);

        verify(validator).validate(model);
        verify(characterDao).save(character);
    }
}