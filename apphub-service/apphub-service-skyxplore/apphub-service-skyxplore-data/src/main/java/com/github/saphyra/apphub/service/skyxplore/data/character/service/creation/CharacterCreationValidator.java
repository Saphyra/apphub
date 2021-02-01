package com.github.saphyra.apphub.service.skyxplore.data.character.service.creation;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.exception.BadRequestException;
import com.github.saphyra.apphub.lib.exception.ConflictException;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.CharacterDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

import static java.util.Objects.isNull;


@Component
@RequiredArgsConstructor
@Slf4j
class CharacterCreationValidator {
    private final CharacterDao characterDao;

    public void validate(SkyXploreCharacterModel character) {
        if (isNull(character.getName())) {
            Map<String, String> params = CollectionUtils.singleValueMap("name", "Must not be null");
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), params), "Name must not be null.");
        }

        if (character.getName().length() < 3) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.CHARACTER_NAME_TOO_SHORT.name()), "CharacterName too short.");
        }

        if (character.getName().length() > 30) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.CHARACTER_NAME_TOO_LONG.name()), "CharacterName too long.");
        }

        if (characterDao.findByName(character.getName()).isPresent()) {
            throw new ConflictException(new ErrorMessage(ErrorCode.CHARACTER_NAME_ALREADY_EXISTS.name()), "Character with name " + character.getName() + " already exists.");
        }
    }
}
