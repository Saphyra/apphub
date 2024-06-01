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
    WHICH_CONFIGURATION_TO_EDIT("Melyik beállítást szeretned módosítani?", "Which configuration would you like to edit?"),
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
    LOCAL_START_SERVICES("Komponensek indítása", "Start services"),
    SERVICES_TO_START("Indítandó komponensek:", "Services to start:"),
    SERVICE_NOT_FOUND("%s nevű komponens nem található.", "Service with name %s not found."),
    MINIKUBE("Minikube", "Minikube"),
    MINIKUBE_START("Virtuális gép indítása", "Start VM"),
    MINIKUBE_STOP("Virtuális gép leállítása", "Stop VM"),
    MINIKUBE_DEPLOY("Virtuális gépre telepít", "Deploy to VM"),
    MINIKUBE_DEPLOY_SERVICES("Komponensek virtuális gépre telepítése", "Deploy services to VM"),
    DELETE_NAMESPACE("Környezet törlése", "Delete namespace"),
    CONFIRM_DELETE_NAMESPACE("Biztosan törlöd %s környezetet?", "Are you sure you want to delete namespace %s?"),
    PRODUCTION("Prod szerver", "Production server"),
    PRODUCTION_RELEASE("Telepítés prod szerverre", "Production release"),
    DOCKER_HUB_USERNAME("DockerHub felhasználónév:", "DockerHub username:"),
    DOCKER_HUB_PASSWORD("DockerHub jelszó:", "DockerHub password:"),
    MUST_NOT_BE_BLANK("Nem lehet üres!", "Must not be blank."),
    PRODUCTION_PROXY("ProdProxy indítása", "Start Production Proxy"),
    SERVICE_STARTUP_COUNT_LIMIT("Szerver indítás limit (%s)", "Service startup count limit (%s)"),
    STARTUP_LIMIT_SAVED("Limit elmentve.", "Limit saved."),
    ENABLE_SERVICES("Servicek ki / bekapcsolása ", "Enable / Disable services"),
    ENABLED("Engedélyezve ", "Enabled"),
    DISABLED("Letiltva ", "Disabled"),
    TOGGLE_SERVICES("Válaszd ki, melyik servicet szeretnéd letiltani / engedélyezni: ", "Select service to toggle status:"),
    BUILD_THREAD_COUNT_DEFAULT("Szalak szama build soran (%s)", "Build Thread Count (%s)"),
    THREAD_COUNT_FOR_PRODUCTION_RELEASE("Szalak szama prod deployment soran:", "Thread count during production deployment:"),
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
