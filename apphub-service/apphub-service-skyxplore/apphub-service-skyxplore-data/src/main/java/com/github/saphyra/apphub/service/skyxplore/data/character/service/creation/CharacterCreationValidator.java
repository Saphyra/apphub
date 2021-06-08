package com.github.saphyra.apphub.service.skyxplore.data.character.service.creation;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.exception.BadRequestException;
import com.github.saphyra.apphub.lib.exception.ConflictException;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.CharacterDao;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.SkyXploreCharacter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
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
            Map<String, String> params = CollectionUtils.singleValueMap("name", "must not be null");
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), params), "Name must not be null.");
        }

        if (model.getName().length() < 3) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.CHARACTER_NAME_TOO_SHORT.name()), "CharacterName too short.");
        }

        if (model.getName().length() > 30) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.CHARACTER_NAME_TOO_LONG.name()), "CharacterName too long.");
        }

        Optional<SkyXploreCharacter> character = characterDao.findByName(model.getName());
        if (character.isPresent() && !character.get().getUserId().equals(userId)) {
            throw new ConflictException(new ErrorMessage(ErrorCode.CHARACTER_NAME_ALREADY_EXISTS.name()), "Character with name " + model.getName() + " already exists.");
        }
    }
}
