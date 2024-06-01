package com.github.saphyra.apphub.service.custom.villany_atesz.stock;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockItemInventoryResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.server.StockInventoryController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.inventory.EditStockItemService;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.inventory.StockItemInventoryQueryService;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.item.DeleteStockItemService;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.item.MoveStockService;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.item.ResetInventoriedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
class StockInventoryControllerImpl implements StockInventoryController {
    private final StockItemInventoryQueryService stockItemInventoryQueryService;
    private final EditStockItemService editStockItemService;
    private final DeleteStockItemService deleteStockItemService;
    private final MoveStockService moveStockService;
    private final ResetInventoriedService resetInventoriedService;

    @Override
    public List<StockItemInventoryResponse> getItemsForInventory(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know their items for inventory.", accessTokenHeader.getUserId());

        return stockItemInventoryQueryService.getItems(accessTokenHeader.getUserId());
    }

    @Override
    public List<StockItemInventoryResponse> deleteStockItem(UUID stockItemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to delete stockItem {}", accessTokenHeader.getUserId(), stockItemId);

        deleteStockItemService.delete(accessTokenHeader.getUserId(), stockItemId);

        return getItemsForInventory(accessTokenHeader);
    }

    @Override
    public void editCategory(OneParamRequest<UUID> stockCategoryId, UUID stockItemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to edit the category of stockItem {}", accessTokenHeader.getUserId(), stockItemId);

        editStockItemService.editCategory(stockItemId, stockCategoryId.getValue());
    }

    @Override
    public void editInventoried(OneParamRequest<Boolean> inventoried, UUID stockItemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to edit the inventoried of stockItem {}", accessTokenHeader.getUserId(), stockItemId);

        editStockItemService.editInventoried(stockItemId, inventoried.getValue());
    }

    @Override
    public void editName(OneParamRequest<String> name, UUID stockItemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to edit the name of stockItem {}", accessTokenHeader.getUserId(), stockItemId);

        editStockItemService.editName(stockItemId, name.getValue());
    }

    @Override
    public void editSerialNumber(OneParamRequest<String> serialNumber, UUID stockItemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to edit the serialNumber of stockItem {}", accessTokenHeader.getUserId(), stockItemId);

        editStockItemService.editSerialNumber(stockItemId, serialNumber.getValue());
    }

    @Override
    public void editBarCode(OneParamRequest<String> barCode, UUID stockItemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to edit the barCode of stockItem {}", accessTokenHeader.getUserId(), stockItemId);

        editStockItemService.editBarCode(stockItemId, barCode.getValue());
    }

    @Override
    public void editInCar(OneParamRequest<Integer> inCar, UUID stockItemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to edit the inCar of stockItem {}", accessTokenHeader.getUserId(), stockItemId);

        editStockItemService.editInCar(stockItemId, inCar.getValue());
    }

    @Override
    public void editInStorage(OneParamRequest<Integer> inStorage, UUID stockItemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to edit the inStorage of stockItem {}", accessTokenHeader.getUserId(), stockItemId);

        editStockItemService.editInStorage(stockItemId, inStorage.getValue());
    }

    @Override
    public List<StockItemInventoryResponse> moveStockToCar(OneParamRequest<Integer> amount, UUID stockItemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to move stock from storage to car for stockItem {}", accessTokenHeader.getUserId(), stockItemId);

        moveStockService.moveToCar(stockItemId, amount.getValue());

        return getItemsForInventory(accessTokenHeader);

    }

    @Override
    public List<StockItemInventoryResponse> moveStockToStorage(OneParamRequest<Integer> amount, UUID stockItemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to move stock from storage to storage for stockItem {}", accessTokenHeader.getUserId(), stockItemId);

        moveStockService.moveToStorage(stockItemId, amount.getValue());

        return getItemsForInventory(accessTokenHeader);
    }

    @Override
    public List<StockItemInventoryResponse> resetInventoried(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to reset inventoried status for all of their items.", accessTokenHeader.getUserId());

        resetInventoriedService.resetInventoried(accessTokenHeader.getUserId());

        return getItemsForInventory(accessTokenHeader);
    }
}
