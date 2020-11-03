package com.github.saphyra.apphub.service.skyxplore.data.character.service.creation;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.CharacterDao;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.SkyXploreCharacter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class CharacterCreationService {
    private final CharacterDao characterDao;
    private final CharacterCreationValidator validator;
    private final CharacterModelToCharacterConverter converter;

    public void create(UUID userId, SkyXploreCharacterModel characterModel) {
        validator.validate(characterModel);

        SkyXploreCharacter character = converter.convert(userId, characterModel);
        characterDao.save(character);
    }
}
