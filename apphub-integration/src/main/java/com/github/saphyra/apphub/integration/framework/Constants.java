package com.github.saphyra.apphub.integration.framework;

import com.google.common.collect.ImmutableList;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class Constants {
    //Cookies
    public final String ACCESS_TOKEN_COOKIE = "access-token";
    public final String LOCALE_COOKIE = "language";

    //Roles
    public final String ROLE_ACCESS = "ACCESS";
    public final String ROLE_ADMIN = "ADMIN";
    public final String ROLE_NOTEBOOK = "NOTEBOOK";
    public final String ROLE_SKYXPLORE = "SKYXPLORE";
    public final String ROLE_TEST = "TEST";
    public final String ROLE_TRAINING = "TRAINING";
    public final String ROLE_UTILS = "UTILS";
    public final String ROLE_COMMUNITY = "COMMUNITY";
    public final String ROLE_CALENDAR = "CALENDAR";

    //Alliances
    public final String GENERAL_CHAT_ROOM_NAME = "general";
    public final String ALLIANCE_CHAT_ROOM_NAME = "alliance";
    public static final String NEW_ALLIANCE_LABEL = "New alliance";
    public static final String NEW_ALLIANCE_VALUE = "new-alliance";
    public final String NO_ALLIANCE_LABEL = "No alliance";
    public static final String NO_ALLIANCE_VALUE = "no-alliance";

    //SurfaceTypes
    public final String SURFACE_TYPE_DESERT = "DESERT";
    public final String SURFACE_TYPE_OIL_FIELD = "OIL_FIELD";
    public final String SURFACE_TYPE_LAKE = "LAKE";
    public final String SURFACE_TYPE_FOREST = "FOREST";
    public final String SURFACE_TYPE_CONCRETE = "CONCRETE";
    public static final String SURFACE_TYPE_MILITARY = "MILITARY";

    //DataIds
    public final String DATA_ID_SOLAR_PANEL = "solar_panel";
    public final String DATA_ID_BATTERY = "battery";
    public final String DATA_ID_WATER_PUMP = "water_pump";
    public final String DATA_ID_CAMP = "camp";
    public final String DATA_ID_ORE = "ore";
    public static final String DATA_ID_DEPOT = "depot";
    public static final String DATA_ID_HEADQUARTERS = "headquarters";

    //QueueTypes
    public final String QUEUE_TYPE_CONSTRUCTION = "CONSTRUCTION";
    public final String QUEUE_TYPE_DECONSTRUCTION = "DECONSTRUCTION";
    public final String QUEUE_TYPE_TERRAFORMATION = "TERRAFORMATION";

    //Etc
    public final int DEFAULT_PRIORITY = 5;
    public static final String DEFAULT_GAME_NAME = "game-name";
    public static final int MAX_CITIZEN_MORALE = 10000;
    public static final int MAX_CITIZEN_SATIETY = 10000;
    public static final String PASSED = "PASSED";
    public static final String FAILED = "FAILED";
    public static final String DEFAULT_PIN_GROUP_NAME = "All";

    //OpenedPageType
    public final String PAGE_TYPE_PLANET = "PLANET";

    //ModuleIds
    public final String MODULE_ID_TRAINING = "training";

    //Calendar
    public final String CALENDAR_OCCURRENCE_STATUS_PENDING = "PENDING";
    public final String CALENDAR_OCCURRENCE_STATUS_EXPIRED = "EXPIRED";
    public final String CALENDAR_OCCURRENCE_STATUS_VIRTUAL = "VIRTUAL";
    public final String CALENDAR_OCCURRENCE_STATUS_DONE = "DONE";
    public final String CALENDAR_OCCURRENCE_STATUS_SNOOZED = "SNOOZED";

    //User settings
    public final String USER_SETTING_CATEGORY_NOTEBOOK = "notebook";

    public final String USER_SETTING_KEY_SHOW_ARCHIVED = "show-archived";

    public static final String SERVICE_NAME_USER = "user";
    public static final List<String> SERVICES = ImmutableList.of(
        "event-gateway",
        "admin-panel",
        "main-gateway",
        "modules",
        "notebook",
        "scheduler",
        "skyxplore-data",
        "skyxplore-game",
        "skyxplore-lobby",
        "training",
        SERVICE_NAME_USER,
        "utils",
        "web-content",
        "community",
        "calendar",
        "encryption",
        "storage",
        "villany-atesz"
    );

    public static final String CITIZEN_PROPERTY_MORALE = "MORALE";
}
