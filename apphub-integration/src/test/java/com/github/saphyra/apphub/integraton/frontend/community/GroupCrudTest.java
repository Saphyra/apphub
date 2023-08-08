package com.github.saphyra.apphub.integraton.frontend.community;

import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.action.frontend.community.CommunityActions;
import com.github.saphyra.apphub.integration.action.frontend.community.GroupActions;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.structure.api.community.GroupInvitationType;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class GroupCrudTest extends SeleniumTest {
    private static final String GROUP_NAME = "group-name";
    private static final String NEW_GROUP_NAME = "new-group-name";

    @Test(groups = "community")
    public void groupCrud() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.COMMUNITY);
        CommunityActions.openGroupsTab(driver);

        //Create group - name too long
        GroupActions.openCreateGroupWindow(driver);

        GroupActions.fillCreateGroupNameInput(driver, Stream.generate(() -> "a").limit(31).collect(Collectors.joining()));
        SleepUtil.sleep(2000);
        GroupActions.verifyCreateGroupNameError(driver, "Csoport neve túl hosszú (Maximum 30 karakter)");

        //Create group - name too short
        GroupActions.fillCreateGroupNameInput(driver, "as");
        SleepUtil.sleep(2000);
        GroupActions.verifyCreateGroupNameError(driver, "Csoport neve túl rövid (Minimum 3 karakter)");

        //Create group
        GroupActions.fillCreateGroupNameInput(driver, GROUP_NAME);
        SleepUtil.sleep(2000);
        GroupActions.verifyCreateGroupNameCorrect(driver);
        GroupActions.submitCreateGroupForm(driver);

        NotificationUtil.verifySuccessNotification(driver, "Csoport létrehozva");

        GroupActions.closeGroupDetails(driver);

        List<WebElement> groups = GroupActions.getGroups(driver);
        assertThat(groups).hasSize(1);
        assertThat(groups.get(0).getText()).isEqualTo(GROUP_NAME);

        //Rename group - name too short
        GroupActions.openGroup(driver, groups.get(0));

        GroupActions.setGroupName(driver, "as");

        NotificationUtil.verifyErrorNotification(driver, "Csoport neve túl rövid (Minimum 3 karakter)");

        assertThat(GroupActions.getGroupName(driver)).isEqualTo(GROUP_NAME);

        //Rename group - name too long
        GroupActions.setGroupName(driver, Stream.generate(() -> "a").limit(31).collect(Collectors.joining()));

        NotificationUtil.verifyErrorNotification(driver, "Csoport neve túl hosszú (Maximum 30 karakter)");

        assertThat(GroupActions.getGroupName(driver)).isEqualTo(GROUP_NAME);

        //Rename group
        GroupActions.setGroupName(driver, NEW_GROUP_NAME);

        NotificationUtil.verifySuccessNotification(driver, "Csoport átnevezve.");

        GroupActions.closeGroupDetails(driver);

        groups = GroupActions.getGroups(driver);
        assertThat(groups).hasSize(1);
        assertThat(groups.get(0).getText()).isEqualTo(NEW_GROUP_NAME);

        //Change invitationType
        GroupActions.openGroup(driver, groups.get(0));

        GroupActions.changeInvitationType(driver, GroupInvitationType.FRIENDS_OF_FRIENDS);

        NotificationUtil.verifySuccessNotification(driver, "Meghívás módja megváltoztatva.");

        //Disband group
        GroupActions.disbandGroup(driver);

        NotificationUtil.verifySuccessNotification(driver, "Csapat feloszlatva.");

        assertThat(GroupActions.getGroups(driver)).isEmpty();
    }
}
