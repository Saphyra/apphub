package com.github.saphyra.apphub.integration.frontend;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.villany_atesz.VillanyAteszContactsPageActions;
import com.github.saphyra.apphub.integration.action.frontend.villany_atesz.VillanyAteszNavigation;
import com.github.saphyra.apphub.integration.action.frontend.villany_atesz.VillanyAteszStockCategoriesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.villany_atesz.VillanyAteszStockNewItemPageActions;
import com.github.saphyra.apphub.integration.action.frontend.villany_atesz.VillanyAteszStockOverviewPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.framework.concurrent.FutureWrapper;
import com.github.saphyra.apphub.integration.localization.LocalizedText;
import com.github.saphyra.apphub.integration.structure.api.LoginParameters;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.view.villany_atesz.Contact;
import com.github.saphyra.apphub.integration.structure.view.villany_atesz.StockCategory;
import com.github.saphyra.apphub.integration.structure.view.villany_atesz.StockItemOverview;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class ImportTest extends SeleniumTest {
    private static final String EMAIL = "<FILL IT>";
    private static final String PASSWORD = "<FILL IT>";
    public static final int DRIVER_COUNT = 9;

    @Test(groups = {"fe", "villany-atesz"})
    public void importData() throws IOException {
        List<Table> tables = getTables();
        List<WebDriver> drivers = extractDrivers(DRIVER_COUNT);
        List<DataHolder<List<ContactData>>> contactDataHolders = createContactDataHolders(drivers, tables);
        List<CategoryData> categories = getCategories(tables);
        List<DataHolder<List<CategoryData>>> categoryDataHolders = getCategoryDataHolders(drivers, categories);
        List<DataHolder<List<ItemData>>> itemDataHolders = createItemDataHolders(drivers, tables, categories);

        login(drivers);
        createContacts(contactDataHolders);
        createCategories(categoryDataHolders);
        createItems(itemDataHolders);
    }

    private List<DataHolder<List<ItemData>>> createItemDataHolders(List<WebDriver> drivers, List<Table> tables, List<CategoryData> categories) {
        Map<String, String> categoryMapping = categories.stream()
            .collect(Collectors.toMap(CategoryData::getId, CategoryData::getName));
        Map<String, Map<String, String>> stockMapping = tables.stream()
            .filter(table -> table.getName().equals("rk_keszlet"))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Table rk_keszlet not found"))
            .getData()
            .stream()
            .collect(Collectors.toMap(map -> map.get("alkatresz_id"), Function.identity()));
        Map<String, Integer> priceMapping = tables.stream()
            .filter(table -> table.getName().equals("rk_ar"))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Table rk_ar not found"))
            .getData()
            .stream()
            .collect(Collectors.groupingBy(map -> map.get("alkatresz_id")))
            .entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream().map(map -> map.get("egysegar")).mapToInt(Integer::parseInt).max().orElse(0)));

        List<ItemData> contactDataList = tables.stream()
            .filter(table -> table.getName().equals("rk_alkatresz"))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Table rk_alkatresz not found"))
            .getData()
            .stream()
            .map(row -> ItemData.builder()
                .categoryName(categoryMapping.get(row.get("alkatresz_fajta_id")))
                .name(row.get("megnevezes"))
                .serialNumber(row.get("cikkszam"))
                .inCar(Integer.parseInt(stockMapping.get(row.get("id")).get("autoban")))
                .inStorage(Integer.parseInt(stockMapping.get(row.get("id")).get("raktaron")))
                .price(priceMapping.get(row.get("id")))
                .build()
            )
            .toList();
        List<List<ItemData>> split = splitList(contactDataList);
        assertThat(split.size()).isLessThanOrEqualTo(DRIVER_COUNT);

        List<DataHolder<List<ItemData>>> result = new ArrayList<>();
        for (int i = 0; i < split.size(); i++) {
            result.add(new DataHolder<>(drivers.get(i), split.get(i)));
        }
        return result;
    }

    private List<DataHolder<List<CategoryData>>> getCategoryDataHolders(List<WebDriver> drivers, List<CategoryData> categories) {
        List<List<CategoryData>> split = splitList(categories);
        assertThat(split.size()).isLessThanOrEqualTo(DRIVER_COUNT);

        List<DataHolder<List<CategoryData>>> result = new ArrayList<>();
        for (int i = 0; i < split.size(); i++) {
            result.add(new DataHolder<>(drivers.get(i), split.get(i)));
        }
        return result;
    }

    private List<CategoryData> getCategories(List<Table> tables) {
        return tables.stream()
            .filter(table -> table.getName().equals("rk_alkatresz_fajta"))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Table rk_alkatresz_fajta not found"))
            .getData()
            .stream()
            .map(row -> CategoryData.builder()
                .id(row.get("id"))
                .name(row.get("megnevezes"))
                .measurement(row.get("mertekegyseg"))
                .build()
            )
            .toList();
    }

    private List<DataHolder<List<ContactData>>> createContactDataHolders(List<WebDriver> drivers, List<Table> tables) {
        List<ContactData> contactDataList = tables.stream()
            .filter(table -> table.getName().equals("kapcsolatok"))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Table kapcsolatok not found"))
            .getData()
            .stream()
            .map(row -> ContactData.builder()
                .code(row.get("sorszam"))
                .name(row.get("nev"))
                .phone(row.get("telefonszam"))
                .address(row.get("cim"))
                .note(row.get("megjegyzes"))
                .build()
            )
            .toList();
        List<List<ContactData>> split = splitList(contactDataList);
        assertThat(split.size()).isLessThanOrEqualTo(DRIVER_COUNT);

        List<DataHolder<List<ContactData>>> result = new ArrayList<>();
        for (int i = 0; i < split.size(); i++) {
            result.add(new DataHolder<>(drivers.get(i), split.get(i)));
        }
        return result;
    }

    private <T> List<List<T>> splitList(List<T> contactDataList) {
        List<List<T>> result = new ArrayList<>();

        for (int i = 0; i < DRIVER_COUNT; i++) {
            result.add(new ArrayList<>());
        }

        int listIndex = 0;
        for (T t : contactDataList) {
            result.get(listIndex).add(t);
            listIndex++;
            if (listIndex == DRIVER_COUNT) {
                listIndex = 0;
            }
        }

        return result;
    }

    private static List<Table> getTables() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = ImportTest.class.getResourceAsStream("/villany_atesz.json");
        return Arrays.asList(mapper.readValue(is, Table[].class));
    }

    private static void login(List<WebDriver> drivers) {
        List<FutureWrapper<Void>> futures = drivers.stream()
            .map(driver -> new DataHolder<>(driver, LoginParameters.builder().email(EMAIL).password(PASSWORD).build()))
            .map(LoginProcessor::new)
            .map(EXECUTOR_SERVICE::execute)
            .toList();

        futures.forEach(voidFutureWrapper -> voidFutureWrapper.get().getOrThrow());
    }

    private void createContacts(List<DataHolder<List<ContactData>>> contactDataHolders) {
        List<FutureWrapper<Void>> futures = contactDataHolders.stream()
            .map(ContactProcessor::new)
            .map(EXECUTOR_SERVICE::execute)
            .toList();

        futures.forEach(voidFutureWrapper -> voidFutureWrapper.get().getOrThrow());
    }

    private void createCategories(List<DataHolder<List<CategoryData>>> categoryDataHolders) {
        List<FutureWrapper<Void>> futures = categoryDataHolders.stream()
            .map(CategoryProcessor::new)
            .map(EXECUTOR_SERVICE::execute)
            .toList();

        futures.forEach(voidFutureWrapper -> voidFutureWrapper.get().getOrThrow());
    }

    private void createItems(List<DataHolder<List<ItemData>>> itemDataHolders) {
        List<FutureWrapper<Void>> futures = itemDataHolders.stream()
            .map(ItemProcessor::new)
            .map(EXECUTOR_SERVICE::execute)
            .toList();

        futures.forEach(voidFutureWrapper -> voidFutureWrapper.get().getOrThrow());
    }

    @RequiredArgsConstructor
    @Getter
    static class DataHolder<T> {
        private final WebDriver driver;
        private final T data;
    }

    @RequiredArgsConstructor
    static class ItemProcessor implements Runnable {
        private final DataHolder<List<ItemData>> dataHolder;

        @Override
        public void run() {
            WebDriver driver = dataHolder.getDriver();

            VillanyAteszNavigation.openStockNewItem(driver);

            dataHolder.getData()
                .forEach(itemData -> {
                    VillanyAteszStockNewItemPageActions.fillForm(driver, itemData.getCategoryName(), itemData.getName(), itemData.getSerialNumber(), "", itemData.getInCar(), itemData.getInStorage(), itemData.getPrice());
                    VillanyAteszStockNewItemPageActions.submit(driver);

                    ToastMessageUtil.verifySuccessToast(driver, LocalizedText.VILLANY_ATESZ_STOCK_NEW_ITEM_CREATED);
                    ToastMessageUtil.clearToasts(driver);
                });

            VillanyAteszNavigation.openStockOverview(driver);
            AwaitilityWrapper.createDefault()
                .until(() -> !VillanyAteszStockOverviewPageActions.getItems(driver).isEmpty())
                .assertTrue("Items are not loaded.");

            dataHolder.getData()
                .forEach(itemData -> {
                    assertThat(VillanyAteszStockOverviewPageActions.getItems(driver).stream().filter(stockItemOverview -> stockItemOverview.getName().equals(itemData.getName())).findFirst().orElseThrow(() -> new RuntimeException("StockItem not found with name " + itemData.getName())))
                        .returns(itemData.getCategoryName(), StockItemOverview::getCategoryName)
                        .returns(itemData.getName(), StockItemOverview::getName)
                        .returns(itemData.getSerialNumber(), StockItemOverview::getSerialNumber)
                        .returns(itemData.getInCar().toString(), stockItemOverview -> stockItemOverview.getInCar().split(" ")[0])
                        .returns(itemData.getInStorage().toString(), stockItemOverview -> stockItemOverview.getInStorage().split(" ")[0])
                        .returns(itemData.getPrice() + Constants.FT_SUFFIX, StockItemOverview::getPrice);
                });
        }
    }

    @RequiredArgsConstructor
    static class CategoryProcessor implements Runnable {
        private final DataHolder<List<CategoryData>> dataHolder;

        @Override
        public void run() {
            WebDriver driver = dataHolder.getDriver();

            VillanyAteszNavigation.openStockCategories(driver);

            dataHolder.getData()
                .forEach(categoryData -> {
                    VillanyAteszStockCategoriesPageActions.fillCreateForm(driver, categoryData.getName(), categoryData.getMeasurement());
                    VillanyAteszStockCategoriesPageActions.submitCreateForm(driver);

                    AwaitilityWrapper.assertWithWaitList(() -> VillanyAteszStockCategoriesPageActions.getStockCategories(driver), categories -> !categories.isEmpty(), categories -> categories.stream().filter(contact -> contact.getName().equals(categoryData.getName())).findFirst().orElseThrow())
                        .returns(categoryData.getName().trim(), StockCategory::getName)
                        .returns(categoryData.getMeasurement().trim(), StockCategory::getMeasurement);
                });
        }
    }

    @RequiredArgsConstructor
    static class ContactProcessor implements Runnable {
        private final DataHolder<List<ContactData>> dataHolder;

        @Override
        public void run() {
            WebDriver driver = dataHolder.getDriver();

            VillanyAteszNavigation.openContacts(driver);

            dataHolder.getData()
                .forEach(contactData -> {
                    VillanyAteszContactsPageActions.fillContactData(driver, contactData.getCode(), contactData.getName(), contactData.getPhone(), contactData.getAddress(), contactData.getNote());
                    VillanyAteszContactsPageActions.submitSaveContact(driver);

                    AwaitilityWrapper.assertWithWaitList(() -> VillanyAteszContactsPageActions.getContacts(driver), contacts -> !contacts.isEmpty(), contacts -> contacts.stream().filter(contact -> contact.getName().equals(contactData.getName())).findFirst().orElseThrow())
                        .returns(contactData.getCode().trim(), Contact::getCode)
                        .returns(contactData.getPhone().trim(), Contact::getPhone)
                        .returns(contactData.getAddress().trim(), Contact::getAddress)
                        .returns(contactData.getNote().trim(), Contact::getNote);
                });
        }
    }

    @RequiredArgsConstructor
    static class LoginProcessor implements Runnable {
        private final DataHolder<LoginParameters> dataHolder;

        @Override
        public void run() {
            WebDriver driver = dataHolder.getDriver();


            Navigation.toIndexPage(driver);
            IndexPageActions.submitLogin(driver, dataHolder.getData());

            ModulesPageActions.openModule(driver, ModuleLocation.VILLANY_ATESZ);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @NoArgsConstructor
    @Data
    static class Table {
        private String name;
        private List<Map<String, String>> data;
    }

    @AllArgsConstructor
    @Builder
    @Data
    static class ContactData {
        @NonNull
        private String code;
        @NonNull
        private String name;
        @NonNull
        private String phone;
        @NonNull
        private String address;
        @NonNull
        private String note;
    }

    @AllArgsConstructor
    @Builder
    @Data
    static class CategoryData {
        @NonNull
        private String id;
        @NonNull
        private String name;
        @NonNull
        private String measurement;
    }

    @AllArgsConstructor
    @Builder
    @Data
    static class ItemData {
        @NonNull
        private String categoryName;
        @NonNull
        private String name;
        @NonNull
        private String serialNumber;
        @NonNull
        private Integer inCar;
        @NonNull
        private Integer inStorage;
        @NonNull
        private Integer price;
    }
}
