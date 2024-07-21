package com.github.saphyra.apphub.service.custom.villany_atesz.stock;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.AcquisitionRequest;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.CreateStockItemRequest;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockItemAcquisitionResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockItemForCategoryResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockItemOverviewResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.item.AcquireItemsService;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.item.CreateStockItemService;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.item.StockItemQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class StockItemControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID STOCK_CATEGORY_ID = UUID.randomUUID();
    private static final String BAR_CODE = "bar-code";
    private static final UUID STOCK_ITEM_ID = UUID.randomUUID();

    @Mock
    private CreateStockItemService createStockItemService;

    @Mock
    private StockItemQueryService stockItemQueryService;

    @Mock
    private AcquireItemsService acquireItemsService;

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
    private AcquisitionRequest acquisitionRequest;

    @Mock
    private StockItemAcquisitionResponse stockItemAcquisitionResponse;

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
    void getStockItemForCategory() {
        given(stockItemQueryService.getForCategory(STOCK_CATEGORY_ID)).willReturn(List.of(stockItemForCategoryResponse));

        assertThat(underTest.getStockItemsForCategory(STOCK_CATEGORY_ID, accessTokenHeader)).containsExactly(stockItemForCategoryResponse);
    }

    @Test
    void acquire() {
        underTest.acquire(acquisitionRequest, accessTokenHeader);

        then(acquireItemsService).should().acquire(USER_ID, acquisitionRequest);
    }

    @Test
    void findByBarCode() {
        given(stockItemQueryService.findByBarCode(USER_ID, BAR_CODE)).willReturn(Optional.of(stockItemAcquisitionResponse));

        assertThat(underTest.findByBarCode(new OneParamRequest<>(BAR_CODE), accessTokenHeader))
            .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
            .returns(stockItemAcquisitionResponse, HttpEntity::getBody);
    }

    @Test
    void findByBarCode_notFound() {
        given(stockItemQueryService.findByBarCode(USER_ID, BAR_CODE)).willReturn(Optional.empty());

        assertThat(underTest.findByBarCode(new OneParamRequest<>(BAR_CODE), accessTokenHeader))
            .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode);
    }

    @Test
    void findBarCodeByStockItemId() {
        given(stockItemQueryService.findBarCodeByStockItemId(STOCK_ITEM_ID)).willReturn(BAR_CODE);

        assertThat(underTest.findBarCodeByStockItemId(STOCK_ITEM_ID, accessTokenHeader))
            .returns(BAR_CODE, OneParamResponse::getValue);
    }
}