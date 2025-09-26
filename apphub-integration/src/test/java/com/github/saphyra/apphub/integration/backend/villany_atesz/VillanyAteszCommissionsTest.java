package com.github.saphyra.apphub.integration.backend.villany_atesz;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.villany_atesz.VillanyAteszCartActions;
import com.github.saphyra.apphub.integration.action.backend.villany_atesz.VillanyAteszCommissionActions;
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
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.CommissionModel;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.CommissionView;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.ContactModel;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.CreateStockItemRequest;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.StockCategoryModel;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class VillanyAteszCommissionsTest extends BackEndTest {
    private static final Integer DAYS_OF_WORK = 1;
    private static final Integer HOURS_PER_DAY = 2;
    private static final Integer DEPARTURE_FEE = 23;
    private static final Integer HOURLY_WAGE = 24;
    private static final Integer EXTRA_COST = 25;
    private static final Integer DEPOSIT = 26;
    private static final Double MARGIN = 27d;
    private static final Integer NEW_DAYS_OF_WORK = 3;
    private static final Integer NEW_HOURS_PER_DAY = 31;
    private static final Integer NEW_DEPARTURE_FEE = 32;
    private static final Integer NEW_HOURLY_WAGE = 33;
    private static final Integer NEW_EXTRA_COST = 34;
    private static final Integer NEW_DEPOSIT = 35;
    private static final Double NEW_MARGIN = 36d;
    private static final String CONTACT_NAME_1 = "contact-name-1";
    private static final String CONTACT_NAME_2 = "contact-name-2";
    private static final String CATEGORY_NAME = "category-name";
    private static final String STOCK_ITEM_NAME_1 = "stock-item-name-1";
    private static final String STOCK_ITEM_NAME_2 = "stock-item-name-2";
    private static final Integer AMOUNT = 12;
    private static final Integer PRICE = 432;

    @Test(groups = {"villany-atesz", "be"})
    public void commissionCrud() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_VILLANY_ATESZ);

        create_nullDaysOfWork(accessTokenId);
        create_nullHoursPerDay(accessTokenId);
        create_nullDepartureFee(accessTokenId);
        create_nullHourlyWage(accessTokenId);
        create_nullExtraCost(accessTokenId);
        create_nullDeposit(accessTokenId);
        create_nullMargin(accessTokenId);
        UUID commissionId = create(accessTokenId);
        update(accessTokenId, commissionId);
        delete(accessTokenId, commissionId);
    }

    private void delete(UUID accessTokenId, UUID commissionId) {
        assertThat(VillanyAteszCommissionActions.delete(getServerPort(), accessTokenId, commissionId)).isEmpty();
    }

    private void update(UUID accessTokenId, UUID commissionId) {
        CommissionModel request = CommissionModel.builder()
            .commissionId(commissionId)
            .daysOfWork(NEW_DAYS_OF_WORK)
            .hoursPerDay(NEW_HOURS_PER_DAY)
            .departureFee(NEW_DEPARTURE_FEE)
            .hourlyWage(NEW_HOURLY_WAGE)
            .extraCost(NEW_EXTRA_COST)
            .deposit(NEW_DEPOSIT)
            .margin(NEW_MARGIN)
            .build();

        CommissionModel response = VillanyAteszCommissionActions.createOrUpdateCommission(getServerPort(), accessTokenId, request);

        assertThat(response)
            .returns(commissionId, CommissionModel::getCommissionId)
            .returns(NEW_DAYS_OF_WORK, CommissionModel::getDaysOfWork)
            .returns(NEW_HOURS_PER_DAY, CommissionModel::getHoursPerDay)
            .returns(NEW_DEPARTURE_FEE, CommissionModel::getDepartureFee)
            .returns(NEW_HOURLY_WAGE, CommissionModel::getHourlyWage)
            .returns(NEW_EXTRA_COST, CommissionModel::getExtraCost)
            .returns(NEW_DEPOSIT, CommissionModel::getDeposit)
            .returns(NEW_MARGIN, CommissionModel::getMargin)
            .extracting(CommissionModel::getCommissionId)
            .isNotNull();
    }

    private UUID create(UUID accessTokenId) {
        CommissionModel request = validRequest(null, null)
            .build();

        CommissionModel response = VillanyAteszCommissionActions.createOrUpdateCommission(getServerPort(), accessTokenId, request);

        assertThat(response)
            .returns(DAYS_OF_WORK, CommissionModel::getDaysOfWork)
            .returns(HOURS_PER_DAY, CommissionModel::getHoursPerDay)
            .returns(DEPARTURE_FEE, CommissionModel::getDepartureFee)
            .returns(HOURLY_WAGE, CommissionModel::getHourlyWage)
            .returns(EXTRA_COST, CommissionModel::getExtraCost)
            .returns(DEPOSIT, CommissionModel::getDeposit)
            .returns(MARGIN, CommissionModel::getMargin)
            .extracting(CommissionModel::getCommissionId)
            .isNotNull();

        return response.getCommissionId();
    }

    private void create_nullMargin(UUID accessTokenId) {
        CommissionModel request = validRequest(null, null)
            .margin(null)
            .build();
        ResponseValidator.verifyInvalidParam(VillanyAteszCommissionActions.getCreateOrUpdateCommissionResponse(getServerPort(), accessTokenId, request), "margin", "must not be null");
    }

    private void create_nullDeposit(UUID accessTokenId) {
        CommissionModel request = validRequest(null, null)
            .deposit(null)
            .build();
        ResponseValidator.verifyInvalidParam(VillanyAteszCommissionActions.getCreateOrUpdateCommissionResponse(getServerPort(), accessTokenId, request), "deposit", "must not be null");
    }

    private void create_nullExtraCost(UUID accessTokenId) {
        CommissionModel request = validRequest(null, null)
            .extraCost(null)
            .build();
        ResponseValidator.verifyInvalidParam(VillanyAteszCommissionActions.getCreateOrUpdateCommissionResponse(getServerPort(), accessTokenId, request), "extraCost", "must not be null");
    }

    private void create_nullHourlyWage(UUID accessTokenId) {
        CommissionModel request = validRequest(null, null)
            .hourlyWage(null)
            .build();
        ResponseValidator.verifyInvalidParam(VillanyAteszCommissionActions.getCreateOrUpdateCommissionResponse(getServerPort(), accessTokenId, request), "hourlyWage", "must not be null");
    }


    private void create_nullDepartureFee(UUID accessTokenId) {
        CommissionModel request = validRequest(null, null)
            .departureFee(null)
            .build();
        ResponseValidator.verifyInvalidParam(VillanyAteszCommissionActions.getCreateOrUpdateCommissionResponse(getServerPort(), accessTokenId, request), "departureFee", "must not be null");
    }

    private void create_nullHoursPerDay(UUID accessTokenId) {
        CommissionModel request = validRequest(null, null)
            .hoursPerDay(null)
            .build();
        ResponseValidator.verifyInvalidParam(VillanyAteszCommissionActions.getCreateOrUpdateCommissionResponse(getServerPort(), accessTokenId, request), "hoursPerDay", "must not be null");
    }

    private void create_nullDaysOfWork(UUID accessTokenId) {
        CommissionModel request = validRequest(null, null)
            .daysOfWork(null)
            .build();
        ResponseValidator.verifyInvalidParam(VillanyAteszCommissionActions.getCreateOrUpdateCommissionResponse(getServerPort(), accessTokenId, request), "daysOfWork", "must not be null");
    }

    @Test(groups = {"villany-atesz", "be"})
    public void commissionWithCart() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_VILLANY_ATESZ);

        UUID cartId1 = setUpCart(accessTokenId, CONTACT_NAME_1, STOCK_ITEM_NAME_1);
        UUID cartId2 = setUpCart(accessTokenId, CONTACT_NAME_2, STOCK_ITEM_NAME_2);
        UUID commissionId = createWithCart(accessTokenId, cartId1);
        getCommissions(accessTokenId, commissionId, CONTACT_NAME_1);
        updateWithCart(accessTokenId, commissionId, cartId2);
    }

    private void getCommissions(UUID accessTokenId, UUID commissionId, String contactName) {
        CustomAssertions.singleListAssertThat(VillanyAteszCommissionActions.getCommissions(getServerPort(), accessTokenId))
            .returns(commissionId, CommissionView::getCommissionId)
            .returns(contactName, CommissionView::getContactName);
    }

    private void updateWithCart(UUID accessTokenId, UUID commissionId, UUID cartId) {
        CommissionModel request = validRequest(commissionId, cartId)
            .build();

        CommissionModel response = VillanyAteszCommissionActions.createOrUpdateCommission(getServerPort(), accessTokenId, request);

        assertThat(response.getCartId()).isEqualTo(cartId);
    }

    private UUID createWithCart(UUID accessTokenId, UUID cartId) {
        CommissionModel request = validRequest(null, cartId)
            .build();

        CommissionModel response = VillanyAteszCommissionActions.createOrUpdateCommission(getServerPort(), accessTokenId, request);

        assertThat(response.getCartId()).isEqualTo(cartId);

        return response.getCommissionId();
    }

    private UUID setUpCart(UUID accessTokenId, String contactName, String stockItemName) {
        UUID contactId = createContact(accessTokenId, contactName);
        UUID stockItemId = createStockItem(accessTokenId, stockItemName);
        UUID cartId = VillanyAteszCartActions.createCart(getServerPort(), accessTokenId, contactId);
        AddToCartRequest addToCartRequest = AddToCartRequest.builder()
            .cartId(cartId)
            .stockItemId(stockItemId)
            .amount(AMOUNT)
            .build();
        VillanyAteszCartActions.addToCart(getServerPort(), accessTokenId, addToCartRequest);

        return cartId;
    }

    private UUID createContact(UUID accessTokenId, String contactName) {
        ContactModel request = ContactModel.builder()
            .name(contactName)
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

    private UUID createStockItem(UUID accessTokenId, String stockItemName) {
        StockCategoryModel stockCategoryModel = StockCategoryModel.builder()
            .name(CATEGORY_NAME)
            .measurement("")
            .build();

        UUID stockCategoryId = VillanyAteszStockCategoryActions.createStockCategory(getServerPort(), accessTokenId, stockCategoryModel)
            .stream()
            .findFirst()
            .orElseThrow()
            .getStockCategoryId();

        CreateStockItemRequest request = CreateStockItemRequest.builder()
            .stockCategoryId(stockCategoryId)
            .name(stockItemName)
            .serialNumber("")
            .barCode("")
            .inCar(AMOUNT)
            .inStorage(0)
            .price(PRICE)
            .build();

        VillanyAteszStockItemActions.createStockItem(getServerPort(), accessTokenId, request);

        return VillanyAteszStockItemActions.getStockItems(getServerPort(), accessTokenId)
            .stream()
            .findFirst()
            .orElseThrow()
            .getStockItemId();
    }

    private CommissionModel.CommissionModelBuilder validRequest(UUID commissionId, UUID cartId) {
        return CommissionModel.builder()
            .commissionId(commissionId)
            .cartId(cartId)
            .daysOfWork(DAYS_OF_WORK)
            .hoursPerDay(HOURS_PER_DAY)
            .departureFee(DEPARTURE_FEE)
            .hourlyWage(HOURLY_WAGE)
            .extraCost(EXTRA_COST)
            .deposit(DEPOSIT)
            .margin(MARGIN);
    }
}
