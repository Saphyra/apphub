package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.production_order;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProductionOrderModel;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ProductionOrderDaoTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final String GAME_ID_STRING = "game-id";
    private static final UUID PRODUCTION_ORDER_ID = UUID.randomUUID();
    private static final String PRODUCTION_ORDER_ID_STRING = "production-order-id";
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String LOCATION_STRING = "location";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private ProductionOrderConverter converter;

    @Mock
    private ProductionOrderRepository repository;

    @InjectMocks
    private ProductionOrderDao underTest;

    @Mock
    private ProductionOrderModel model;

    @Mock
    private ProductionOrderEntity entity;

    @Test
    public void deleteByGameId() {
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);

        underTest.deleteByGameId(GAME_ID);

        verify(repository).deleteByGameId(GAME_ID_STRING);
    }

    @Test
    public void findById() {
        given(uuidConverter.convertDomain(PRODUCTION_ORDER_ID)).willReturn(PRODUCTION_ORDER_ID_STRING);
        given(repository.findById(PRODUCTION_ORDER_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(model));

        Optional<ProductionOrderModel> result = underTest.findById(PRODUCTION_ORDER_ID);

        assertThat(result).contains(model);
    }

    @Test
    public void getByLocation() {
        given(uuidConverter.convertDomain(LOCATION)).willReturn(LOCATION_STRING);
        given(repository.getByLocation(LOCATION_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(model));

        List<ProductionOrderModel> result = underTest.getByLocation(LOCATION);

        assertThat(result).containsExactly(model);
    }

    @Test
    public void deleteById() {
        given(uuidConverter.convertDomain(PRODUCTION_ORDER_ID)).willReturn(PRODUCTION_ORDER_ID_STRING);
        given(repository.existsById(PRODUCTION_ORDER_ID_STRING)).willReturn(true);

        underTest.deleteById(PRODUCTION_ORDER_ID);

        verify(repository).deleteById(PRODUCTION_ORDER_ID_STRING);
    }
}