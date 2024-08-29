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
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.CartDeletedResponse;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.CartResponse;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.ContactModel;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.CreateStockItemRequest;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.StockCategoryModel;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CartCrudTest extends BackEndTest {
    private static final String CATEGORY_NAME = "category-name";
    private static final String STOCK_ITEM_NAME = "stock-item-name";
    private static final Integer IN_CAR = 100;
    private static final Integer IN_STORAGE = 10;
    private static final Integer PRICE = 435;
    private static final String CONTACT_NAME = "contact-name";
    private static final Integer AMOUNT = 37;
    private static final Double MARGIN = 3.5d;

    @Test(groups = {"be", "villany-atesz"})
    public void cartCrud() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_VILLANY_ATESZ);

        UUID stockItemId = createStockItem(accessTokenId);
        UUID contactId = createContact(accessTokenId);

        create_nullContactId(accessTokenId);
        create_contactNotFound(accessTokenId);
        UUID cartId = create(accessTokenId, contactId);

        editMargin_null(accessTokenId, cartId);
        editMargin_negative(accessTokenId, cartId);
        editMargin(accessTokenId, cartId);

        addToCart_nullCartId(accessTokenId, stockItemId);
        addToCart_nullStockItemId(accessTokenId, cartId);
        addToCart_zeroAmount(accessTokenId, cartId, stockItemId);
        addToCart(accessTokenId, cartId, stockItemId);

        removeFromCart(accessTokenId, cartId, stockItemId);

        finalizeCart(accessTokenId, cartId, stockItemId);
        finalizeCart_alreadyFinalized(accessTokenId, cartId);

        delete(accessTokenId, contactId, stockItemId);
    }

    private void editMargin(UUID accessTokenId, UUID cartId) {
        VillanyAteszCartActions.editMargin(getServerPort(), accessTokenId, cartId, MARGIN);
    }

    private void editMargin_negative(UUID accessTokenId, UUID cartId) {
        ResponseValidator.verifyInvalidParam(VillanyAteszCartActions.getEditMarginResponse(getServerPort(), accessTokenId, cartId, -1.0), "margin", "too low");
    }

    private void editMargin_null(UUID accessTokenId, UUID cartId) {
        ResponseValidator.verifyInvalidParam(VillanyAteszCartActions.getEditMarginResponse(getServerPort(), accessTokenId, cartId, null), "margin", "must not be null");
    }

    private void create_nullContactId(UUID accessTokenId) {
        ResponseValidator.verifyInvalidParam(VillanyAteszCartActions.getCreateResponse(getServerPort(), accessTokenId, null), "contactId", "must not be null");
    }

    private void create_contactNotFound(UUID accessTokenId) {
        ResponseValidator.verifyErrorResponse(VillanyAteszCartActions.getCreateResponse(getServerPort(), accessTokenId, UUID.randomUUID()), 404, ErrorCode.DATA_NOT_FOUND);
    }

    private UUID create(UUID accessTokenId, UUID contactId) {
        UUID cartId = VillanyAteszCartActions.create(getServerPort(), accessTokenId, contactId);

        CustomAssertions.singleListAssertThat(VillanyAteszCartActions.getCarts(getServerPort(), accessTokenId))
            .extracting(CartResponse::getContact)
            .returns(contactId, ContactModel::getContactId);

        return cartId;
    }

    private void addToCart_nullCartId(UUID accessTokenId, UUID stockItemId) {
        AddToCartRequest request = AddToCartRequest.builder()
            .cartId(null)
            .stockItemId(stockItemId)
            .amount(AMOUNT)
            .build();

        ResponseValidator.verifyInvalidParam(VillanyAteszCartActions.getAddToCartResponse(getServerPort(), accessTokenId, request), "cartId", "must not be null");
    }

    private void addToCart_nullStockItemId(UUID accessTokenId, UUID cartId) {
        AddToCartRequest request = AddToCartRequest.builder()
            .cartId(cartId)
            .stockItemId(null)
            .amount(AMOUNT)
            .build();

        ResponseValidator.verifyInvalidParam(VillanyAteszCartActions.getAddToCartResponse(getServerPort(), accessTokenId, request), "stockItemId", "must not be null");
    }

    private void addToCart_zeroAmount(UUID accessTokenId, UUID cartId, UUID stockItemId) {
        AddToCartRequest request = AddToCartRequest.builder()
            .cartId(cartId)
            .stockItemId(stockItemId)
            .amount(0)
            .build();

        ResponseValidator.verifyInvalidParam(VillanyAteszCartActions.getAddToCartResponse(getServerPort(), accessTokenId, request), "amount", "must not be zero");
    }

    private void addToCart(UUID accessTokenId, UUID cartId, UUID stockItemId) {
        AddToCartRequest request = AddToCartRequest.builder()
            .cartId(cartId)
            .stockItemId(stockItemId)
            .amount(AMOUNT)
            .build();

        assertThat(VillanyAteszCartActions.addToCart(getServerPort(), accessTokenId, request))
            .returns(stockItemId, cartModifiedResponse -> cartModifiedResponse.getItems().get(0).getStockItemId())
            .returns(STOCK_ITEM_NAME, cartModifiedResponse -> cartModifiedResponse.getItems().get(0).getName())
            .returns(AMOUNT, cartModifiedResponse -> cartModifiedResponse.getItems().get(0).getInCart())
            .returns(AMOUNT * PRICE, cartModifiedResponse -> cartModifiedResponse.getCart().getTotalPrice())
            .returns(stockItemId, cartModifiedResponse -> cartModifiedResponse.getCart().getItems().get(0).getStockItemId())
            .returns(STOCK_ITEM_NAME, cartModifiedResponse -> cartModifiedResponse.getCart().getItems().get(0).getName())
            .returns(AMOUNT, cartModifiedResponse -> cartModifiedResponse.getCart().getItems().get(0).getAmount());
    }

    private void removeFromCart(UUID accessTokenId, UUID cartId, UUID stockItemId) {
        assertThat(VillanyAteszCartActions.removeFromCart(getServerPort(), accessTokenId, cartId, stockItemId))
            .returns(stockItemId, cartModifiedResponse -> cartModifiedResponse.getItems().get(0).getStockItemId())
            .returns(STOCK_ITEM_NAME, cartModifiedResponse -> cartModifiedResponse.getItems().get(0).getName())
            .returns(0, cartModifiedResponse -> cartModifiedResponse.getCart().getTotalPrice())
            .returns(Collections.emptyList(), cartModifiedResponse -> cartModifiedResponse.getCart().getItems());
    }

    private void finalizeCart(UUID accessTokenId, UUID cartId, UUID stockItemId) {
        addToCart(accessTokenId, cartId, stockItemId);

        assertThat(VillanyAteszCartActions.finalize(getServerPort(), accessTokenId, cartId))
            .returns(IN_CAR - AMOUNT, cartDeletedResponse -> cartDeletedResponse.getItems().get(0).getInCar())
            .returns(Collections.emptyList(), CartDeletedResponse::getCarts);
    }

    private void finalizeCart_alreadyFinalized(UUID accessTokenId, UUID cartId) {
        ResponseValidator.verifyForbiddenOperation(VillanyAteszCartActions.getFinalizeCartResponse(getServerPort(), accessTokenId, cartId));
    }

    private void delete(UUID accessTokenId, UUID contactId, UUID stockItemId) {
        UUID cartId = create(accessTokenId, contactId);

        addToCart(accessTokenId, cartId, stockItemId);

        assertThat(VillanyAteszCartActions.delete(getServerPort(), accessTokenId, cartId))
            .returns(IN_CAR - AMOUNT, cartDeletedResponse -> cartDeletedResponse.getItems().get(0).getInCar())
            .returns(0, cartDeletedResponse -> cartDeletedResponse.getItems().get(0).getInCart())
            .returns(Collections.emptyList(), CartDeletedResponse::getCarts);
    }

    //Utils
    private UUID createContact(UUID accessTokenId) {
        ContactModel request = ContactModel.builder()
            .name(CONTACT_NAME)
            .code("")
            .phone("")
            .address("")
            .note("")
            .build();

        return VillanyAteszContactActions.createContact(getServerPort(), accessTokenId, request)
            .stream()
            .findFirst()
            .orElseThrow()
            .getContactId();
    }

    private UUID createStockItem(UUID accessTokenId) {
        StockCategoryModel stockCategoryModel = StockCategoryModel.builder()
            .name(CATEGORY_NAME)
            .measurement("")
            .build();

        UUID stockCategoryId = VillanyAteszStockCategoryActions.create(getServerPort(), accessTokenId, stockCategoryModel)
            .stream()
            .findFirst()
            .orElseThrow()
            .getStockCategoryId();

        CreateStockItemRequest request = CreateStockItemRequest.builder()
            .stockCategoryId(stockCategoryId)
            .name(STOCK_ITEM_NAME)
            .serialNumber("")
            .barCode("")
            .inCar(IN_CAR)
            .inStorage(IN_STORAGE)
            .price(PRICE)
            .build();

        VillanyAteszStockItemActions.create(getServerPort(), accessTokenId, request);

        return VillanyAteszStockItemActions.getStockItems(getServerPort(), accessTokenId)
            .stream()
            .findFirst()
            .orElseThrow()
            .getStockItemId();
    }
}
