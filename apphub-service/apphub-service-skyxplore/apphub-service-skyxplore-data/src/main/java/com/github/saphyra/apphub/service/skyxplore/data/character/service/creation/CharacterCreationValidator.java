package com.github.saphyra.apphub.service.skyxplore.data.character.service.creation;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.CharacterDao;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.SkyXploreCharacter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.isNull;


@Component
@RequiredArgsConstructor
@Slf4j
class CharacterCreationValidator {
    private final CharacterDao characterDao;

    public void validate(UUID userId, SkyXploreCharacterModel model) {
        if (isNull(model.getName())) {
            throw ExceptionFactory.invalidParam("name", "must not be null");
        }

        if (model.getName().length() < 3) {
            throw ExceptionFactory.invalidParam("characterName", "too short");
        }

        if (model.getName().length() > 30) {
            throw ExceptionFactory.invalidParam("characterName", "too long");
        }

        Optional<SkyXploreCharacter> character = characterDao.findByName(model.getName());
        if (character.isPresent() && !character.get().getUserId().equals(userId)) {
            throw ExceptionFactory.notLoggedException(HttpStatus.CONFLICT, ErrorCode.CHARACTER_NAME_ALREADY_EXISTS, String.format("Character with name %s already exists.", model.getName()));
        }
    }
}
