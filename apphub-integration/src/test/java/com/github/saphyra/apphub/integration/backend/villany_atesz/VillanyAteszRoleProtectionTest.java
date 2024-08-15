package com.github.saphyra.apphub.integration.backend.villany_atesz;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.villany_atesz.VillanyAteszAcquisitionActions;
import com.github.saphyra.apphub.integration.action.backend.villany_atesz.VillanyAteszCartActions;
import com.github.saphyra.apphub.integration.action.backend.villany_atesz.VillanyAteszContactActions;
import com.github.saphyra.apphub.integration.action.backend.villany_atesz.VillanyAteszIndexActions;
import com.github.saphyra.apphub.integration.action.backend.villany_atesz.VillanyAteszStockCategoryActions;
import com.github.saphyra.apphub.integration.action.backend.villany_atesz.VillanyAteszStockItemActions;
import com.github.saphyra.apphub.integration.action.backend.villany_atesz.VillanyAteszStockItemInventoryActions;
import com.github.saphyra.apphub.integration.action.backend.villany_atesz.VillanyAteszToolboxActions;
import com.github.saphyra.apphub.integration.action.backend.villany_atesz.VillanyAteszToolboxInventoryActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.AddToCartRequest;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.ContactModel;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.CreateStockItemRequest;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.CreateToolRequest;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.StockCategoryModel;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.StorageBoxModel;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.ToolTypeModel;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.UUID;

