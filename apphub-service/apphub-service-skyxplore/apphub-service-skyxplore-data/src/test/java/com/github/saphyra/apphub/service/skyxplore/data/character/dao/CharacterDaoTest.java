package com.github.saphyra.apphub.service.skyxplore.data.character.dao;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.NotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CharacterDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";
    private static final String CHARACTER_NAME = "character-name";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private SkyXploreCharacterConverter converter;

    @Mock
    private SkyXploreCharacterRepository repository;

    @InjectMocks
    private CharacterDao underTest;

    @Mock
    private SkyXploreCharacter domain;

    @Mock
    private SkyXploreCharacterEntity entity;

    @Before
    public void setUp() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
    }

    @Test
    public void deleteByUserId() {
        underTest.deleteByUserId(USER_ID);

        verify(repository).deleteById(USER_ID_STRING);
    }

    @Test
    public void exists() {
        underTest.exists(USER_ID);

        verify(repository).existsById(USER_ID_STRING);
    }

    @Test
    public void findByName() {
        given(repository.findByName(CHARACTER_NAME)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        Optional<SkyXploreCharacter> result = underTest.findByName(CHARACTER_NAME);

        assertThat(result).contains(domain);
    }

    @Test
    public void findById() {
        given(repository.findById(USER_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        Optional<SkyXploreCharacter> result = underTest.findById(USER_ID);

        assertThat(result).contains(domain);
    }

    @Test(expected = NotFoundException.class)
    public void findByIdValidated_notFound() {
        given(repository.findById(USER_ID_STRING)).willReturn(Optional.empty());

        underTest.findByIdValidated(USER_ID);
    }

    @Test
    public void findByIdValidated_found() {
        given(repository.findById(USER_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        SkyXploreCharacter result = underTest.findByIdValidated(USER_ID);

        assertThat(result).isEqualTo(domain);
    }

    @Test
    public void getByNameLike() {
        given(repository.getByNameContainingIgnoreCase(CHARACTER_NAME)).willReturn(Arrays.asList(entity));
        given(converter.convertEntity(Arrays.asList(entity))).willReturn(Arrays.asList(domain));

        List<SkyXploreCharacter> result = underTest.getByNameLike(CHARACTER_NAME);

        assertThat(result).containsExactly(domain);
    }
}