package com.github.saphyra.apphub.integration.frontend.notebook.checklist;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookUtils;
import com.github.saphyra.apphub.integration.action.frontend.notebook.view.ViewChecklistActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.view.notebook.ChecklistItem;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderChecklistTest extends SeleniumTest {
    private static final String TITLE = "title";

    @Test(groups = {"fe", "notebook"})
    public void checklistItemsOrderedCaseInsensitive() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.NOTEBOOK);

        List<BiWrapper<String, Boolean>> items = List.of(
                new BiWrapper<>("b", false),
                new BiWrapper<>("Aa", false),
                new BiWrapper<>("ab", false),
                new BiWrapper<>("c", false)
        );
        NotebookUtils.newChecklist(getServerPort(), driver, TITLE, items);

        NotebookActions.findListItemByTitleValidated(driver, TITLE)
            .open(driver);

        AwaitilityWrapper.awaitAssert(() -> assertThat(ViewChecklistActions.getItems(driver)).hasSize(items.size()));
        ViewChecklistActions.orderItems(driver);

        List<String> sorted = new ArrayList<>(items.stream().map(BiWrapper::getEntity1).toList());
        sorted.sort(String.CASE_INSENSITIVE_ORDER);

        AwaitilityWrapper.awaitAssert(() -> assertThat(ViewChecklistActions.getItems(driver).stream().map(ChecklistItem::getValue)).containsExactly(sorted.toArray(new String[0])));

    }
}
