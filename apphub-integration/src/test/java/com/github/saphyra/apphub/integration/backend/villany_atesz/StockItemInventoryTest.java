package com.github.saphyra.apphub.integration.backend.villany_atesz;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.villany_atesz.VillanyAteszStockCategoryActions;
import com.github.saphyra.apphub.integration.action.backend.villany_atesz.VillanyAteszStockItemActions;
import com.github.saphyra.apphub.integration.action.backend.villany_atesz.VillanyAteszStockItemInventoryActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.CustomAssertions;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.CreateStockItemRequest;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.StockCategoryModel;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.StockItemInventoryResponse;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class StockItemInventoryTest extends BackEndTest {
    private static final String CATEGORY_NAME = "category-name";
    private static final String NAME = "name";
    private static final String SERIAL_NUMBER = "serial-number";
    private static final Integer IN_CAR = 34;
    private static final Integer IN_STORAGE = 56;
    private static final Integer PRICE = 356;
    private static final String NEW_NAME = "new-name";
    private static final String NEW_SERIAL_NUMBER = "new-serial-number";
    private static final Integer NEW_IN_CAR = 3568;
    private static final Integer NEW_IN_STORAGE = 7458;
    private static final Integer AMOUNT = 32;
    private static final String BAR_CODE = "bar-code";
    private static final String NEW_BAR_CODE = "new-bar-code";

    @Test(groups = {"be", "villany-atesz"})
    public void stockItemInventory() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData);
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_VILLANY_ATESZ);

        createCategory(accessTokenId);
        createCategory(accessTokenId);
        List<UUID> categoryIds = getCategoryIds(accessTokenId);
        UUID stockCategoryId1 = categoryIds.get(0);
        UUID stockCategoryId2 = categoryIds.get(1);

        UUID stockItemId = createStockItem(accessTokenId, stockCategoryId1);

        editCategory_nullStockCategoryId(accessTokenId, stockItemId);
        editCategory_nonExistingStockCategoryId(accessTokenId, stockItemId);
        editCategory(accessTokenId, stockItemId, stockCategoryId2);

        editMarkedForAcquisition_null(accessTokenId, stockItemId);
        editMarkedForAcquisition(accessTokenId, stockItemId);

        editInventoried_null(accessTokenId, stockItemId);
        editInventoried(accessTokenId, stockItemId);

        resetInventoried(accessTokenId, stockItemId);

        editName_blank(accessTokenId, stockItemId);
        editName(accessTokenId, stockItemId);

        editSerialNumber_null(accessTokenId, stockItemId);
        editSerialNumber(accessTokenId, stockItemId);

        editBarCode_null(accessTokenId, stockItemId);
        editBarCode(accessTokenId, stockItemId);

        editInCar_null(accessTokenId, stockItemId);
        editInCar(accessTokenId, stockItemId);

        editInStorage_null(accessTokenId, stockItemId);
        editInStorage(accessTokenId, stockItemId);

        moveStockToCar_zeroAmount(accessTokenId, stockItemId);
        moveStockToCar(accessTokenId, stockItemId);

        moveStockToStorage_zeroAmount(accessTokenId, stockItemId);
        moveStockToStorage(accessTokenId, stockItemId);
    }

    private void resetInventoried(UUID accessTokenId, UUID stockItemId) {
        CustomAssertions.singleListAssertThat(VillanyAteszStockItemInventoryActions.resetInventoried(accessTokenId))
            .returns(false, StockItemInventoryResponse::getInventoried);
    }

    private void editCategory_nullStockCategoryId(UUID accessTokenId, UUID stockItemId) {
        ResponseValidator.verifyInvalidParam(VillanyAteszStockItemInventoryActions.getEditCategoryResponse(accessTokenId, stockItemId, null), "stockCategoryId", "must not be null");
    }

    private void editCategory_nonExistingStockCategoryId(UUID accessTokenId, UUID stockItemId) {
        ResponseValidator.verifyErrorResponse(VillanyAteszStockItemInventoryActions.getEditCategoryResponse(accessTokenId, stockItemId, UUID.randomUUID()), 404, ErrorCode.DATA_NOT_FOUND);
    }

    private void editCategory(UUID accessTokenId, UUID stockItemId, UUID stockCategoryId) {
        VillanyAteszStockItemInventoryActions.editCategory(accessTokenId, stockItemId, stockCategoryId);

        CustomAssertions.singleListAssertThat(VillanyAteszStockItemInventoryActions.getItems(accessTokenId))
            .returns(stockCategoryId, StockItemInventoryResponse::getStockCategoryId);
    }

    private void editMarkedForAcquisition_null(UUID accessTokenId, UUID stockItemId) {
        ResponseValidator.verifyInvalidParam(VillanyAteszStockItemInventoryActions.getEditMarkedForAcquisitionResponse(accessTokenId, stockItemId, null), "markedForAcquisition", "must not be null");
    }

    private void editMarkedForAcquisition(UUID accessTokenId, UUID stockItemId) {
        VillanyAteszStockItemInventoryActions.getEditMarkedForAcquisitionResponse(accessTokenId, stockItemId, true);

        CustomAssertions.singleListAssertThat(VillanyAteszStockItemInventoryActions.getItems(accessTokenId))
            .returns(true, StockItemInventoryResponse::getMarkedForAcquisition);
    }

    private void editInventoried_null(UUID accessTokenId, UUID stockItemId) {
        ResponseValidator.verifyInvalidParam(VillanyAteszStockItemInventoryActions.getEditInventoriedResponse(accessTokenId, stockItemId, null), "inventoried", "must not be null");
    }

    private void editInventoried(UUID accessTokenId, UUID stockItemId) {
        VillanyAteszStockItemInventoryActions.editInventoried(accessTokenId, stockItemId, true);

        CustomAssertions.singleListAssertThat(VillanyAteszStockItemInventoryActions.getItems(accessTokenId))
            .returns(true, StockItemInventoryResponse::getInventoried);
    }

    private void editName_blank(UUID accessTokenId, UUID stockItemId) {
        ResponseValidator.verifyInvalidParam(VillanyAteszStockItemInventoryActions.getEditNameResponse(accessTokenId, stockItemId, " "), "name", "must not be null or blank");
    }

    private void editName(UUID accessTokenId, UUID stockItemId) {
        VillanyAteszStockItemInventoryActions.editName(accessTokenId, stockItemId, NEW_NAME);

        CustomAssertions.singleListAssertThat(VillanyAteszStockItemInventoryActions.getItems(accessTokenId))
            .returns(NEW_NAME, StockItemInventoryResponse::getName);
    }

    private void editSerialNumber_null(UUID accessTokenId, UUID stockItemId) {
        ResponseValidator.verifyInvalidParam(VillanyAteszStockItemInventoryActions.getEditSerialNumberResponse(accessTokenId, stockItemId, null), "serialNumber", "must not be null");
    }

    private void editSerialNumber(UUID accessTokenId, UUID stockItemId) {
        VillanyAteszStockItemInventoryActions.editSerialNumber(accessTokenId, stockItemId, NEW_SERIAL_NUMBER);

        CustomAssertions.singleListAssertThat(VillanyAteszStockItemInventoryActions.getItems(accessTokenId))
            .returns(NEW_SERIAL_NUMBER, StockItemInventoryResponse::getSerialNumber);
    }

    private void editBarCode_null(UUID accessTokenId, UUID stockItemId) {
        ResponseValidator.verifyInvalidParam(VillanyAteszStockItemInventoryActions.getEditBarCodeResponse(accessTokenId, stockItemId, null), "barCode", "must not be null");
    }

    private void editBarCode(UUID accessTokenId, UUID stockItemId) {
        VillanyAteszStockItemInventoryActions.editBarCode(accessTokenId, stockItemId, NEW_BAR_CODE);

        CustomAssertions.singleListAssertThat(VillanyAteszStockItemInventoryActions.getItems(accessTokenId))
            .returns(NEW_BAR_CODE, StockItemInventoryResponse::getBarCode);
    }

    private void editInCar_null(UUID accessTokenId, UUID stockItemId) {
        ResponseValidator.verifyInvalidParam(VillanyAteszStockItemInventoryActions.getEditInCarResponse(accessTokenId, stockItemId, null), "inCar", "must not be null");
    }

    private void editInCar(UUID accessTokenId, UUID stockItemId) {
        VillanyAteszStockItemInventoryActions.editInCar(accessTokenId, stockItemId, NEW_IN_CAR);

        CustomAssertions.singleListAssertThat(VillanyAteszStockItemInventoryActions.getItems(accessTokenId))
            .returns(NEW_IN_CAR, StockItemInventoryResponse::getInCar);
    }

    private void editInStorage_null(UUID accessTokenId, UUID stockItemId) {
        ResponseValidator.verifyInvalidParam(VillanyAteszStockItemInventoryActions.getEditInStorageResponse(accessTokenId, stockItemId, null), "inStorage", "must not be null");
    }

    private void editInStorage(UUID accessTokenId, UUID stockItemId) {
        VillanyAteszStockItemInventoryActions.editInStorage(accessTokenId, stockItemId, NEW_IN_STORAGE);

        CustomAssertions.singleListAssertThat(VillanyAteszStockItemInventoryActions.getItems(accessTokenId))
            .returns(NEW_IN_STORAGE, StockItemInventoryResponse::getInStorage);
    }

    private void moveStockToCar_zeroAmount(UUID accessTokenId, UUID stockItemId) {
        ResponseValidator.verifyInvalidParam(VillanyAteszStockItemInventoryActions.getMoveStockToCarResponse(accessTokenId, stockItemId, 0), "amount", "must not be zero");
    }

    private void moveStockToCar(UUID accessTokenId, UUID stockItemId) {
        CustomAssertions.singleListAssertThat(VillanyAteszStockItemInventoryActions.moveStockToCar(accessTokenId, stockItemId, AMOUNT))
            .returns(NEW_IN_CAR + AMOUNT, StockItemInventoryResponse::getInCar)
            .returns(NEW_IN_STORAGE - AMOUNT, StockItemInventoryResponse::getInStorage);
    }

    private void moveStockToStorage_zeroAmount(UUID accessTokenId, UUID stockItemId) {
        ResponseValidator.verifyInvalidParam(VillanyAteszStockItemInventoryActions.getMoveStockToStorageResponse(accessTokenId, stockItemId, 0), "amount", "must not be zero");
    }

    private void moveStockToStorage(UUID accessTokenId, UUID stockItemId) {
        CustomAssertions.singleListAssertThat(VillanyAteszStockItemInventoryActions.moveStockToStorage(accessTokenId, stockItemId, AMOUNT))
            .returns(NEW_IN_CAR, StockItemInventoryResponse::getInCar)
            .returns(NEW_IN_STORAGE, StockItemInventoryResponse::getInStorage);
    }

    //Utils
    private UUID createStockItem(UUID accessTokenId, UUID stockCategoryId) {
        CreateStockItemRequest request = CreateStockItemRequest.builder()
            .stockCategoryId(stockCategoryId)
            .name(NAME)
            .serialNumber(SERIAL_NUMBER)
            .barCode(BAR_CODE)
            .inCar(IN_CAR)
            .inStorage(IN_STORAGE)
            .price(PRICE)
            .build();

        VillanyAteszStockItemActions.create(accessTokenId, request);

        return VillanyAteszStockItemActions.getStockItems(accessTokenId)
            .stream()
            .findFirst()
            .orElseThrow()
            .getStockItemId();
    }

    private List<UUID> getCategoryIds(UUID accessTokenId) {
        return VillanyAteszStockCategoryActions.getStockCategories(accessTokenId)
            .stream()
            .map(StockCategoryModel::getStockCategoryId)
            .collect(Collectors.toList());
    }

    private void createCategory(UUID accessTokenId) {
        StockCategoryModel request = StockCategoryModel.builder()
            .name(CATEGORY_NAME)
            .measurement("")
            .build();

        VillanyAteszStockCategoryActions.create(accessTokenId, request);
    }
}
