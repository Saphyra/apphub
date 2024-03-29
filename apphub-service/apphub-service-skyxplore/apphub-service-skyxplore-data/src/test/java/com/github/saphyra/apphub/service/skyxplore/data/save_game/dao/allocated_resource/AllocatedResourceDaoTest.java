package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.allocated_resource;

import com.github.saphyra.apphub.api.skyxplore.model.game.AllocatedResourceModel;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AllocatedResourceDaoTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final String GAME_ID_STRING = "game-id";
    private static final UUID ALLOCATED_RESOURCE_ID = UUID.randomUUID();
    private static final String ALLOCATED_RESOURCE_ID_STRING = "allocated-resource-id";
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String LOCATION_STRING = "location";
    private static final Integer PAGE = 3215;
    private static final Integer ITEMS_PER_PAGE = 457;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private AllocatedResourceConverter converter;

    @Mock
    private AllocatedResourceRepository repository;

    @InjectMocks
    private AllocatedResourceDao underTest;

    @Mock
    private AllocatedResourceEntity entity;

    @Mock
    private AllocatedResourceModel model;

    @Test
    public void deleteByGameId() {
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);

        underTest.deleteByGameId(GAME_ID);

        verify(repository).deleteByGameId(GAME_ID_STRING);
    }

    @Test
    public void findById() {
        given(uuidConverter.convertDomain(ALLOCATED_RESOURCE_ID)).willReturn(ALLOCATED_RESOURCE_ID_STRING);
        given(repository.findById(ALLOCATED_RESOURCE_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(model));

        Optional<AllocatedResourceModel> result = underTest.findById(ALLOCATED_RESOURCE_ID);

        assertThat(result).contains(model);
    }

    @Test
    public void getByLocation() {
        given(uuidConverter.convertDomain(LOCATION)).willReturn(LOCATION_STRING);
        given(repository.getByLocation(LOCATION_STRING)).willReturn(Arrays.asList(entity));
        given(converter.convertEntity(Arrays.asList(entity))).willReturn(Arrays.asList(model));

        List<AllocatedResourceModel> result = underTest.getByLocation(LOCATION);

        assertThat(result).containsExactly(model);
    }

    @Test
    public void deleteById() {
        given(uuidConverter.convertDomain(ALLOCATED_RESOURCE_ID)).willReturn(ALLOCATED_RESOURCE_ID_STRING);
        given(repository.existsById(ALLOCATED_RESOURCE_ID_STRING)).willReturn(true);

        underTest.deleteById(ALLOCATED_RESOURCE_ID);

        verify(repository).deleteById(ALLOCATED_RESOURCE_ID_STRING);
    }

    @Test
    void getPageByGameId() {
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(repository.getByGameId(GAME_ID_STRING, PageRequest.of(PAGE, ITEMS_PER_PAGE))).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(model));

        assertThat(underTest.getPageByGameId(GAME_ID, PAGE, ITEMS_PER_PAGE)).containsExactly(model);
    }
}