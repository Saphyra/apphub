package com.github.saphyra.apphub.service.skyxplore.data.character.service.creation;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.PlayerModel;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.CharacterDao;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.SkyXploreCharacter;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.player.PlayerDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CharacterCreationServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String PLAYER_NAME = "player-name";

    @Mock
    private CharacterDao characterDao;

    @Mock
    private CharacterCreationValidator validator;

    @Mock
    private CharacterModelToCharacterConverter converter;

    @Mock
    private PlayerDao playerDao;

    @InjectMocks
    private CharacterCreationService underTest;

    @Mock
    private SkyXploreCharacter character;

    @Mock
    private PlayerModel playerModel;

    @Test
    public void create() {
        SkyXploreCharacterModel model = SkyXploreCharacterModel.builder()
            .name(PLAYER_NAME)
            .build();

        given(converter.convert(USER_ID, model)).willReturn(character);
        given(playerDao.getByUserId(USER_ID)).willReturn(Arrays.asList(playerModel));

        underTest.create(USER_ID, model);

        verify(validator).validate(USER_ID, model);
        verify(characterDao).save(character);
        verify(playerModel).setUsername(PLAYER_NAME);
        verify(playerDao).save(playerModel);
    }
}