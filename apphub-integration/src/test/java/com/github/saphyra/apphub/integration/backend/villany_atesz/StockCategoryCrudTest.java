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
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.AddToCartRequest;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.CartView;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.ContactModel;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.CreateStockItemRequest;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.StockCategoryModel;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class StockCategoryCrudTest extends BackEndTest {
    private static final String MEASUREMENT = "measurement";
    private static final String NAME = "name";
    private static final String NEW_MEASUREMENT = "new-measurement";
    private static final String NEW_NAME = "new-name";
    private static final Integer AMOUNT = 4;
    private static final Integer PRICE = 46;

    @Test(groups = {"be", "villany-atesz"})
    public void stockCategoryCrud() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData);
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_VILLANY_ATESZ);

        create_blankName(accessTokenId);
        create_nullMeasurement(accessTokenId);
        create(accessTokenId);

        UUID stockCategoryId = getStockCategoryId(accessTokenId);

        edit_blankName(accessTokenId, stockCategoryId);
        edit_nullMeasurement(accessTokenId, stockCategoryId);
        edit(accessTokenId, stockCategoryId);

        delete(accessTokenId, stockCategoryId);
    }

    private UUID getStockCategoryId(UUID accessTokenId) {
        return VillanyAteszStockCategoryActions.getStockCategories(accessTokenId)
            .stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("User has no stock category created."))
            .getStockCategoryId();
    }

    private void create_blankName(UUID accessTokenId) {
        StockCategoryModel request = StockCategoryModel.builder()
            .name(" ")
            .measurement(MEASUREMENT)
            .build();

        ResponseValidator.verifyInvalidParam(VillanyAteszStockCategoryActions.getCreateResponse(accessTokenId, request), "name", "must not be null or blank");
    }

    private void create_nullMeasurement(UUID accessTokenId) {
        StockCategoryModel request = StockCategoryModel.builder()
            .name(NAME)
            .measurement(null)
            .build();

        ResponseValidator.verifyInvalidParam(VillanyAteszStockCategoryActions.getCreateResponse(accessTokenId, request), "measurement", "must not be null");
    }

    private void create(UUID accessTokenId) {
        StockCategoryModel request = StockCategoryModel.builder()
            .name(NAME)
            .measurement(MEASUREMENT)
            .build();

        CustomAssertions.singleListAssertThat(VillanyAteszStockCategoryActions.create(accessTokenId, request))
            .returns(NAME, StockCategoryModel::getName)
            .returns(MEASUREMENT, StockCategoryModel::getMeasurement);
    }

    private void edit_blankName(UUID accessTokenId, UUID stockCategoryId) {
        StockCategoryModel request = StockCategoryModel.builder()
            .name(" ")
            .measurement(NEW_MEASUREMENT)
            .build();

        ResponseValidator.verifyInvalidParam(VillanyAteszStockCategoryActions.getEditResponse(accessTokenId, stockCategoryId, request), "name", "must not be null or blank");
    }

    private void edit_nullMeasurement(UUID accessTokenId, UUID stockCategoryId) {
        StockCategoryModel request = StockCategoryModel.builder()
            .name(NEW_NAME)
            .measurement(null)
            .build();

        ResponseValidator.verifyInvalidParam(VillanyAteszStockCategoryActions.getEditResponse(accessTokenId, stockCategoryId, request), "measurement", "must not be null");
    }

    private void edit(UUID accessTokenId, UUID stockCategoryId) {
        StockCategoryModel request = StockCategoryModel.builder()
            .name(NEW_NAME)
            .measurement(NEW_MEASUREMENT)
            .build();

        CustomAssertions.singleListAssertThat(VillanyAteszStockCategoryActions.edit(accessTokenId, stockCategoryId, request))
            .returns(NEW_NAME, StockCategoryModel::getName)
            .returns(NEW_MEASUREMENT, StockCategoryModel::getMeasurement);
    }

    private void delete(UUID accessTokenId, UUID stockCategoryId) {
        CreateStockItemRequest createStockItemRequest = CreateStockItemRequest.builder()
            .stockCategoryId(stockCategoryId)
            .name(NAME)
            .serialNumber("")
            .inCar(0)
            .inStorage(0)
            .price(PRICE)
            .build();
        VillanyAteszStockItemActions.create(accessTokenId, createStockItemRequest);
        UUID stockItemId = VillanyAteszStockItemActions.getStockItems(accessTokenId)
            .stream()
            .findFirst()
            .orElseThrow()
            .getStockItemId();

        ContactModel contactModel = ContactModel.builder()
            .name(NAME)
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
        UUID cartId = VillanyAteszCartActions.getCarts(accessTokenId)
            .stream()
            .findFirst()
            .orElseThrow()
            .getCartId();

        AddToCartRequest addToCartRequest = AddToCartRequest.builder()
            .cartId(cartId)
            .stockItemId(stockItemId)
            .amount(AMOUNT)
            .build();
        VillanyAteszCartActions.addToCart(accessTokenId, addToCartRequest);

        assertThat(VillanyAteszStockCategoryActions.delete(accessTokenId, stockCategoryId)).isEmpty();
        assertThat(VillanyAteszStockItemActions.getStockItems(accessTokenId)).isEmpty();
        assertThat(VillanyAteszCartActions.getCart(accessTokenId, cartId))
            .returns(0, CartView::getTotalPrice)
            .returns(Collections.emptyList(), CartView::getItems);
    }
}
