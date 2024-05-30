package com.github.saphyra.apphub.integration.backend.villany_atesz;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.villany_atesz.VillanyAteszIndexActions;
import com.github.saphyra.apphub.integration.action.backend.villany_atesz.VillanyAteszStockCategoryActions;
import com.github.saphyra.apphub.integration.action.backend.villany_atesz.VillanyAteszStockItemActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.CreateStockItemRequest;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.StockCategoryModel;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class IndexTest extends BackEndTest {
    private static final String CATEGORY_NAME = "category-name";
    private static final String STOCK_ITEM_NAME_1 = "stock-item-name-1";
    private static final String STOCK_ITEM_NAME_2 = "stock-item-name-2";
    private static final Integer IN_CAR_1 = 23;
    private static final Integer IN_CAR_2 = 2332;
    private static final Integer IN_STORAGE_1 = 45;
    private static final Integer IN_STORAGE_2 = 455;
    private static final Integer PRICE_1 = 35;
    private static final Integer PRICE_2 = 355;

    @Test(groups = {"be", "villany-atesz"})
    public void totalValue() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData);
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_VILLANY_ATESZ);

        StockCategoryModel category = StockCategoryModel.builder()
            .name(CATEGORY_NAME)
            .measurement("")
            .build();
        UUID stockCategoryId = VillanyAteszStockCategoryActions.create(accessTokenId, category)
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
        VillanyAteszStockItemActions.create(accessTokenId, stockItemRequest1);

        CreateStockItemRequest stockItemRequest2 = CreateStockItemRequest.builder()
            .stockCategoryId(stockCategoryId)
            .name(STOCK_ITEM_NAME_2)
            .serialNumber("")
            .barCode("")
            .inCar(IN_CAR_2)
            .inStorage(IN_STORAGE_2)
            .price(PRICE_2)
            .build();
        VillanyAteszStockItemActions.create(accessTokenId, stockItemRequest2);

        assertThat(VillanyAteszIndexActions.getTotalValue(accessTokenId)).isEqualTo((IN_CAR_1 + IN_STORAGE_1) * PRICE_1 + (IN_CAR_2 + IN_STORAGE_2) * PRICE_2);
    }
}
