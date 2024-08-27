package com.github.saphyra.apphub.integration.frontend.notebook;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class NotebookRoleProtectionTest extends SeleniumTest {
    @Test(dataProvider = "roleDataProvider", groups = {"fe", "notebook"})
    public void notebookRoleProtection(String role) {
        WebDriver driver = extractDriver();

        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        DatabaseUtil.removeRoleByEmail(userData.getEmail(), role);
        SleepUtil.sleep(3000);

        CommonUtils.verifyMissingRole(getServerPort(), driver, Endpoints.NOTEBOOK_PAGE);
        CommonUtils.verifyMissingRole(getServerPort(), driver, Endpoints.NOTEBOOK_PAGE + "/new/:parent");
        CommonUtils.verifyMissingRole(getServerPort(), driver, Endpoints.NOTEBOOK_PAGE + "/new/category/:parent");
        CommonUtils.verifyMissingRole(getServerPort(), driver, Endpoints.NOTEBOOK_PAGE + "/new/text/:parent");
        CommonUtils.verifyMissingRole(getServerPort(), driver, Endpoints.NOTEBOOK_PAGE + "/new/link/:parent");
        CommonUtils.verifyMissingRole(getServerPort(), driver, Endpoints.NOTEBOOK_PAGE + "/new/only-title/:parent");
        CommonUtils.verifyMissingRole(getServerPort(), driver, Endpoints.NOTEBOOK_PAGE + "/new/checklist/:parent");
        CommonUtils.verifyMissingRole(getServerPort(), driver, Endpoints.NOTEBOOK_PAGE + "/new/table/:parent");
        CommonUtils.verifyMissingRole(getServerPort(), driver, Endpoints.NOTEBOOK_PAGE + "/new/custom-table/:parent");
        CommonUtils.verifyMissingRole(getServerPort(), driver, Endpoints.NOTEBOOK_PAGE + "/new/checklist-table/:parent");
        CommonUtils.verifyMissingRole(getServerPort(), driver, Endpoints.NOTEBOOK_PAGE + "/new/image/:parent");
        CommonUtils.verifyMissingRole(getServerPort(), driver, Endpoints.NOTEBOOK_PAGE + "/new/file/:parent");
        CommonUtils.verifyMissingRole(getServerPort(), driver, Endpoints.NOTEBOOK_PAGE + "/edit/:listItemId");
    }

    @DataProvider(parallel = true)
    public Object[][] roleDataProvider() {
        return new Object[][]{
            new Object[]{Constants.ROLE_NOTEBOOK},
            new Object[]{Constants.ROLE_ACCESS}
        };
    }
}
