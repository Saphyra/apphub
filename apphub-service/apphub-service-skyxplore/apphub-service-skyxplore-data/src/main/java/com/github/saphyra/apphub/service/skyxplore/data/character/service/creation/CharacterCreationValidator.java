package com.github.saphyra.apphub.service.skyxplore.data.character.service.creation;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.CharacterDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static rx.internal.operators.NotificationLite.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class CharacterCreationValidator {
    private final CharacterDao characterDao;

    public void validate(SkyXploreCharacterModel character) {
        if (isNull(character.getName())) {
            //TODO throw exception
        }

        if (character.getName().length() < 3) {
            //TODO throw exception
        }

        if (character.getName().length() > 30) {
            //TODO threw exception
        }

        if (characterDao.findByName(character.getName()).isPresent()) {
            //TODO threw exception
        }
    }
}
