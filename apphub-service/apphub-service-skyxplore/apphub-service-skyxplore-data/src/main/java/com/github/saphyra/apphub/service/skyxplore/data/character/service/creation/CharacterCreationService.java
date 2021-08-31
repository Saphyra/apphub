package com.github.saphyra.apphub.service.skyxplore.data.character.service.creation;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.PlayerModel;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.CharacterDao;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.SkyXploreCharacter;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.player.PlayerDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CharacterCreationService {
    private final CharacterDao characterDao;
    private final CharacterCreationValidator validator;
    private final CharacterModelToCharacterConverter converter;
    private final PlayerDao playerDao;

    public void create(UUID userId, SkyXploreCharacterModel characterModel) {
        validator.validate(userId, characterModel);

        SkyXploreCharacter character = converter.convert(userId, characterModel);
        characterDao.save(character);

        playerDao.getByUserId(userId)
            .forEach(playerModel -> renamePlayer(playerModel, characterModel.getName()));
    }

    private void renamePlayer(PlayerModel playerModel, String name) {
        playerModel.setUsername(name);
        playerDao.save(playerModel);
    }
}
