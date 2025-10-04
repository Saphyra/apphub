package com.github.saphyra.apphub.integration.backend.villany_atesz;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.villany_atesz.VillanyAteszIndexActions;
import com.github.saphyra.apphub.integration.action.backend.villany_atesz.VillanyAteszStockCategoryActions;
import com.github.saphyra.apphub.integration.action.backend.villany_atesz.VillanyAteszStockItemActions;
import com.github.saphyra.apphub.integration.action.backend.villany_atesz.VillanyAteszStockItemInventoryActions;
import com.github.saphyra.apphub.integration.action.backend.villany_atesz.VillanyAteszToolboxActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.CustomAssertions;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.CreateStockItemRequest;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.CreateToolRequest;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.StockCategoryModel;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.StockItemResponse;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.StorageBoxModel;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.ToolTypeModel;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class VillanyAteszIndexTest extends BackEndTest {
    private static final String CATEGORY_NAME = "category-name";
    private static final String STOCK_ITEM_NAME_1 = "stock-item-name-1";
    private static final String STOCK_ITEM_NAME_2 = "stock-item-name-2";
    private static final Integer IN_CAR_1 = 23;
    private static final Integer IN_CAR_2 = 2332;
    private static final Integer IN_STORAGE_1 = 45;
    private static final Integer IN_STORAGE_2 = 455;
    private static final Integer PRICE_1 = 35;
    private static final Integer PRICE_2 = 355;
    private static final String TOOL_NAME_1 = "tool-name-1";
    private static final String TOOL_NAME_2 = "tool-name-2";

    @Test(groups = {"be", "villany-atesz"})
    public void totalStockValue() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_VILLANY_ATESZ);

        StockCategoryModel category = StockCategoryModel.builder()
            .name(CATEGORY_NAME)
            .measurement("")
            .build();
        UUID stockCategoryId = VillanyAteszStockCategoryActions.createStockCategory(getServerPort(), accessTokenId, category)
            .get(0)
            .getStockCategoryId();

        CreateStockItemRequest stockItemRequest1 = CreateStockItemRequest.builder()
            .stockCategoryId(stockCategoryId)
            .name(STOCK_ITEM_NAME_1)
            .serialNumber("")
            .barCode("")
            .inCar(IN_CAR_1)
            .inStorage(IN_STORAGE_1)
            .price(PRICE_1)
            .build();
        VillanyAteszStockItemActions.createStockItem(getServerPort(), accessTokenId, stockItemRequest1);

        CreateStockItemRequest stockItemRequest2 = CreateStockItemRequest.builder()
            .stockCategoryId(stockCategoryId)
            .name(STOCK_ITEM_NAME_2)
            .serialNumber("")
            .barCode("")
            .inCar(IN_CAR_2)
            .inStorage(IN_STORAGE_2)
            .price(PRICE_2)
            .build();
        VillanyAteszStockItemActions.createStockItem(getServerPort(), accessTokenId, stockItemRequest2);

        assertThat(VillanyAteszIndexActions.getTotalStockValue(getServerPort(), accessTokenId)).isEqualTo((IN_CAR_1 + IN_STORAGE_1) * PRICE_1 + (IN_CAR_2 + IN_STORAGE_2) * PRICE_2);
    }

    @Test(groups = {"be", "villany-atesz"})
    public void totalToolboxValue() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_VILLANY_ATESZ);

        CreateToolRequest createToolRequest1 = CreateToolRequest.builder()
            .brand("")
            .name(TOOL_NAME_1)
            .cost(PRICE_1)
            .acquiredAt(LocalDate.now())
            .storageBox(StorageBoxModel.builder().name("").build())
            .toolType(ToolTypeModel.builder().name("").build())
            .build();
        CreateToolRequest createToolRequest2 = CreateToolRequest.builder()
            .brand("")
            .name(TOOL_NAME_2)
            .cost(PRICE_2)
            .acquiredAt(LocalDate.now())
            .storageBox(StorageBoxModel.builder().name("").build())
            .toolType(ToolTypeModel.builder().name("").build())
            .build();

        VillanyAteszToolboxActions.createTool(getServerPort(), accessTokenId, createToolRequest1);
        VillanyAteszToolboxActions.createTool(getServerPort(), accessTokenId, createToolRequest2);

        assertThat(VillanyAteszIndexActions.getTotalToolboxValue(getServerPort(), accessTokenId)).isEqualTo(PRICE_1 + PRICE_2);
    }

    @Test(groups = {"be", "villany-atesz"})
    public void getStockItemsMarkedForAcquisition() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_VILLANY_ATESZ);

        StockCategoryModel category = StockCategoryModel.builder()
            .name(CATEGORY_NAME)
            .measurement("")
            .build();
        UUID stockCategoryId = VillanyAteszStockCategoryActions.createStockCategory(getServerPort(), accessTokenId, category)
            .get(0)
            .getStockCategoryId();

        CreateStockItemRequest stockItemRequest = CreateStockItemRequest.builder()
            .stockCategoryId(stockCategoryId)
            .name(STOCK_ITEM_NAME_1)
            .serialNumber("")
            .barCode("")
            .inCar(IN_CAR_1)
            .inStorage(IN_STORAGE_1)
            .price(PRICE_1)
            .build();
        VillanyAteszStockItemActions.createStockItem(getServerPort(), accessTokenId, stockItemRequest);

        UUID stockItemId = VillanyAteszStockItemActions.getStockItems(getServerPort(), accessTokenId)
            .get(0)
            .getStockItemId();

        VillanyAteszStockItemInventoryActions.getEditMarkedForAcquisitionResponse(getServerPort(), accessTokenId, stockItemId, true);

        CustomAssertions.singleListAssertThat(VillanyAteszIndexActions.getStockItemsMarkedForAcquisition(getServerPort(), accessTokenId))
            .returns(stockItemId, StockItemResponse::getStockItemId);
    }
}
