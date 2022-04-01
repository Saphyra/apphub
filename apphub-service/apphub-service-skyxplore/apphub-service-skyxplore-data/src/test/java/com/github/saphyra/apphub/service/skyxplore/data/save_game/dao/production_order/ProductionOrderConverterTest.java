package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.production_order;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProductionOrderModel;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;


@RunWith(MockitoJUnitRunner.class)
public class ProductionOrderConverterTest {
    private static final UUID PRODUCTION_ORDER_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String LOCATION_TYPE = "location-type";
    private static final UUID ASSIGNEE = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final Integer AMOUNT = 235;
    private static final Integer REQUIRED_WORK_POINTS = 3546;
    private static final Integer CURRENT_WORK_POINTS = 467;
    private static final String PRODUCTION_ORDER_ID_STRING = "production-order-id";
    private static final String GAME_ID_STRING = "game-id";
    private static final String LOCATION_STRING = "location";
    private static final String ASSIGNEE_STRING = "assignee";
    private static final String EXTERNAL_REFERENCE_STRING = "external-reference";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private ProductionOrderConverter underTest;

    @Test
    public void convertModel() {
        ProductionOrderModel model = new ProductionOrderModel();
        model.setId(PRODUCTION_ORDER_ID);
        model.setGameId(GAME_ID);
        model.setLocation(LOCATION);
        model.setLocationType(LOCATION_TYPE);
        model.setAssignee(ASSIGNEE);
        model.setExternalReference(EXTERNAL_REFERENCE);
        model.setDataId(DATA_ID);
        model.setAmount(AMOUNT);
        model.setRequiredWorkPoints(REQUIRED_WORK_POINTS);
        model.setCurrentWorkPoints(CURRENT_WORK_POINTS);

        given(uuidConverter.convertDomain(PRODUCTION_ORDER_ID)).willReturn(PRODUCTION_ORDER_ID_STRING);
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(uuidConverter.convertDomain(LOCATION)).willReturn(LOCATION_STRING);
        given(uuidConverter.convertDomain(ASSIGNEE)).willReturn(ASSIGNEE_STRING);
        given(uuidConverter.convertDomain(EXTERNAL_REFERENCE)).willReturn(EXTERNAL_REFERENCE_STRING);

        ProductionOrderEntity result = underTest.convertDomain(model);

        assertThat(result.getProductionOrderId()).isEqualTo(PRODUCTION_ORDER_ID_STRING);
        assertThat(result.getGameId()).isEqualTo(GAME_ID_STRING);
        assertThat(result.getLocation()).isEqualTo(LOCATION_STRING);
        assertThat(result.getLocationType()).isEqualTo(LOCATION_TYPE);
        assertThat(result.getAssignee()).isEqualTo(ASSIGNEE_STRING);
        assertThat(result.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE_STRING);
        assertThat(result.getDataId()).isEqualTo(DATA_ID);
        assertThat(result.getAmount()).isEqualTo(AMOUNT);
        assertThat(result.getRequiredWorkPoints()).isEqualTo(REQUIRED_WORK_POINTS);
        assertThat(result.getCurrentWorkPoints()).isEqualTo(CURRENT_WORK_POINTS);
    }

    @Test
    public void convertEntity() {
        ProductionOrderEntity entity = new ProductionOrderEntity();
        entity.setProductionOrderId(PRODUCTION_ORDER_ID_STRING);
        entity.setGameId(GAME_ID_STRING);
        entity.setLocation(LOCATION_STRING);
        entity.setLocationType(LOCATION_TYPE);
        entity.setAssignee(ASSIGNEE_STRING);
        entity.setExternalReference(EXTERNAL_REFERENCE_STRING);
        entity.setDataId(DATA_ID);
        entity.setAmount(AMOUNT);
        entity.setRequiredWorkPoints(REQUIRED_WORK_POINTS);
        entity.setCurrentWorkPoints(CURRENT_WORK_POINTS);

        given(uuidConverter.convertEntity(PRODUCTION_ORDER_ID_STRING)).willReturn(PRODUCTION_ORDER_ID);
        given(uuidConverter.convertEntity(GAME_ID_STRING)).willReturn(GAME_ID);
        given(uuidConverter.convertEntity(LOCATION_STRING)).willReturn(LOCATION);
        given(uuidConverter.convertEntity(ASSIGNEE_STRING)).willReturn(ASSIGNEE);
        given(uuidConverter.convertEntity(EXTERNAL_REFERENCE_STRING)).willReturn(EXTERNAL_REFERENCE);

        ProductionOrderModel result = underTest.convertEntity(entity);

        assertThat(result.getId()).isEqualTo(PRODUCTION_ORDER_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getProcessType()).isEqualTo(GameItemType.PRODUCTION_ORDER);
        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getLocationType()).isEqualTo(LOCATION_TYPE);
        assertThat(result.getAssignee()).isEqualTo(ASSIGNEE);
        assertThat(result.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE);
        assertThat(result.getDataId()).isEqualTo(DATA_ID);
        assertThat(result.getAmount()).isEqualTo(AMOUNT);
        assertThat(result.getRequiredWorkPoints()).isEqualTo(REQUIRED_WORK_POINTS);
        assertThat(result.getCurrentWorkPoints()).isEqualTo(CURRENT_WORK_POINTS);
    }
}