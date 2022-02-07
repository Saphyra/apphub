package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.production_order;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProductionOrderModel;
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
public class ProductionOrderServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID PRODUCTION_ORDER_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();

    @Mock
    private ProductionOrderDao productionOrderDao;

    @Mock
    private ProductionOrderModelValidator productionOrderModelValidator;

    @InjectMocks
    private ProductionOrderService underTest;

    @Mock
    private ProductionOrderModel model;


    @Test
    public void deleteByGameId() {
        underTest.deleteByGameId(GAME_ID);

        verify(productionOrderDao).deleteByGameId(GAME_ID);
    }

    @Test
    public void getType() {
        assertThat(underTest.getType()).isEqualTo(GameItemType.PRODUCTION_ORDER);
    }

    @Test
    public void save() {
        underTest.save(Arrays.asList(model));

        verify(productionOrderModelValidator).validate(model);
        verify(productionOrderDao).saveAll(Arrays.asList(model));
    }

    @Test
    public void findById() {
        given(productionOrderDao.findById(PRODUCTION_ORDER_ID)).willReturn(Optional.of(model));

        Optional<ProductionOrderModel> result = underTest.findById(PRODUCTION_ORDER_ID);

        assertThat(result).contains(model);
    }

    @Test
    public void getByParent() {
        given(productionOrderDao.getByLocation(LOCATION)).willReturn(Arrays.asList(model));

        List<ProductionOrderModel> result = underTest.getByParent(LOCATION);

        assertThat(result).containsExactly(model);
    }

    @Test
    public void deleteById() {
        underTest.deleteById(PRODUCTION_ORDER_ID);

        verify(productionOrderDao).deleteById(PRODUCTION_ORDER_ID);
    }
}