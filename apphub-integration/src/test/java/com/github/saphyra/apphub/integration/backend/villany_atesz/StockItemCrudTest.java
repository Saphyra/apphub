package com.github.saphyra.apphub.integration.backend.villany_atesz;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.villany_atesz.VillanyAteszCartActions;
import com.github.saphyra.apphub.integration.action.backend.villany_atesz.VillanyAteszContactActions;
import com.github.saphyra.apphub.integration.action.backend.villany_atesz.VillanyAteszStockCategoryActions;
import com.github.saphyra.apphub.integration.action.backend.villany_atesz.VillanyAteszStockItemActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.CustomAssertions;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.AddToCartRequest;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.AddToStockRequest;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.CartView;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.ContactModel;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.CreateStockItemRequest;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.StockCategoryModel;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.StockItemOverviewResponse;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class StockItemCrudTest extends BackEndTest {
    private static final String CATEGORY_NAME = "category-name";
    private static final String NAME = "name";
    private static final String SERIAL_NUMBER = "serial-number";
    private static final Integer IN_CAR = 4;
    private static final Integer IN_STORAGE = 35;
    private static final Integer PRICE = 355;
    private static final Integer ACQUIRE_IN_CAR = 312;
    private static final Integer ACQUIRE_IN_STORAGE = 3;
    private static final Integer ACQUIRE_PRICE = PRICE + 1;
    private static final String CONTACT_NAME = "contact-name";
    private static final Integer IN_CART = 3;

    @Test(groups = {"be", "villany-atesz"})
    public void stockItemCrud() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData);
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_VILLANY_ATESZ);

        UUID stockCategoryId = createCategory(accessTokenId);

        create_nullStockCategoryId(accessTokenId);
        create_stockCategoryNotFound(accessTokenId);
        create_blankName(accessTokenId, stockCategoryId);
        create_nullSerialNumber(accessTokenId, stockCategoryId);
        create_nullInCar(accessTokenId, stockCategoryId);
        create_nullInStorage(accessTokenId, stockCategoryId);
        create_nullPrice(accessTokenId, stockCategoryId);
        create(accessTokenId, stockCategoryId);

        UUID stockItemId = getStockItemId(accessTokenId);

        acquire_nullStockItemId(accessTokenId);
        acquire_nullInCar(accessTokenId, stockItemId);
        acquire_nullInStorage(accessTokenId, stockItemId);
        acquire_nullPrice(accessTokenId, stockItemId);
        acquire(accessTokenId, stockItemId);

        moveStockToCar_zeroAmount(accessTokenId, stockItemId);
        moveStockToCar(accessTokenId, stockItemId);

        moveStockToStorage_zeroAmount(accessTokenId, stockItemId);
        moveStockToStorage(accessTokenId, stockItemId);

        addToCart(accessTokenId, stockItemId);

        delete(accessTokenId, stockItemId);
    }

    private void create_nullStockCategoryId(UUID accessTokenId) {
        CreateStockItemRequest request = CreateStockItemRequest.builder()
            .stockCategoryId(null)
            .name(NAME)
            .serialNumber(SERIAL_NUMBER)
            .inCar(IN_CAR)
            .inStorage(IN_STORAGE)
            .price(PRICE)
            .build();

        ResponseValidator.verifyInvalidParam(VillanyAteszStockItemActions.getCreateResponse(accessTokenId, request), "stockCategoryId", "must not be null");
    }

    private void create_stockCategoryNotFound(UUID accessTokenId) {
        CreateStockItemRequest request = CreateStockItemRequest.builder()
            .stockCategoryId(UUID.randomUUID())
            .name(NAME)
            .serialNumber(SERIAL_NUMBER)
            .inCar(IN_CAR)
            .inStorage(IN_STORAGE)
            .price(PRICE)
            .build();

        ResponseValidator.verifyErrorResponse(VillanyAteszStockItemActions.getCreateResponse(accessTokenId, request), 404, ErrorCode.DATA_NOT_FOUND);
    }

    private void create_blankName(UUID accessTokenId, UUID stockCategoryId) {
        CreateStockItemRequest request = CreateStockItemRequest.builder()
            .stockCategoryId(stockCategoryId)
            .name(" ")
            .serialNumber(SERIAL_NUMBER)
            .inCar(IN_CAR)
            .inStorage(IN_STORAGE)
            .price(PRICE)
            .build();

        ResponseValidator.verifyInvalidParam(VillanyAteszStockItemActions.getCreateResponse(accessTokenId, request), "name", "must not be null or blank");
    }

    private void create_nullSerialNumber(UUID accessTokenId, UUID stockCategoryId) {
        CreateStockItemRequest request = CreateStockItemRequest.builder()
            .stockCategoryId(stockCategoryId)
            .name(NAME)
            .serialNumber(null)
            .inCar(IN_CAR)
            .inStorage(IN_STORAGE)
            .price(PRICE)
            .build();

        ResponseValidator.verifyInvalidParam(VillanyAteszStockItemActions.getCreateResponse(accessTokenId, request), "serialNumber", "must not be null");
    }

    private void create_nullInCar(UUID accessTokenId, UUID stockCategoryId) {
        CreateStockItemRequest request = CreateStockItemRequest.builder()
            .stockCategoryId(stockCategoryId)
            .name(NAME)
            .serialNumber(SERIAL_NUMBER)
            .inCar(null)
            .inStorage(IN_STORAGE)
            .price(PRICE)
            .build();

        ResponseValidator.verifyInvalidParam(VillanyAteszStockItemActions.getCreateResponse(accessTokenId, request), "inCar", "must not be null");
    }

    private void create_nullInStorage(UUID accessTokenId, UUID stockCategoryId) {
        CreateStockItemRequest request = CreateStockItemRequest.builder()
            .stockCategoryId(stockCategoryId)
            .name(NAME)
            .serialNumber(SERIAL_NUMBER)
            .inCar(IN_CAR)
            .inStorage(null)
            .price(PRICE)
            .build();

        ResponseValidator.verifyInvalidParam(VillanyAteszStockItemActions.getCreateResponse(accessTokenId, request), "inStorage", "must not be null");
    }

    private void create_nullPrice(UUID accessTokenId, UUID stockCategoryId) {
        CreateStockItemRequest request = CreateStockItemRequest.builder()
            .stockCategoryId(stockCategoryId)
            .name(NAME)
            .serialNumber(SERIAL_NUMBER)
            .inCar(IN_CAR)
            .inStorage(IN_STORAGE)
            .price(null)
            .build();

        ResponseValidator.verifyInvalidParam(VillanyAteszStockItemActions.getCreateResponse(accessTokenId, request), "price", "must not be null");
    }

    private void create(UUID accessTokenId, UUID stockCategoryId) {
        CreateStockItemRequest request = CreateStockItemRequest.builder()
            .stockCategoryId(stockCategoryId)
            .name(NAME)
            .serialNumber(SERIAL_NUMBER)
            .inCar(IN_CAR)
            .inStorage(IN_STORAGE)
            .price(PRICE)
            .build();

        VillanyAteszStockItemActions.create(accessTokenId, request);

        CustomAssertions.singleListAssertThat(VillanyAteszStockItemActions.getStockItems(accessTokenId))
            .returns(stockCategoryId, stockItemOverviewResponse -> stockItemOverviewResponse.getCategory().getStockCategoryId())
            .returns(NAME, StockItemOverviewResponse::getName)
            .returns(SERIAL_NUMBER, StockItemOverviewResponse::getSerialNumber)
            .returns(IN_CAR, StockItemOverviewResponse::getInCar)
            .returns(IN_STORAGE, StockItemOverviewResponse::getInStorage)
            .returns(PRICE, StockItemOverviewResponse::getPrice);
    }

    private void acquire_nullStockItemId(UUID accessTokenId) {
        AddToStockRequest request = AddToStockRequest.builder()
            .stockItemId(null)
            .inCar(ACQUIRE_IN_CAR)
            .inStorage(ACQUIRE_IN_STORAGE)
            .price(ACQUIRE_PRICE)
            .build();

        ResponseValidator.verifyInvalidParam(VillanyAteszStockItemActions.getAcquireResponse(accessTokenId, request), "stockItemId", "must not be null");
    }

    private void acquire_nullInCar(UUID accessTokenId, UUID stockItemId) {
        AddToStockRequest request = AddToStockRequest.builder()
            .stockItemId(stockItemId)
            .inCar(null)
            .inStorage(ACQUIRE_IN_STORAGE)
            .price(ACQUIRE_PRICE)
            .build();

        ResponseValidator.verifyInvalidParam(VillanyAteszStockItemActions.getAcquireResponse(accessTokenId, request), "inCar", "must not be null");
    }

    private void acquire_nullInStorage(UUID accessTokenId, UUID stockItemId) {
        AddToStockRequest request = AddToStockRequest.builder()
            .stockItemId(stockItemId)
            .inCar(ACQUIRE_IN_CAR)
            .inStorage(null)
            .price(ACQUIRE_PRICE)
            .build();

        ResponseValidator.verifyInvalidParam(VillanyAteszStockItemActions.getAcquireResponse(accessTokenId, request), "inStorage", "must not be null");
    }

    private void acquire_nullPrice(UUID accessTokenId, UUID stockItemId) {
        AddToStockRequest request = AddToStockRequest.builder()
            .stockItemId(stockItemId)
            .inCar(ACQUIRE_IN_CAR)
            .inStorage(ACQUIRE_IN_STORAGE)
            .price(null)
            .build();

        ResponseValidator.verifyInvalidParam(VillanyAteszStockItemActions.getAcquireResponse(accessTokenId, request), "price", "must not be null");
    }

    private void acquire(UUID accessTokenId, UUID stockItemId) {
        AddToStockRequest request = AddToStockRequest.builder()
            .stockItemId(stockItemId)
            .inCar(ACQUIRE_IN_CAR)
            .inStorage(ACQUIRE_IN_STORAGE)
            .price(ACQUIRE_PRICE)
            .build();

        VillanyAteszStockItemActions.acquire(accessTokenId, request);

        CustomAssertions.singleListAssertThat(VillanyAteszStockItemActions.getStockItems(accessTokenId))
            .returns(IN_CAR + ACQUIRE_IN_CAR, StockItemOverviewResponse::getInCar)
            .returns(IN_STORAGE + ACQUIRE_IN_STORAGE, StockItemOverviewResponse::getInStorage)
            .returns(ACQUIRE_PRICE, StockItemOverviewResponse::getPrice);
    }

    private void moveStockToCar_zeroAmount(UUID accessTokenId, UUID stockItemId) {
        ResponseValidator.verifyInvalidParam(VillanyAteszStockItemActions.getMoveStockToCarResponse(accessTokenId, stockItemId, 0), "amount", "must not be zero");
    }

    private void moveStockToCar(UUID accessTokenId, UUID stockItemId) {
        CustomAssertions.singleListAssertThat(VillanyAteszStockItemActions.moveStockToCar(accessTokenId, stockItemId, ACQUIRE_IN_STORAGE))
            .returns(IN_CAR + ACQUIRE_IN_CAR + ACQUIRE_IN_STORAGE, StockItemOverviewResponse::getInCar)
            .returns(IN_STORAGE, StockItemOverviewResponse::getInStorage);
    }

    private void moveStockToStorage_zeroAmount(UUID accessTokenId, UUID stockItemId) {
        ResponseValidator.verifyInvalidParam(VillanyAteszStockItemActions.getMoveStockToStorageResponse(accessTokenId, stockItemId, 0), "amount", "must not be zero");
    }

    private void moveStockToStorage(UUID accessTokenId, UUID stockItemId) {
        CustomAssertions.singleListAssertThat(VillanyAteszStockItemActions.moveStockToStorage(accessTokenId, stockItemId, ACQUIRE_IN_STORAGE))
            .returns(IN_CAR + ACQUIRE_IN_CAR, StockItemOverviewResponse::getInCar)
            .returns(IN_STORAGE + ACQUIRE_IN_STORAGE, StockItemOverviewResponse::getInStorage);
    }

    private void addToCart(UUID accessTokenId, UUID stockItemId) {
        ContactModel contactModel = ContactModel.builder()
            .name(CONTACT_NAME)
            .code("")
            .phone("")
            .address("")
            .note("")
            .build();

        UUID contactId = VillanyAteszContactActions.createContact(accessTokenId, contactModel)
            .stream()
            .findFirst()
            .orElseThrow()
            .getContactId();

        VillanyAteszCartActions.create(accessTokenId, contactId);

        UUID cartId = getCartId(accessTokenId);

        AddToCartRequest addToCartRequest = AddToCartRequest.builder()
            .cartId(cartId)
            .stockItemId(stockItemId)
            .amount(IN_CART)
            .build();

        VillanyAteszCartActions.addToCart(accessTokenId, addToCartRequest);

        CustomAssertions.singleListAssertThat(VillanyAteszStockItemActions.getStockItems(accessTokenId))
            .returns(IN_CART, StockItemOverviewResponse::getInCart);
    }

    private void delete(UUID accessTokenId, UUID stockItemId) {
        assertThat(VillanyAteszStockItemActions.delete(accessTokenId, stockItemId)).isEmpty();

        assertThat(VillanyAteszCartActions.getCart(accessTokenId, getCartId(accessTokenId)))
            .returns(0, CartView::getTotalPrice)
            .returns(Collections.emptyList(), CartView::getItems);
    }

    //Utils
    private UUID createCategory(UUID accessTokenId) {
        StockCategoryModel request = StockCategoryModel.builder()
            .name(CATEGORY_NAME)
            .measurement("")
            .build();

        return VillanyAteszStockCategoryActions.create(accessTokenId, request)
            .stream()
            .findFirst()
            .orElseThrow()
            .getStockCategoryId();
    }

    private UUID getStockItemId(UUID accessTokenId) {
        return VillanyAteszStockItemActions.getStockItems(accessTokenId)
            .stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No stockItem created for user"))
            .getStockItemId();
    }

    private static UUID getCartId(UUID accessTokenId) {
        return VillanyAteszCartActions.getCarts(accessTokenId)
            .stream()
            .findFirst()
            .orElseThrow()
            .getCartId();
    }
}
