package com.github.saphyra.apphub.service.custom.villany_atesz.stock;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockItemInventoryResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.server.StockInventoryController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.inventory.EditItemService;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.inventory.StockItemInventoryQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class StockInventoryControllerImpl implements StockInventoryController {
    private final StockItemInventoryQueryService stockItemInventoryQueryService;
    private final EditItemService editItemService;

    @Override
    public List<StockItemInventoryResponse> getItemsForInventory(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know their items for inventory.", accessTokenHeader.getUserId());

        return stockItemInventoryQueryService.getItems(accessTokenHeader.getUserId());
    }

    @Override
    public void editCategory(OneParamRequest<UUID> category, UUID stockItemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to edit the category of stockItem {}", accessTokenHeader.getUserId(), stockItemId);

        editItemService.editCategory(stockItemId, category.getValue());
    }

    @Override
    public void editName(OneParamRequest<String> name, UUID stockItemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to edit the name of stockItem {}", accessTokenHeader.getUserId(), stockItemId);

        editItemService.editName(stockItemId, name.getValue());
    }

    @Override
    public void editSerialNumber(OneParamRequest<String> serialNumber, UUID stockItemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to edit the serialNumber of stockItem {}", accessTokenHeader.getUserId(), stockItemId);

        editItemService.editSerialNumber(stockItemId, serialNumber.getValue());
    }

    @Override
    public void editInCar(OneParamRequest<Integer> inCar, UUID stockItemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to edit the inCar of stockItem {}", accessTokenHeader.getUserId(), stockItemId);

        editItemService.editInCar(stockItemId, inCar.getValue());
    }

    @Override
    public void editInStorage(OneParamRequest<Integer> inStorage, UUID stockItemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to edit the inStorage of stockItem {}", accessTokenHeader.getUserId(), stockItemId);

        editItemService.editInStorage(stockItemId, inStorage.getValue());
    }
}
