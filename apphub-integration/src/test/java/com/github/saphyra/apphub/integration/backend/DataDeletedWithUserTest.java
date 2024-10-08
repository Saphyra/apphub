package com.github.saphyra.apphub.integration.backend;

import com.github.saphyra.apphub.integration.action.backend.AccountActions;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.ModulesActions;
import com.github.saphyra.apphub.integration.action.backend.UserSettingsActions;
import com.github.saphyra.apphub.integration.action.backend.admin_panel.BanActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.EventActions;
import com.github.saphyra.apphub.integration.action.backend.community.BlacklistActions;
import com.github.saphyra.apphub.integration.action.backend.community.FriendRequestActions;
import com.github.saphyra.apphub.integration.action.backend.community.GroupActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.ChecklistActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.PinActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.TableActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFriendActions;
import com.github.saphyra.apphub.integration.action.backend.villany_atesz.VillanyAteszCartActions;
import com.github.saphyra.apphub.integration.action.backend.villany_atesz.VillanyAteszContactActions;
import com.github.saphyra.apphub.integration.action.backend.villany_atesz.VillanyAteszStockCategoryActions;
import com.github.saphyra.apphub.integration.action.backend.villany_atesz.VillanyAteszStockItemActions;
import com.github.saphyra.apphub.integration.action.backend.villany_atesz.VillanyAteszToolboxActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.CollectionUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.structure.api.calendar.CreateEventRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.ReferenceDate;
import com.github.saphyra.apphub.integration.structure.api.calendar.RepetitionType;
import com.github.saphyra.apphub.integration.structure.api.notebook.ColumnType;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateTableRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.ItemType;
import com.github.saphyra.apphub.integration.structure.api.notebook.ListItemType;
import com.github.saphyra.apphub.integration.structure.api.notebook.checklist.ChecklistItemModel;
import com.github.saphyra.apphub.integration.structure.api.notebook.checklist.CreateChecklistRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.table.TableColumnModel;
import com.github.saphyra.apphub.integration.structure.api.notebook.table.TableHeadModel;
import com.github.saphyra.apphub.integration.structure.api.notebook.table.TableRowModel;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.user.BanRequest;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.api.user.SetUserSettingsRequest;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.AddToCartRequest;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.AddToStockRequest;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.ContactModel;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.CreateStockItemRequest;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.CreateToolRequest;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.StockCategoryModel;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.StorageBoxModel;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.ToolTypeModel;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

public class DataDeletedWithUserTest extends BackEndTest {
    private static final String REASON = "reason";
    private static final String TITLE = "title";
    private static final String GROUP_NAME = "group-name";
    private static final String CONTENT = "content";

    private static final Map<String, String> GENERIC_TABLES = CollectionUtils.toMap(
        new BiWrapper<>("apphub_user", "access_token"),
        new BiWrapper<>("apphub_user", "apphub_role"),
        new BiWrapper<>("apphub_user", "apphub_user"),
        new BiWrapper<>("apphub_user", "settings"),
        new BiWrapper<>("calendar", "event"),
        new BiWrapper<>("calendar", "occurrence"),
        new BiWrapper<>("modules", "favorite"),
        new BiWrapper<>("notebook", "checked_item"),
        new BiWrapper<>("notebook", "column_type"),
        new BiWrapper<>("notebook", "content"),
        new BiWrapper<>("notebook", "dimension"),
        new BiWrapper<>("notebook", "list_item"),
        new BiWrapper<>("notebook", "pin_group"),
        new BiWrapper<>("notebook", "pin_mapping"),
        new BiWrapper<>("notebook", "table_head"),
        new BiWrapper<>("villany_atesz", "acquisition"),
        new BiWrapper<>("villany_atesz", "cart"),
        new BiWrapper<>("villany_atesz", "cart_item"),
        new BiWrapper<>("villany_atesz", "contact"),
        new BiWrapper<>("villany_atesz", "stock_category"),
        new BiWrapper<>("villany_atesz", "stock_item"),
        new BiWrapper<>("villany_atesz", "stock_item_price"),
        new BiWrapper<>("villany_atesz", "storage_box"),
        new BiWrapper<>("villany_atesz", "tool"),
        new BiWrapper<>("villany_atesz", "tool_type")
    );

