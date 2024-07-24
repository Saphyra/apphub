package com.github.saphyra.apphub.ci.localization;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum LocalizedText implements LocalizationProvider {
    DEFAULT("Alapertelmezett", "Default"),
    DEPLOY_MODE_UPDATED("Futtatas modja frissitve.", "Deploy Mode updated."),
    EXIT("Kilepes", "Exit"),
    BUILD_THREAD_COUNT("Szalak szama build soran", "Build Thread Count"),
    INTEGRATION_TEST_THREAD_COUNT("Szalak szama integracios tesztek futasa soran (%s)", "Integration Test Thread Count (%s)"),
    DEPLOY_MODE("Futas modja (%s)", "Deploy Mode (%s)"),
    DEPLOY_MODE_SKIP_BUILD("Build kihagyasa", "Skip Build"),
    DEPLOY_MODE_SKIP_TESTS("Tesztek kihagyasa (%s)", "Skip Tests (%s)"),
    DEPLOY_MODE_DEFAULT("Alapertelmezett (%s)", "Default (%s)"),
    EDIT_CONFIGURATION("Konfiguracio szerkesztese", "Edit configuration"),
    START("Inditas", "Start"),
    STOP("Leallitas", "Stop"),
    RUN_TEST_GROUPS("Teszt csoportok futtatasa", "Run Test Groups"),
    RUN_TESTS("Tesztek futtatasa", "Run Tests"),
    LOCAL_RUN("Helyi futtatas", "Local run"),
    SETTINGS("Beallitasok", "Settings"),
    ENGLISH("Angol", "English"),
    HUNGARIAN("Magyar", "Hungarian"),
    CHANGE_LANGUAGE("Nyelv modositasa", "Change language"),
    SELECT_MODE("Mod kivalaszatsa", "Select mode"),
    MAIN_MENU("Fomenu", "Main Menu"),
    WHICH_CONFIGURATION_TO_EDIT("Melyik beallítast szeretned modosítani?", "Which configuration would you like to edit?"),
    SELECT_LANGUAGE("Nyelv kivalasztasa", "Select language"),
    THREAD_COUNT("Szalak stama", "Thread count"),
    THREAD_COUNT_FOR_RUNNING_WITH_TESTS("Szalak szama tesztek futtatasavel:", "Thread Count for running with tests:"),
    INVALID_PARAMETER("Ervenytelen parameter: %s", "Invalid parameter: %s"),
    MUST_NOT_BE_LOWER_THAN_1("Nem lehet 1-nel kisebb.", "Must not be lower than 1."),
    THREAD_COUNT_UPDATED("Szalak szama frissitve.", "Thread Count updated."),
    WHICH_TEST_GROUPS_TO_RUN("Melyik teszt csoportokat szeretned futtatni?", "Which groups you would like to run?"),
    ENTER_TEST_GROUP("Ird be a teszt csoport nevet.", "Enter the test group."),
    THREAD_COUNT_FOR_RUNNING_WITHOUT_TESTS("Szalak szama tesztek futtatasa nelkul:", "Thread Count for running without tests:"),
    THREAD_COUNT_FOR_RUNNING_INTEGRATION_TESTS("Szalak szama tesztek futtatasa integracios tesztek soran:", "Thread Count for running integration tests:"),
    LANGUAGE_CHANGED("Nyelv modositva.", "Language changed."),
    INVALID_COMMAND("Ervenytelen parancs.", "Invalid command."),
    WHAT_WOULD_YOU_LIKE_TO_DO("Mit szeretnel csinalni?", "What would you like to do?"),
    LOCAL_START_SERVICES("Komponensek indítasa", "Start services"),
    SERVICES_TO_START("Indítando komponensek:", "Services to start:"),
    SERVICE_NOT_FOUND("%s nevu komponens nem talalhato.", "Service with name %s not found."),
    MINIKUBE("Minikube", "Minikube"),
    MINIKUBE_START("Virtualis gep indítasa", "Start VM"),
    MINIKUBE_STOP("Virtualis gep leallítasa", "Stop VM"),
    MINIKUBE_DEPLOY("Virtualis gepre telepít", "Deploy to VM"),
    MINIKUBE_DEPLOY_SERVICES("Komponensek virtualis gepre telepítese", "Deploy services to VM"),
    DELETE_NAMESPACE("Kornyezet torlese", "Delete namespace"),
    CONFIRM_DELETE_NAMESPACE("Biztosan torlod %s kornyezetet?", "Are you sure you want to delete namespace %s?"),
    PRODUCTION("Prod szerver", "Production server"),
    PRODUCTION_RELEASE("Telepítes prod szerverre", "Production release"),
    DOCKER_HUB_USERNAME("DockerHub felhasznalonev:", "DockerHub username:"),
    DOCKER_HUB_PASSWORD("DockerHub jelszo:", "DockerHub password:"),
    MUST_NOT_BE_BLANK("Nem lehet ures!", "Must not be blank."),
    PRODUCTION_PROXY("ProdProxy indítasa", "Start Production Proxy"),
    SERVICE_STARTUP_COUNT_LIMIT("Szerver indítas limit (%s)", "Service startup count limit (%s)"),
    STARTUP_LIMIT_SAVED("Limit elmentve.", "Limit saved."),
    ENABLE_SERVICES("Servicek ki / bekapcsolasa ", "Enable / Disable services"),
    ENABLED("Engedelyezve ", "Enabled"),
    DISABLED("Letiltva ", "Disabled"),
    TOGGLE_SERVICES("Valaszd ki, melyik servicet szeretned letiltani / engedelyezni: ", "Select service to toggle status:"),
    BUILD_THREAD_COUNT_DEFAULT("Szalak szama build soran (%s)", "Build Thread Count (%s)"),
    THREAD_COUNT_FOR_PRODUCTION_RELEASE("Szalak szama prod deployment soran:", "Thread count during production deployment:"),
    PRE_CREATE_DRIVERS("WebDriverek létrehozása integrációs tesztek futtatása előtt (%s)", "Pre-create WebDrivers before running integration tests (%s)"),
    PRE_CREATE_DRIVERS_COUNT("Hány WebDrivert szeretnél létrehozni az integrációs tesztek futtatása előtt?", "How many WebDrivers would you like to create before running integration tests?"),
    ;

    private final String hu;
    private final String en;

    public String getLocalizedText(Language language) {
        return switch (language) {
            case ENGLISH -> en;
            case HUNGARIAN -> hu;
            default -> throw new IllegalArgumentException("Unsupported language: " + language);
        };
    }
}
