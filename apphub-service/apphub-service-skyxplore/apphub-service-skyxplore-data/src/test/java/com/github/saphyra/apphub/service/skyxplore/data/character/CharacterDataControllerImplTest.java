package com.github.saphyra.apphub.service.skyxplore.data.character;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.CharacterDao;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.SkyXploreCharacter;
import com.github.saphyra.apphub.service.skyxplore.data.character.service.creation.CharacterCreationService;
import com.github.saphyra.apphub.service.skyxplore.data.common.SkyXploreCharacterModelConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CharacterDataControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final AccessTokenHeader ACCESS_TOKEN_HEADER = AccessTokenHeader.builder()
        .userId(USER_ID)
        .build();
    private static final String CHARACTER_NAME = "character-name";

    @Mock
    private CharacterCreationService characterCreationService;

    @Mock
    private CharacterDao characterDao;

    @Mock
    private SkyXploreCharacterModelConverter characterModelConverter;

    @InjectMocks
    private CharacterDataControllerImpl underTest;

    @Mock
    private SkyXploreCharacter character;

    @Mock
    private SkyXploreCharacterModel model;

    @Test
    public void createOrUpdateCharacter() {
        underTest.createOrUpdateCharacter(model, ACCESS_TOKEN_HEADER);

        verify(characterCreationService).create(USER_ID, model);
    }

    @Test
    void getCharacterName() {
        given(characterDao.findByIdValidated(USER_ID)).willReturn(character);
        given(character.getName()).willReturn(CHARACTER_NAME);

        OneParamResponse<String> result = underTest.getCharacterName(ACCESS_TOKEN_HEADER);

        assertThat(result.getValue()).isEqualTo(CHARACTER_NAME);
    }

    @Test
    public void internalGetCharacterByUserId_found() {
        given(characterDao.findById(USER_ID)).willReturn(Optional.of(character));
        given(characterModelConverter.convertEntity(Optional.of(character))).willReturn(Optional.of(model));

        ResponseEntity<SkyXploreCharacterModel> result = underTest.internalGetCharacterByUserId(USER_ID);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(model);
    }

    @Test
    public void internalGetCharacterByUserId_notFound() {
        given(characterDao.findById(USER_ID)).willReturn(Optional.empty());

        ResponseEntity<SkyXploreCharacterModel> result = underTest.internalGetCharacterByUserId(USER_ID);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void exists() {
        given(characterDao.exists(USER_ID)).willReturn(true);

        OneParamResponse<Boolean> result = underTest.exists(ACCESS_TOKEN_HEADER);

        assertThat(result.getValue()).isTrue();
    }
}