    private static final Map<String, String> COMMUNITY_TABLES = CollectionUtils.toMap(
        new BiWrapper<>("community", "blacklist"),
        new BiWrapper<>("community", "community_group"),
        new BiWrapper<>("community", "community_group_member"),
        new BiWrapper<>("community", "friend_request"),
        new BiWrapper<>("community", "friendship")
    );

    private static final Map<String, String> SKYXPLORE_TABLES = CollectionUtils.toMap(
        new BiWrapper<>("skyxplore", "character")
    );

    @Test(groups = {"be", "misc"})
    public void dataDeletedWithTheUser() {
        //apphub_user.access_token
        //apphub_user.apphub_role
        //apphub_user.apphub_user
        //skyxplore.character
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);
        UUID userId = DatabaseUtil.getUserIdByEmail(userData.getEmail());
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_VILLANY_ATESZ);

        RegistrationParameters adminUserData = RegistrationParameters.validParameters();
        UUID adminAccessTokenId = IndexPageActions.registerAndLogin(getServerPort(), adminUserData);
        DatabaseUtil.addRoleByEmail(adminUserData.getEmail(), Constants.ROLE_ADMIN);

        createRecords(accessTokenId, userId, adminUserData, adminAccessTokenId);

        verifyRecords(GENERIC_TABLES, userId, rowCount -> rowCount > 0);

        AccountActions.deleteAccount(getServerPort(), accessTokenId, userData.getPassword());

        AwaitilityWrapper.awaitAssert(() -> verifyRecords(GENERIC_TABLES, userId, integer -> integer == 0));
    }

    @Test(groups = {"be", "community"})
    public void communityDataDeletedWithTheUser() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);
        UUID userId = DatabaseUtil.getUserIdByEmail(userData.getEmail());

        RegistrationParameters adminUserData = RegistrationParameters.validParameters();
        IndexPageActions.registerAndLogin(getServerPort(), adminUserData);
        UUID adminUserId = DatabaseUtil.getUserIdByEmail(adminUserData.getEmail());
        DatabaseUtil.addRoleByEmail(adminUserData.getEmail(), Constants.ROLE_ADMIN);

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        IndexPageActions.registerAndLogin(getServerPort(), userData2);
        UUID userId2 = DatabaseUtil.getUserIdByEmail(userData2.getEmail());

        RegistrationParameters userData3 = RegistrationParameters.validParameters();
        UUID accessTokenId3 = IndexPageActions.registerAndLogin(getServerPort(), userData3);
        UUID userId3 = DatabaseUtil.getUserIdByEmail(userData3.getEmail());

        communityTables(accessTokenId, adminUserId, userId2, accessTokenId3, userId3);

        verifyRecords(COMMUNITY_TABLES, userId, rowCount -> rowCount > 0);

        AccountActions.deleteAccount(getServerPort(), accessTokenId, userData.getPassword());

        AwaitilityWrapper.awaitAssert(() -> verifyRecords(COMMUNITY_TABLES, userId, integer -> integer == 0));
    }

    @Test(groups = {"be", "skyxplore"})
    public void skyXploreDataDeletedWithTheUser() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);
        UUID userId = DatabaseUtil.getUserIdByEmail(userData.getEmail());
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessTokenId, SkyXploreCharacterModel.valid());

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        UUID accessTokenId2 = IndexPageActions.registerAndLogin(getServerPort(), userData2);
        UUID userId2 = DatabaseUtil.getUserIdByEmail(userData2.getEmail());
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessTokenId2, SkyXploreCharacterModel.valid());

        RegistrationParameters userData3 = RegistrationParameters.validParameters();
        UUID accessTokenId3 = IndexPageActions.registerAndLogin(getServerPort(), userData3);
        UUID userId3 = DatabaseUtil.getUserIdByEmail(userData3.getEmail());
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessTokenId3, SkyXploreCharacterModel.valid());

        skyXploreTables(accessTokenId, userId2, accessTokenId3, userId3);

        verifySkyXploreRecords(userId, rowCount -> rowCount > 0);

        AccountActions.deleteAccount(getServerPort(), accessTokenId, userData.getPassword());

        AwaitilityWrapper.awaitAssert(() -> verifySkyXploreRecords(userId, integer -> integer == 0));
    }

    private void verifyRecords(Map<String, String> tables, UUID userId, Predicate<Integer> validator) {
        tables.forEach((schema, tableName) -> assertThat(validator.test(DatabaseUtil.getRowCountByValue(userId, schema, tableName, "user_id"))).isTrue());
    }

    private void verifySkyXploreRecords(UUID userId, Predicate<Integer> validator) {
        SKYXPLORE_TABLES.forEach((schema, tableName) -> assertThat(validator.test(DatabaseUtil.getRowCountByValue(userId, schema, tableName, "user_id"))).isTrue());

        assertThat(validator.test(DatabaseUtil.getRowCountByValue(userId, "skyxplore", "friendship", "friend_2"))).isTrue();
        assertThat(validator.test(DatabaseUtil.getRowCountByValue(userId, "skyxplore", "friend_request", "sender_id"))).isTrue();
    }

    private void createRecords(UUID accessTokenId, UUID userId, RegistrationParameters adminUserData, UUID adminAccessTokenId) {
        apphubUserTables(accessTokenId, userId, adminUserData, adminAccessTokenId);
        calendarTables(accessTokenId);
        modulesTables(accessTokenId);
        notebookTables(accessTokenId);
        villanyAteszTables(accessTokenId);
    }

    private void villanyAteszTables(UUID accessTokenId) {
        //villany_atesz.contact
        ContactModel contactModel = ContactModel.builder()
            .name(TITLE)
            .code("")
            .phone("")
            .address("")
            .note("")
            .build();
        UUID contactId = VillanyAteszContactActions.createContact(getServerPort(), accessTokenId, contactModel)
            .get(0)
            .getContactId();

        //villany_atesz.stock_category
        StockCategoryModel stockCategoryModel = StockCategoryModel.builder()
            .name(TITLE)
            .measurement("")
            .build();
        UUID stockCategoryId = VillanyAteszStockCategoryActions.create(getServerPort(), accessTokenId, stockCategoryModel)
            .get(0)
            .getStockCategoryId();

        //villany_atesz.stock_item
        //villany_atesz.stock_item_price
        CreateStockItemRequest createStockItemRequest = CreateStockItemRequest.builder()
            .stockCategoryId(stockCategoryId)
            .name(TITLE)
            .price(20)
            .serialNumber("")
            .barCode("")
            .inCar(2)
            .inStorage(2)
            .build();

        VillanyAteszStockItemActions.create(getServerPort(), accessTokenId, createStockItemRequest);
        UUID stockItemId = VillanyAteszStockItemActions.getStockItems(getServerPort(), accessTokenId).get(0).getStockItemId();

        //villany_atesz.acquisition
        AddToStockRequest addToStockRequest = AddToStockRequest.builder()
            .stockItemId(stockItemId)
            .inCar(10)
            .inStorage(2)
            .price(2)
            .barCode("")
            .forceUpdatePrice(false)
            .build();

        VillanyAteszStockItemActions.acquire(getServerPort(), accessTokenId, LocalDate.now(), addToStockRequest);

        //villany_atesz.cart
        UUID cartId = VillanyAteszCartActions.create(getServerPort(), accessTokenId, contactId);

        //villany_atesz.cart_item
        AddToCartRequest addToCartRequest = AddToCartRequest.builder()
            .cartId(cartId)
            .stockItemId(stockItemId)
            .amount(1)
            .build();

        VillanyAteszCartActions.addToCart(getServerPort(), accessTokenId, addToCartRequest);

        //villany_atesz.storage_box
        //villany_atesz.tool_type
        //villany_atesz.tool
        CreateToolRequest createToolRequest = CreateToolRequest.builder()
            .storageBox(StorageBoxModel.builder().name(TITLE).build())
            .toolType(ToolTypeModel.builder().name(TITLE).build())
            .name(TITLE)
            .brand("")
            .cost(32)
            .acquiredAt(LocalDate.now())
            .build();

        VillanyAteszToolboxActions.createTool(getServerPort(), accessTokenId, createToolRequest);
    }

    private static void skyXploreTables(UUID accessTokenId, UUID userId2, UUID accessTokenId3, UUID userId3) {
        //skyxplore.friend_request
        SkyXploreFriendActions.createFriendRequest(getServerPort(), accessTokenId, userId2);

        //skyxplore.friendship
        SkyXploreFriendActions.setUpFriendship(getServerPort(), accessTokenId, accessTokenId3, userId3);
    }

    private static void notebookTables(UUID accessTokenId) {
        //notebook.checked_item
        //notebook.list_item
        //notebook.content
        CreateChecklistRequest createChecklistRequest = CreateChecklistRequest.builder()
            .title(TITLE)
            .items(List.of(
                ChecklistItemModel.builder()
                    .index(0)
                    .checked(true)
                    .content(CONTENT)
                    .type(ItemType.NEW)
                    .build()
            ))
            .build();
        UUID listItemId = ChecklistActions.createChecklist(getServerPort(), accessTokenId, createChecklistRequest);

        //notebook.column_type
        //notebook.dimension
        //notebook.table_head
        CreateTableRequest createTableRequest = CreateTableRequest.builder()
            .title(createChecklistRequest.getTitle())
            .listItemType(ListItemType.TABLE)
            .tableHeads(List.of(
                TableHeadModel.builder()
                    .columnIndex(0)
                    .content(CONTENT)
                    .type(ItemType.NEW)
                    .build()
            ))
            .rows(List.of(
                TableRowModel.builder()
                    .rowIndex(0)
                    .itemType(ItemType.NEW)
                    .columns(List.of(
                        TableColumnModel.builder()
                            .columnIndex(0)
                            .columnType(ColumnType.TEXT)
                            .itemType(ItemType.NEW)
                            .data(CONTENT)
                            .build()
                    ))
                    .build()
            ))
            .build();
        TableActions.createTable(getServerPort(), accessTokenId, createTableRequest);

        //notebook.pin_group
        UUID pinGroupId = PinActions.createPinGroup(getServerPort(), accessTokenId, TITLE)
            .get(0)
            .getPinGroupId();

        //notebook.pin_mapping
        PinActions.addItemToPinGroup(getServerPort(), accessTokenId, pinGroupId, listItemId);
    }

    private static void modulesTables(UUID accessTokenId) {
        //modules.favorite
        ModulesActions.setAsFavorite(getServerPort(), accessTokenId, "notebook", true);
    }

    private static void communityTables(UUID accessTokenId, UUID adminUserId, UUID userId2, UUID accessTokenId3, UUID userId3) {
        //community.blacklist
        BlacklistActions.createBlacklist(getServerPort(), accessTokenId, userId2);

        //community.community_group
        GroupActions.createGroup(getServerPort(), accessTokenId, GROUP_NAME);

        //community.friend_request
        FriendRequestActions.createFriendRequest(getServerPort(), accessTokenId, adminUserId);

        //community.friendship
        FriendRequestActions.createFriendRequest(getServerPort(), accessTokenId, userId3);
        FriendRequestActions.acceptFriendRequest(getServerPort(), accessTokenId3, FriendRequestActions.getReceivedFriendRequests(getServerPort(), accessTokenId3).get(0).getFriendRequestId());
    }

    private static void calendarTables(UUID accessTokenId) {
        //calendar.event
        //calendar.occurrence
        CreateEventRequest createEventRequest = CreateEventRequest.builder()
            .referenceDate(ReferenceDate.builder()
                .day(LocalDate.now())
                .month(LocalDate.now())
                .build())
            .date(LocalDate.now())
            .title(TITLE)
            .content("")
            .repetitionType(RepetitionType.ONE_TIME)
            .build();
        EventActions.createEvent(getServerPort(), accessTokenId, createEventRequest);
    }

    private static void apphubUserTables(UUID accessTokenId, UUID userId, RegistrationParameters adminUserData, UUID adminAccessTokenId) {
        //apphub_user.ban
        BanRequest banRequest = BanRequest.builder()
            .bannedUserId(userId)
            .bannedRole(Constants.ROLE_TRAINING)
            .permanent(true)
            .reason(REASON)
            .password(adminUserData.getPassword())
            .build();
        BanActions.ban(getServerPort(), adminAccessTokenId, banRequest);

        //apphub_user.settings
        SetUserSettingsRequest setUserSettingsRequest = SetUserSettingsRequest.builder()
            .category("notebook")
            .key("show-archived")
            .value("true")
            .build();
        UserSettingsActions.setUserSetting(getServerPort(), accessTokenId, setUserSettingsRequest);
    }
}
