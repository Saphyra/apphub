package com.github.saphyra.apphub.service.custom.villany_atesz.stock;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.AddToStockRequest;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockItemForCategoryResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockItemOverviewResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.CreateStockItemRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.item.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class StockItemControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID STOCK_ITEM_ID = UUID.randomUUID();
    private static final UUID STOCK_CATEGORY_ID = UUID.randomUUID();
    private static final Integer AMOUNT = 35;

    @Mock
    private CreateStockItemService createStockItemService;

    @Mock
    private DeleteStockItemService deleteStockItemService;

    @Mock
    private StockItemQueryService stockItemQueryService;

    @Mock
    private AcquisitionService acquisitionService;

    @Mock
    private MoveStockService moveStockService;

    @InjectMocks
    private StockItemControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private CreateStockItemRequest createStockItemRequest;

    @Mock
    private StockItemOverviewResponse stockItemOverviewResponse;

    @Mock
    private StockItemForCategoryResponse stockItemForCategoryResponse;

    @Mock
    private AddToStockRequest addToStockRequest;

    @BeforeEach
    void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    void createStockItem() {
        underTest.createStockItem(createStockItemRequest, accessTokenHeader);

        then(createStockItemService).should().create(USER_ID, createStockItemRequest);
    }

    @Test
    void getStockItems() {
        given(stockItemQueryService.getStockItems(USER_ID)).willReturn(List.of(stockItemOverviewResponse));

        assertThat(underTest.getStockItems(accessTokenHeader)).containsExactly(stockItemOverviewResponse);
    }

    @Test
    void deleteStockItem() {
        given(stockItemQueryService.getStockItems(USER_ID)).willReturn(List.of(stockItemOverviewResponse));

        assertThat(underTest.deleteStockItem(STOCK_ITEM_ID, accessTokenHeader)).containsExactly(stockItemOverviewResponse);

        then(deleteStockItemService).should().delete(USER_ID, STOCK_ITEM_ID);
    }

    @Test
    void getStockItemForCategory() {
        given(stockItemQueryService.getForCategory(STOCK_CATEGORY_ID)).willReturn(List.of(stockItemForCategoryResponse));

        assertThat(underTest.getStockItemsForCategory(STOCK_CATEGORY_ID, accessTokenHeader)).containsExactly(stockItemForCategoryResponse);
    }

    @Test
    void acquire() {
        underTest.acquire(List.of(addToStockRequest), accessTokenHeader);

        then(acquisitionService).should().acquire(List.of(addToStockRequest));
    }

    @Test
    void moveStockToCar() {
        given(stockItemQueryService.getStockItems(USER_ID)).willReturn(List.of(stockItemOverviewResponse));

        assertThat(underTest.moveStockToCar(new OneParamRequest<>(AMOUNT), STOCK_ITEM_ID, accessTokenHeader)).containsExactly(stockItemOverviewResponse);

        then(moveStockService).should().moveToCar(STOCK_ITEM_ID, AMOUNT);
    }

    @Test
    void moveStockToStorage() {
        given(stockItemQueryService.getStockItems(USER_ID)).willReturn(List.of(stockItemOverviewResponse));

        assertThat(underTest.moveStockToStorage(new OneParamRequest<>(AMOUNT), STOCK_ITEM_ID, accessTokenHeader)).containsExactly(stockItemOverviewResponse);

        then(moveStockService).should().moveToStorage(STOCK_ITEM_ID, AMOUNT);
    }
}