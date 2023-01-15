package com.github.saphyra.apphub.service.skyxplore.data.character.service.creation;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.CharacterDao;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.SkyXploreCharacter;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class CharacterCreationValidatorTest {
    private static final String CHARACTER_NAME = "character-name";
    private static final UUID USER_ID = UUID.randomUUID();
    @Mock
    private CharacterDao characterDao;

    @InjectMocks
    private CharacterCreationValidator underTest;

    @Mock
    private SkyXploreCharacter character;

    @Test
    public void nullCharacterName() {
        Throwable ex = catchThrowable(() -> underTest.validate(USER_ID, SkyXploreCharacterModel.builder().build()));

        ExceptionValidator.validateInvalidParam(ex, "name", "must not be null");
    }

    @Test
    public void characterNameTooShort() {
        Throwable ex = catchThrowable(() -> underTest.validate(USER_ID, SkyXploreCharacterModel.builder().name("as").build()));

        ExceptionValidator.validateInvalidParam(ex, "characterName", "too short");
    }

    @Test
    public void characterNameTooLong() {
        Throwable ex = catchThrowable(() -> underTest.validate(USER_ID, SkyXploreCharacterModel.builder().name(Stream.generate(() -> "a").limit(31).collect(Collectors.joining())).build()));

        ExceptionValidator.validateInvalidParam(ex, "characterName", "too long");
    }

    @Test
    public void characterNameAlreadyInUse() {
        given(characterDao.findByName(CHARACTER_NAME)).willReturn(Optional.of(character));
        given(character.getUserId()).willReturn(UUID.randomUUID());

        Throwable ex = catchThrowable(() -> underTest.validate(USER_ID, SkyXploreCharacterModel.builder().name(CHARACTER_NAME).build()));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.CONFLICT, ErrorCode.CHARACTER_NAME_ALREADY_EXISTS);
    }

    @Test
    public void characterNameUsedByTheSameUser() {
        given(characterDao.findByName(CHARACTER_NAME)).willReturn(Optional.of(character));
        given(character.getUserId()).willReturn(USER_ID);

        underTest.validate(USER_ID, SkyXploreCharacterModel.builder().name(CHARACTER_NAME).build());
    }

    @Test
    public void valid() {
        given(characterDao.findByName(CHARACTER_NAME)).willReturn(Optional.empty());

        underTest.validate(USER_ID, SkyXploreCharacterModel.builder().name(CHARACTER_NAME).build());
    }

}