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
    STOP_SERVICES("Komponensek leallitasa", "Stop services"),
    RUN_TEST_GROUPS("Teszt csoportok futtatasa", "Run Test Groups"),
    RUN_LATEST_TEST_GROUPS("Legutobbi teszt csoportok futtatasa (%s)", "Run Latest Test Groups (%s)"),
    RUN_TESTS("Tesztek futtatasa", "Run Tests"),
    LOCAL_RUN("Helyi futtatas", "Local run"),
    SETTINGS("Beallitasok", "Settings"),
    ENGLISH("Angol", "English"),
    HUNGARIAN("Magyar", "Hungarian"),
    CHANGE_LANGUAGE("Nyelv modositasa", "Change language"),
    SELECT_MODE("Mod kivalaszatsa", "Select mode"),
    MAIN_MENU("Fomenu", "Main Menu"),
    WHICH_CONFIGURATION_TO_EDIT("Melyik beallitast szeretned modositani?", "Which configuration would you like to edit?"),
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
    LOCAL_START_SERVICES("Komponensek inditasa", "Start services"),
    LOCAL_START_LATEST_SERVICES("Legutobbi komponensek inditasa (%s)", "Start latest services (%s)"),
    LOCAL_STOP_LATEST_SERVICES("Legutobbi komponensek leallitasa (%s)", "Stop latest services (%s)"),
    REMOTE_DEPLOY_LATEST_SERVICES("Legutobbi komponensek telepitese (%s)", "Deploy latest services (%s)"),
    SERVICES_TO_START("Inditando komponensek:", "Services to start:"),
    SERVICES_TO_STOP("Leallitando komponensek:", "Services to stop:"),
    SERVICE_NOT_FOUND("%s nevu komponens nem talalhato.", "Service with name %s not found."),
    MINIKUBE("Minikube", "Minikube"),
    MINIKUBE_START("Virtualis gep inditasa", "Start VM"),
    MINIKUBE_STOP("Virtualis gep leallitasa", "Stop VM"),
    MINIKUBE_DEPLOY("Virtualis gepre telepit", "Deploy to VM"),
    MINIKUBE_DEPLOY_SERVICES("Komponensek virtualis gepre telepitese", "Deploy services to VM"),
    PREPROD_DEPLOY_SERVICES("Komponensek telepitese", "Deploy services"),
    DELETE_NAMESPACE("Kornyezet torlese", "Delete namespace"),
    CONFIRM_DELETE_NAMESPACE("Biztosan torlod %s kornyezetet?", "Are you sure you want to delete namespace %s?"),
    PRODUCTION("Prod szerver", "Production server"),
    PRODUCTION_RELEASE("Telepites prod szerverre", "Production release"),
    PREPROD_RELEASE("Telepítes preprod szerverre", "Preprod release"),
    MUST_NOT_BE_BLANK("Nem lehet ures!", "Must not be blank."),
    PRODUCTION_PROXY("ProdProxy inditasa", "Start Production Proxy"),
    PREPROD_PROXY("PreprodProxy inditasa", "Start Preprod Proxy"),
    SERVICE_STARTUP_COUNT_LIMIT("Szerver inditas limit (%s)", "Service startup count limit (%s)"),
    STARTUP_LIMIT_SAVED("Limit elmentve.", "Limit saved."),
    ENABLE_SERVICES("Servicek ki / bekapcsolasa ", "Enable / Disable services"),
    ENABLED("Engedelyezve ", "Enabled"),
    DISABLED("Letiltva ", "Disabled"),
    TOGGLE_SERVICES("Valaszd ki, melyik servicet szeretned letiltani / engedelyezni: ", "Select service to toggle status:"),
    BUILD_THREAD_COUNT_DEFAULT("Szalak szama build soran (%s)", "Build Thread Count (%s)"),
    THREAD_COUNT_FOR_PRODUCTION_RELEASE("Szalak szama prod deployment soran:", "Thread count during production deployment:"),
    PRE_CREATE_DRIVERS("WebDriverek létrehozása integrációs tesztek futtatása előtt (%s)", "Pre-create WebDrivers before running integration tests (%s)"),
    PRE_CREATE_DRIVERS_COUNT("Hány WebDrivert szeretnél létrehozni az integrációs tesztek futtatása előtt?", "How many WebDrivers would you like to create before running integration tests?"),
    SET_BASH_LOCATION("bash.exe helyének megadása", "Set bash.exe location"),
    BASH_FILE_LOCATION("bash.exe helye:", "Place of bash.exe:"),
    BROWSER_STARTUP_LIMIT_MENU_OPTION_LABEL("Bongeszo inditas limit (%s)", "Browser startup limit (%s)"),
    BROWSER_STARTUP_LIMIT("Hany bongeszo induljon egyszerre?", "How many browsers can be started at once?"),
    PREPROD("Preprod szerver", "Preprod server"),
    SCALE_DOWN("Leállítás", "Scale down"),
    PORT_FORWARD("Port tovabbitas", "Port forward"),
    GUI_ENABLED_MENU_OPTION_LABEL("Grafikus interfesz engedelyezve (%s)", "GUI enabled (%s)"),
    ENABLE_GUI("Grafikus interfesz engedelyezese?", "Enable GUI?"),
    PROVIDE_YES_OR_NO("Ervenytelen valasz. Igen/nem?", "Invalid answer. Yes/No?"),
    INTEGRATION_RETRY_COUNT("Integractios tesztek ujra probalasa (%s)", "Integration test retry count (%s)"),
    INTEGRATION_RETRY_COUNT_LABEL("Hanyszor probalja ujra a sikertelen teszteket?", "How many times should it retry failed tests?"),
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
