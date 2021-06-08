package com.github.saphyra.apphub.service.skyxplore.data.character.service.creation;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.BadRequestException;
import com.github.saphyra.apphub.lib.exception.ConflictException;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.CharacterDao;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.SkyXploreCharacter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
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

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(exception.getErrorMessage().getParams()).containsEntry("name", "must not be null");
    }

    @Test
    public void characterNameTooShort() {
        Throwable ex = catchThrowable(() -> underTest.validate(USER_ID, SkyXploreCharacterModel.builder().name("as").build()));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.CHARACTER_NAME_TOO_SHORT.name());
    }

    @Test
    public void characterNameTooLong() {
        Throwable ex = catchThrowable(() -> underTest.validate(USER_ID, SkyXploreCharacterModel.builder().name(Stream.generate(() -> "a").limit(31).collect(Collectors.joining())).build()));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.CHARACTER_NAME_TOO_LONG.name());
    }

    @Test
    public void characterNameAlreadyInUse() {
        given(characterDao.findByName(CHARACTER_NAME)).willReturn(Optional.of(character));
        given(character.getUserId()).willReturn(UUID.randomUUID());

        Throwable ex = catchThrowable(() -> underTest.validate(USER_ID, SkyXploreCharacterModel.builder().name(CHARACTER_NAME).build()));

        assertThat(ex).isInstanceOf(ConflictException.class);
        ConflictException exception = (ConflictException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.CHARACTER_NAME_ALREADY_EXISTS.name());
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