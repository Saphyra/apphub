package com.github.saphyra.apphub.integration.backend.villany_atesz;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.villany_atesz.VillanyAteszAcquisitionActions;
import com.github.saphyra.apphub.integration.action.backend.villany_atesz.VillanyAteszCartActions;
import com.github.saphyra.apphub.integration.action.backend.villany_atesz.VillanyAteszCommissionActions;
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
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.CommissionModel;
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
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_VILLANY_ATESZ);

        DatabaseUtil.removeRoleByEmail(userData.getEmail(), role);
        SleepUtil.sleep(3000);

        //Index
        CommonUtils.verifyMissingRole(() -> VillanyAteszIndexActions.getTotalStockValueResponse(getServerPort(), accessTokenId));
        CommonUtils.verifyMissingRole(() -> VillanyAteszIndexActions.getTotalToolboxValueResponse(getServerPort(), accessTokenId));
        CommonUtils.verifyMissingRole(() -> VillanyAteszIndexActions.getStockItemsMarkedForAcquisitionResponse(getServerPort(), accessTokenId));

        //Contact
        CommonUtils.verifyMissingRole(() -> VillanyAteszContactActions.getCreateContactResponse(getServerPort(), accessTokenId, new ContactModel()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszContactActions.getEditContactResponse(getServerPort(), accessTokenId, UUID.randomUUID(), new ContactModel()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszContactActions.getDeleteContactResponse(getServerPort(), accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszContactActions.getContactsResponse(getServerPort(), accessTokenId));

        //StockCategory
        CommonUtils.verifyMissingRole(() -> VillanyAteszStockCategoryActions.getCreateResponse(getServerPort(), accessTokenId, new StockCategoryModel()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszStockCategoryActions.getEditResponse(getServerPort(), accessTokenId, UUID.randomUUID(), new StockCategoryModel()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszStockCategoryActions.getDeleteResponse(getServerPort(), accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszStockCategoryActions.getStockCategoriesResponse(getServerPort(), accessTokenId));

        //StockItem
        CommonUtils.verifyMissingRole(() -> VillanyAteszStockItemActions.getCreateResponse(getServerPort(), accessTokenId, new CreateStockItemRequest()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszStockItemActions.getStockItemsResponse(getServerPort(), accessTokenId));
        CommonUtils.verifyMissingRole(() -> VillanyAteszStockItemActions.getStockItemsForCategoryResponse(getServerPort(), accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszStockItemActions.getAcquireResponse(getServerPort(), accessTokenId, LocalDate.now()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszStockItemActions.getFindByBarcodeResponse(getServerPort(), accessTokenId, ""));
        CommonUtils.verifyMissingRole(() -> VillanyAteszStockItemActions.getFindBarCodeByStockItemIdResponse(getServerPort(), accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszStockItemActions.getStockItemResponse(getServerPort(), accessTokenId, UUID.randomUUID()));

        //StockInventory
        CommonUtils.verifyMissingRole(() -> VillanyAteszStockItemInventoryActions.getItemsResponse(getServerPort(), accessTokenId));
        CommonUtils.verifyMissingRole(() -> VillanyAteszStockItemActions.getDeleteResponse(getServerPort(), accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszStockItemInventoryActions.getEditCategoryResponse(getServerPort(), accessTokenId, UUID.randomUUID(), UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszStockItemInventoryActions.getEditInventoriedResponse(getServerPort(), accessTokenId, UUID.randomUUID(), false));
        CommonUtils.verifyMissingRole(() -> VillanyAteszStockItemInventoryActions.getEditMarkedForAcquisitionResponse(getServerPort(), accessTokenId, UUID.randomUUID(), false));
        CommonUtils.verifyMissingRole(() -> VillanyAteszStockItemInventoryActions.getEditNameResponse(getServerPort(), accessTokenId, UUID.randomUUID(), ""));
        CommonUtils.verifyMissingRole(() -> VillanyAteszStockItemInventoryActions.getEditSerialNumberResponse(getServerPort(), accessTokenId, UUID.randomUUID(), ""));
        CommonUtils.verifyMissingRole(() -> VillanyAteszStockItemInventoryActions.getEditBarCodeResponse(getServerPort(), accessTokenId, UUID.randomUUID(), ""));
        CommonUtils.verifyMissingRole(() -> VillanyAteszStockItemInventoryActions.getEditInCarResponse(getServerPort(), accessTokenId, UUID.randomUUID(), 0));
        CommonUtils.verifyMissingRole(() -> VillanyAteszStockItemInventoryActions.getEditInStorageResponse(getServerPort(), accessTokenId, UUID.randomUUID(), 0));
        CommonUtils.verifyMissingRole(() -> VillanyAteszStockItemInventoryActions.getMoveStockToCarResponse(getServerPort(), accessTokenId, UUID.randomUUID(), 0));
        CommonUtils.verifyMissingRole(() -> VillanyAteszStockItemInventoryActions.getMoveStockToStorageResponse(getServerPort(), accessTokenId, UUID.randomUUID(), 0));
        CommonUtils.verifyMissingRole(() -> VillanyAteszStockItemInventoryActions.getResetInventoriedResponse(getServerPort(), accessTokenId));

        //Cart
        CommonUtils.verifyMissingRole(() -> VillanyAteszCartActions.getCreateResponse(getServerPort(), accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszCartActions.getCartsResponse(getServerPort(), accessTokenId));
        CommonUtils.verifyMissingRole(() -> VillanyAteszCartActions.getCartResponse(getServerPort(), accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszCartActions.getAddToCartResponse(getServerPort(), accessTokenId, new AddToCartRequest()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszCartActions.getRemoveFromCartResponse(getServerPort(), accessTokenId, UUID.randomUUID(), UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszCartActions.getFinalizeCartResponse(getServerPort(), accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszCartActions.getDeleteResponse(getServerPort(), accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszCartActions.getEditMarginResponse(getServerPort(), accessTokenId, UUID.randomUUID(), 1d));

        //Acquisition
        CommonUtils.verifyMissingRole(() -> VillanyAteszAcquisitionActions.getDatesResponse(getServerPort(), accessTokenId));
        CommonUtils.verifyMissingRole(() -> VillanyAteszAcquisitionActions.getAcquisitionsOnDayResponse(getServerPort(), accessTokenId, LocalDate.now()));

        //Toolbox
        CommonUtils.verifyMissingRole(() -> VillanyAteszToolboxActions.getSetToolStatusResponse(getServerPort(), accessTokenId, UUID.randomUUID(), null));
        CommonUtils.verifyMissingRole(() -> VillanyAteszToolboxActions.getToolsResponse(getServerPort(), accessTokenId));
        CommonUtils.verifyMissingRole(() -> VillanyAteszToolboxActions.getCreateToolResponse(getServerPort(), accessTokenId, new CreateToolRequest()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszToolboxActions.getDeleteResponse(getServerPort(), accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszToolboxActions.getToolTypesResponse(getServerPort(), accessTokenId));
        CommonUtils.verifyMissingRole(() -> VillanyAteszToolboxActions.getStorageBoxesResponse(getServerPort(), accessTokenId));
        CommonUtils.verifyMissingRole(() -> VillanyAteszToolboxActions.getEditToolTypeResponse(getServerPort(), accessTokenId, UUID.randomUUID(), ""));
        CommonUtils.verifyMissingRole(() -> VillanyAteszToolboxActions.getEditStorageBoxResponse(getServerPort(), accessTokenId, UUID.randomUUID(), ""));
        CommonUtils.verifyMissingRole(() -> VillanyAteszToolboxActions.getDeleteToolTypeResponse(getServerPort(), accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszToolboxActions.getDeleteStorageBoxResponse(getServerPort(), accessTokenId, UUID.randomUUID()));

        //Toolbox inventory
        CommonUtils.verifyMissingRole(() -> VillanyAteszToolboxInventoryActions.getEditStorageBoxResponse(getServerPort(), accessTokenId, UUID.randomUUID(), new StorageBoxModel()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszToolboxInventoryActions.getEditToolTypeResponse(getServerPort(), accessTokenId, UUID.randomUUID(), new ToolTypeModel()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszToolboxInventoryActions.getEditBrandResponse(getServerPort(), accessTokenId, UUID.randomUUID(), null));
        CommonUtils.verifyMissingRole(() -> VillanyAteszToolboxInventoryActions.getEditNameResponse(getServerPort(), accessTokenId, UUID.randomUUID(), null));
        CommonUtils.verifyMissingRole(() -> VillanyAteszToolboxInventoryActions.getEditCostResponse(getServerPort(), accessTokenId, UUID.randomUUID(), null));
        CommonUtils.verifyMissingRole(() -> VillanyAteszToolboxInventoryActions.getEditAcquiredAtResponse(getServerPort(), accessTokenId, UUID.randomUUID(), null));
        CommonUtils.verifyMissingRole(() -> VillanyAteszToolboxInventoryActions.getEditWarrantyExpiresAtResponse(getServerPort(), accessTokenId, UUID.randomUUID(), null));
        CommonUtils.verifyMissingRole(() -> VillanyAteszToolboxInventoryActions.getEditStatusResponse(getServerPort(), accessTokenId, UUID.randomUUID(), null));
        CommonUtils.verifyMissingRole(() -> VillanyAteszToolboxInventoryActions.getEditScrappedAtResponse(getServerPort(), accessTokenId, UUID.randomUUID(), null));
        CommonUtils.verifyMissingRole(() -> VillanyAteszToolboxInventoryActions.getEditInventoriedResponse(getServerPort(), accessTokenId, UUID.randomUUID(), null));
        CommonUtils.verifyMissingRole(() -> VillanyAteszToolboxInventoryActions.getResetInventoriedResponse(getServerPort(), accessTokenId));

        //Commission
        CommonUtils.verifyMissingRole(() -> VillanyAteszCommissionActions.getCreateOrUpdateCommissionResponse(getServerPort(), accessTokenId, new CommissionModel()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszCommissionActions.getCommissionResponse(getServerPort(), accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszCommissionActions.getCartResponse(getServerPort(), accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszCommissionActions.getDeleteResponse(getServerPort(), accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> VillanyAteszCommissionActions.getCommissionsResponse(getServerPort(), accessTokenId));
    }

    @DataProvider(parallel = true)
    public Object[][] roleProvider() {
        return new Object[][]{
            new Object[]{Constants.ROLE_VILLANY_ATESZ},
            new Object[]{Constants.ROLE_ACCESS}
        };
    }
}
