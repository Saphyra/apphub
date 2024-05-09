package com.github.saphyra.apphub.service.custom.villany_atesz.stock;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockItemInventoryResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.inventory.EditStockItemService;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.inventory.StockItemInventoryQueryService;
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
public class StockInventoryControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID STOCK_CATEGORY_ID = UUID.randomUUID();
    private static final UUID STOCK_ITEM_ID = UUID.randomUUID();
    private static final String NAME = "name";
    private static final String SERIAL_NUMBER = "serial-number";
    private static final Integer IN_CAR = 4;
    private static final Integer IN_STORAGE = 35;

    @Mock
    private StockItemInventoryQueryService stockItemInventoryQueryService;

    @Mock
    private EditStockItemService editStockItemService;

    @InjectMocks
    private StockInventoryControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private StockItemInventoryResponse stockItemInventoryResponse;

    @BeforeEach
    void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    void getItemsForInventory() {
        given(stockItemInventoryQueryService.getItems(USER_ID)).willReturn(List.of(stockItemInventoryResponse));

        assertThat(underTest.getItemsForInventory(accessTokenHeader)).containsExactly(stockItemInventoryResponse);
    }

    @Test
    void editCategory() {
        underTest.editCategory(new OneParamRequest<>(STOCK_CATEGORY_ID), STOCK_ITEM_ID, accessTokenHeader);

        then(editStockItemService).should().editCategory(STOCK_ITEM_ID, STOCK_CATEGORY_ID);
    }

    @Test
    void editInventoried() {
        underTest.editInventoried(new OneParamRequest<>(true), STOCK_ITEM_ID, accessTokenHeader);

        then(editStockItemService).should().editInventoried(STOCK_ITEM_ID, true);
    }

    @Test
    void editName() {
        underTest.editName(new OneParamRequest<>(NAME), STOCK_ITEM_ID, accessTokenHeader);

        then(editStockItemService).should().editName(STOCK_ITEM_ID, NAME);
    }

    @Test
    void editSerialNumber() {
        underTest.editSerialNumber(new OneParamRequest<>(SERIAL_NUMBER), STOCK_ITEM_ID, accessTokenHeader);

        then(editStockItemService).should().editSerialNumber(STOCK_ITEM_ID, SERIAL_NUMBER);
    }

    @Test
    void editInCar() {
        underTest.editInCar(new OneParamRequest<>(IN_CAR), STOCK_ITEM_ID, accessTokenHeader);

        then(editStockItemService).should().editInCar(STOCK_ITEM_ID, IN_CAR);
    }

    @Test
    void editInStorage() {
        underTest.editInStorage(new OneParamRequest<>(IN_STORAGE), STOCK_ITEM_ID, accessTokenHeader);

        then(editStockItemService).should().editInStorage(STOCK_ITEM_ID, IN_STORAGE);
    }
}