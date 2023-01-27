package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.stored_resource;

import com.github.saphyra.apphub.api.skyxplore.model.game.StoredResourceModel;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StoredResourceDaoTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final String GAME_ID_STRING = "game-id";
    private static final UUID STORED_RESOURCE_ID = UUID.randomUUID();
    private static final String STORED_RESOURCE_ID_STRING = "stored-resource-id";
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String LOCATION_STRING = "location";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private StoredResourceConverter converter;

    @Mock
    private StoredResourceRepository repository;

    @InjectMocks
    private StoredResourceDao underTest;

    @Mock
    private StoredResourceEntity entity;

    @Mock
    private StoredResourceModel model;

    @Test
    public void deleteByGameId() {
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);

        underTest.deleteByGameId(GAME_ID);

        verify(repository).deleteByGameId(GAME_ID_STRING);
    }

    @Test
    public void findById() {
        given(uuidConverter.convertDomain(STORED_RESOURCE_ID)).willReturn(STORED_RESOURCE_ID_STRING);
        given(repository.findById(STORED_RESOURCE_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(model));

        Optional<StoredResourceModel> result = underTest.findById(STORED_RESOURCE_ID);

        assertThat(result).contains(model);
    }

    @Test
    public void getByLocation() {
        given(uuidConverter.convertDomain(LOCATION)).willReturn(LOCATION_STRING);
        given(repository.getByLocation(LOCATION_STRING)).willReturn(Arrays.asList(entity));
        given(converter.convertEntity(Arrays.asList(entity))).willReturn(Arrays.asList(model));

        List<StoredResourceModel> result = underTest.getByLocation(LOCATION);

        assertThat(result).containsExactly(model);
    }

    @Test
    public void deleteById() {
        given(uuidConverter.convertDomain(STORED_RESOURCE_ID)).willReturn(STORED_RESOURCE_ID_STRING);
        given(repository.existsById(STORED_RESOURCE_ID_STRING)).willReturn(true);

        underTest.deleteById(STORED_RESOURCE_ID);

        verify(repository).deleteById(STORED_RESOURCE_ID_STRING);
    }
}