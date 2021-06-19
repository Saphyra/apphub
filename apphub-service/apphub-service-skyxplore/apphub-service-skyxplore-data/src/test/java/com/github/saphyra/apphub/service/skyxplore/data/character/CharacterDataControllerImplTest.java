package com.github.saphyra.apphub.service.skyxplore.data.character;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.CharacterDao;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.SkyXploreCharacter;
import com.github.saphyra.apphub.service.skyxplore.data.character.service.creation.CharacterCreationService;
import com.github.saphyra.apphub.service.skyxplore.data.common.SkyXploreCharacterModelConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CharacterDataControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final AccessTokenHeader ACCESS_TOKEN_HEADER = AccessTokenHeader.builder()
        .userId(USER_ID)
        .build();

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
    public void isCharacterExistsForUser() {
        given(characterDao.exists(USER_ID)).willReturn(true);

        boolean result = underTest.doesCharacterExistForUser(ACCESS_TOKEN_HEADER);

        assertThat(result).isTrue();
    }

    @Test
    public void createOrUpdateCharacter() {
        underTest.createOrUpdateCharacter(model, ACCESS_TOKEN_HEADER);

        verify(characterCreationService).create(USER_ID, model);
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
}