package com.github.saphyra.apphub.integration.action.frontend.calendar;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.framework.concurrent.FutureWrapper;
import com.github.saphyra.apphub.integration.localization.LocalizedText;
import com.github.saphyra.apphub.integration.structure.api.calendar.RepetitionType;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

import static com.github.saphyra.apphub.integration.core.TestBase.EXECUTOR_SERVICE;

public class CalendarFlow {
    public static List<Context> init(int serverPort, List<WebDriver> drivers) {
        List<FutureWrapper<Context>> futures = drivers.stream()
            .map(driver -> EXECUTOR_SERVICE.asyncProcess(() -> init(serverPort, driver)))
            .toList();

        return futures.stream()
            .map(contextFutureWrapper -> contextFutureWrapper.get().getOrThrow())
            .toList();
    }

    public static Context init(int serverPort, WebDriver driver) {
        Navigation.toIndexPage(serverPort, driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);
        ModulesPageActions.openModule(serverPort, driver, ModuleLocation.CALENDAR);
        CommonUtils.enableTestMode(driver);
        return new Context(driver, userData);
    }

    public static TestContext initContext(int serverPort, List<WebDriver> drivers) {
        List<Context> contexts = init(serverPort, drivers);
        return new TestContext(contexts.get(0).driver(), contexts.get(1).driver(), contexts.get(1).userData());
    }

    public static CreateEventParameters createEvent(WebDriver ownerDriver, List<String> labels, Integer repeatForDays) {
        CalendarIndexPageActions.openCreateEventPage(ownerDriver);

        CreateEventParameters.CreateEventParametersBuilder builder = CreateEventParameters.valid(RepetitionType.ONE_TIME)
            .toBuilder();
        if (labels != null && !labels.isEmpty()) {
            builder.newLabels(labels);
        }
        if (repeatForDays != null) {
            builder.repeatForDays(repeatForDays);
        }

        CreateEventParameters parameters = builder.build();
        CalendarEventPageActions.fillForm(ownerDriver, parameters);
        CalendarEventPageActions.create(ownerDriver);
        ToastMessageUtil.verifySuccessToast(ownerDriver, LocalizedText.CALENDAR_EVENT_CREATED);
        return parameters;
    }

    public static void shareLabel(WebDriver ownerDriver, String sharedWithEmail, Consumer<WebDriver> grantSetup) {
        CalendarIndexPageActions.toLabelsPage(ownerDriver);
        AwaitilityWrapper.getListWithWait(() -> CalendarLabelsPageActions.getLabels(ownerDriver), labels -> !labels.isEmpty())
            .getFirst()
            .share(ownerDriver);

        CalendarSharePageActions.selectUser(ownerDriver, sharedWithEmail);
        grantSetup.accept(ownerDriver);
        CalendarSharePageActions.share(ownerDriver);
    }

    public static void shareEvent(WebDriver ownerDriver, LocalDate date, String sharedWithEmail, Consumer<WebDriver> grantSetup) {
        CalendarIndexPageActions.setReferenceDate(ownerDriver, date);
        AwaitilityWrapper.getSingleItemFromListWithWait(() -> CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, date))
            .open(ownerDriver);

        CalendarIndexPageActions.editEvent(ownerDriver);
        CalendarEventPageActions.share(ownerDriver);

        CalendarSharePageActions.selectUser(ownerDriver, sharedWithEmail);
        grantSetup.accept(ownerDriver);
        CalendarSharePageActions.share(ownerDriver);
    }

    public static void shareOccurrence(WebDriver ownerDriver, LocalDate date, String sharedWithEmail, Consumer<WebDriver> grantSetup) {
        CalendarIndexPageActions.setReferenceDate(ownerDriver, date);
        AwaitilityWrapper.getSingleItemFromListWithWait(() -> CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, date))
            .open(ownerDriver);

        CalendarIndexPageActions.editOpenedOccurrence(ownerDriver);
        CalendarOccurrencePageActions.share(ownerDriver);

        CalendarSharePageActions.selectUser(ownerDriver, sharedWithEmail);
        grantSetup.accept(ownerDriver);
        CalendarSharePageActions.share(ownerDriver);
    }

    public static void deleteOccurrence(WebDriver driver, LocalDate date) {
        CalendarIndexPageActions.setReferenceDate(driver, date);
        AwaitilityWrapper.getSingleItemFromListWithWait(() -> CalendarIndexPageActions.getOccurrencesOnDate(driver, date))
            .open(driver);

        CalendarIndexPageActions.deleteOpenedOccurrence(driver);
    }

    public static void openSharedLabelEvent(WebDriver sharedWithDriver, String labelName, String eventTitle) {
        CalendarIndexPageActions.toLabelsPage(sharedWithDriver);
        AwaitilityWrapper.getWithWait(() -> CalendarLabelsPageActions.getLabel(sharedWithDriver, labelName))
            .orElseThrow()
            .open();
        AwaitilityWrapper.getWithWait(() -> CalendarLabelsPageActions.getEvent(sharedWithDriver, eventTitle))
            .orElseThrow()
            .click();
    }

    public record Context(WebDriver driver, RegistrationParameters userData) {
    }

    public record TestContext(WebDriver ownerDriver, WebDriver sharedWithDriver, RegistrationParameters sharedWithUserData) {
    }
}
