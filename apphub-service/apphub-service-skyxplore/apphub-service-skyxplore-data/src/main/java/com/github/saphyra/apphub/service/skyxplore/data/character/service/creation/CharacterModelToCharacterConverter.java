package com.github.saphyra.apphub.service.skyxplore.data.character.service.creation;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.SkyXploreCharacter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
class CharacterModelToCharacterConverter {
    public SkyXploreCharacter convert(UUID userId, SkyXploreCharacterModel characterModel) {
        return SkyXploreCharacter.builder()
            .userId(userId)
            .name(characterModel.getName())
            .build();
    }
}