public class VillanyAteszRoleProtectionTest extends BackEndTest {
    @Test(dataProvider = "roleProvider", groups = {"be", "villany-atesz"})
    public void villanyAteszRoleProtection(String role) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData);
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_VILLANY_ATESZ);

        DatabaseUtil.removeRoleByEmail(userData.getEmail(), role);
        SleepUtil.sleep(3000);

        //Index
        CommonUtils.verifyMissingRole(() -> VillanyAteszIndexActions.getTotalStockValueResponse(accessTokenId));
        CommonUtils.verifyMissingRole(() -> VillanyAteszIndexActions.getTotalToolboxValueResponse(accessTokenId));
        CommonUtils.verifyMissingRole(() -> VillanyAteszIndexActions.getStockItemsMarkedForAcquisitionResponse(accessTokenId));

        //Contact
        CommonUtils.verifyMissingRole(() -> VillanyAteszContactActions.getCreateContactResponse(accessTokenId, new ContactModel()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszContactActions.getEditContactResponse(accessTokenId, UUID.randomUUID(), new ContactModel()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszContactActions.getDeleteContactResponse(accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszContactActions.getContactsResponse(accessTokenId));

        //StockCategory
        CommonUtils.verifyMissingRole(() -> VillanyAteszStockCategoryActions.getCreateResponse(accessTokenId, new StockCategoryModel()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszStockCategoryActions.getEditResponse(accessTokenId, UUID.randomUUID(), new StockCategoryModel()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszStockCategoryActions.getDeleteResponse(accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszStockCategoryActions.getStockCategoriesResponse(accessTokenId));

        //StockItem
        CommonUtils.verifyMissingRole(() -> VillanyAteszStockItemActions.getCreateResponse(accessTokenId, new CreateStockItemRequest()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszStockItemActions.getStockItemsResponse(accessTokenId));
        CommonUtils.verifyMissingRole(() -> VillanyAteszStockItemActions.getStockItemsForCategoryResponse(accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszStockItemActions.getAcquireResponse(accessTokenId, LocalDate.now()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszStockItemActions.getFindByBarcodeResponse(accessTokenId, ""));
        CommonUtils.verifyMissingRole(() -> VillanyAteszStockItemActions.getFindBarCodeByStockItemIdResponse(accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszStockItemActions.getStockItemResponse(accessTokenId, UUID.randomUUID()));

        //StockInventory
        CommonUtils.verifyMissingRole(() -> VillanyAteszStockItemInventoryActions.getItemsResponse(accessTokenId));
        CommonUtils.verifyMissingRole(() -> VillanyAteszStockItemActions.getDeleteResponse(accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszStockItemInventoryActions.getEditCategoryResponse(accessTokenId, UUID.randomUUID(), UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszStockItemInventoryActions.getEditInventoriedResponse(accessTokenId, UUID.randomUUID(), false));
        CommonUtils.verifyMissingRole(() -> VillanyAteszStockItemInventoryActions.getEditMarkedForAcquisitionResponse(accessTokenId, UUID.randomUUID(), false));
        CommonUtils.verifyMissingRole(() -> VillanyAteszStockItemInventoryActions.getEditNameResponse(accessTokenId, UUID.randomUUID(), ""));
        CommonUtils.verifyMissingRole(() -> VillanyAteszStockItemInventoryActions.getEditSerialNumberResponse(accessTokenId, UUID.randomUUID(), ""));
        CommonUtils.verifyMissingRole(() -> VillanyAteszStockItemInventoryActions.getEditBarCodeResponse(accessTokenId, UUID.randomUUID(), ""));
        CommonUtils.verifyMissingRole(() -> VillanyAteszStockItemInventoryActions.getEditInCarResponse(accessTokenId, UUID.randomUUID(), 0));
        CommonUtils.verifyMissingRole(() -> VillanyAteszStockItemInventoryActions.getEditInStorageResponse(accessTokenId, UUID.randomUUID(), 0));
        CommonUtils.verifyMissingRole(() -> VillanyAteszStockItemInventoryActions.getMoveStockToCarResponse(accessTokenId, UUID.randomUUID(), 0));
        CommonUtils.verifyMissingRole(() -> VillanyAteszStockItemInventoryActions.getMoveStockToStorageResponse(accessTokenId, UUID.randomUUID(), 0));
        CommonUtils.verifyMissingRole(() -> VillanyAteszStockItemInventoryActions.getResetInventoriedResponse(accessTokenId));

        //Cart
        CommonUtils.verifyMissingRole(() -> VillanyAteszCartActions.getCreateResponse(accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszCartActions.getCartsResponse(accessTokenId));
        CommonUtils.verifyMissingRole(() -> VillanyAteszCartActions.getCartResponse(accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszCartActions.getAddToCartResponse(accessTokenId, new AddToCartRequest()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszCartActions.getRemoveFromCartResponse(accessTokenId, UUID.randomUUID(), UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszCartActions.getFinalizeCartResponse(accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszCartActions.getDeleteResponse(accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszCartActions.getEditMarginResponse(accessTokenId, UUID.randomUUID(), 1d));

        //Acquisition
        CommonUtils.verifyMissingRole(() -> VillanyAteszAcquisitionActions.getDatesResponse(accessTokenId));
        CommonUtils.verifyMissingRole(() -> VillanyAteszAcquisitionActions.getAcquisitionsOnDayResponse(accessTokenId, LocalDate.now()));

        //Toolbox
        CommonUtils.verifyMissingRole(() -> VillanyAteszToolboxActions.getSetToolStatusResponse(accessTokenId, UUID.randomUUID(), null));
        CommonUtils.verifyMissingRole(() -> VillanyAteszToolboxActions.getToolsResponse(accessTokenId));
        CommonUtils.verifyMissingRole(() -> VillanyAteszToolboxActions.getCreateToolResponse(accessTokenId, new CreateToolRequest()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszToolboxActions.getDeleteResponse(accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszToolboxActions.getToolTypesResponse(accessTokenId));
        CommonUtils.verifyMissingRole(() -> VillanyAteszToolboxActions.getStorageBoxesResponse(accessTokenId));

        //Toolbox inventory
        CommonUtils.verifyMissingRole(() -> VillanyAteszToolboxInventoryActions.getEditStorageBoxResponse(accessTokenId, UUID.randomUUID(), new StorageBoxModel()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszToolboxInventoryActions.getEditToolTypeResponse(accessTokenId, UUID.randomUUID(), new ToolTypeModel()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszToolboxInventoryActions.getEditBrandResponse(accessTokenId, UUID.randomUUID(), null));
        CommonUtils.verifyMissingRole(() -> VillanyAteszToolboxInventoryActions.getEditNameResponse(accessTokenId, UUID.randomUUID(), null));
        CommonUtils.verifyMissingRole(() -> VillanyAteszToolboxInventoryActions.getEditCostResponse(accessTokenId, UUID.randomUUID(), null));
        CommonUtils.verifyMissingRole(() -> VillanyAteszToolboxInventoryActions.getEditAcquiredAtResponse(accessTokenId, UUID.randomUUID(), null));
        CommonUtils.verifyMissingRole(() -> VillanyAteszToolboxInventoryActions.getEditWarrantyExpiresAtResponse(accessTokenId, UUID.randomUUID(), null));
        CommonUtils.verifyMissingRole(() -> VillanyAteszToolboxInventoryActions.getEditStatusResponse(accessTokenId, UUID.randomUUID(), null));
        CommonUtils.verifyMissingRole(() -> VillanyAteszToolboxInventoryActions.getEditScrappedAtResponse(accessTokenId, UUID.randomUUID(), null));
        CommonUtils.verifyMissingRole(() -> VillanyAteszToolboxInventoryActions.getEditInventoriedResponse(accessTokenId, UUID.randomUUID(), null));
        CommonUtils.verifyMissingRole(() -> VillanyAteszToolboxInventoryActions.getResetInventoriedResponse(accessTokenId));
    }

    @DataProvider(parallel = true)
    public Object[][] roleProvider() {
        return new Object[][]{
            new Object[]{Constants.ROLE_VILLANY_ATESZ},
            new Object[]{Constants.ROLE_ACCESS}
        };
    }
}
