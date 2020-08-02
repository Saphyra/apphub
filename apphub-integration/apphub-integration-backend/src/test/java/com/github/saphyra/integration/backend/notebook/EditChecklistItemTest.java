package com.github.saphyra.integration.backend.notebook;

import com.github.saphyra.apphub.integration.common.TestBase;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

//TODO implement TCas after Query checklistItem endpoint is done
public class EditChecklistItemTest extends TestBase {
    @DataProvider(name = "localeDataProvider", parallel = true)
    public Object[] localeDataProvider() {
        return Language.values();
    }

    @Test(dataProvider = "localeDataProvider")
    public void nullContent(Language language) {

    }

    @Test(dataProvider = "localeDataProvider")
    public void nullChecked(Language language) {

    }

    @Test(dataProvider = "localeDataProvider")
    public void nullOrder(Language language) {

    }

    @Test(dataProvider = "localeDataProvider")
    public void listItemNotFound(Language language) {

    }

    @Test(dataProvider = "localeDataProvider")
    public void checklistItemNotFound(Language language) {

    }

    @Test(dataProvider = "localeDataProvider")
    public void checklistItemDeleted(Language language) {

    }

    @Test(dataProvider = "localeDataProvider")
    public void checklistItemAdded(Language language) {

    }

    @Test(dataProvider = "localeDataProvider")
    public void checklistItemModified(Language language) {

    }
}
