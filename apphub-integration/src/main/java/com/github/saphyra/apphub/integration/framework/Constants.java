package com.github.saphyra.apphub.integration.framework;

import com.google.common.collect.ImmutableList;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class Constants {
    public static final Object SERVICE_NAME_MAIN_GATEWAY = "main-gateway";
    public static final Object SERVICE_NAME_POSTGRES = "postgres";

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
    public final String ROLE_ELITE_BASE = "ELITE_BASE";
    public static final String ROLE_VILLANY_ATESZ = "VILLANY_ATESZ";
    public static final String ROLE_ELITE_BASE_ADMIN = "ELITE_BASE_ADMIN";

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

    //DataIds
    public static final String DATA_ID_HEADQUARTERS = "headquarters";
    public static final String DATA_ID_STEEL_INGOT = "steel_ingot";

    //QueueTypes
    public final String QUEUE_TYPE_TERRAFORMATION = "TERRAFORMATION";
    public static final String QUEUE_TYPE_CONSTRUCT_CONSTRUCTION_AREA = "CONSTRUCT_CONSTRUCTION_AREA";
    public static final String QUEUE_TYPE_DECONSTRUCT_CONSTRUCTION_AREA = "DECONSTRUCT_CONSTRUCTION_AREA";
    public static final String QUEUE_TYPE_CONSTRUCT_BUILDING_MODULE = "CONSTRUCT_BUILDING_MODULE";
    public static final String QUEUE_TYPE_DECONSTRUCT_BUILDING_MODULE = "DECONSTRUCT_BUILDING_MODULE";

    //Etc
    public final int DEFAULT_PRIORITY = 5;
    public static final String DEFAULT_GAME_NAME = "game-name";
    public static final int MAX_CITIZEN_MORALE = 10000;
    public static final int MAX_CITIZEN_SATIETY = 10000;
    public static final String PASSED = "PASSED";
    public static final String FAILED = "FAILED";
    public static final String DEFAULT_PIN_GROUP_NAME = "All";
    public static final String SELECT_OPTION_CHOOSE = "Choose";

    //VillanyAtesz
    public static final String FT_SUFFIX = " Ft";
    public static final Double CART_DEFAULT_MARGIN = 1.2;

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
        "web-content",
        "community",
        "calendar",
        "encryption",
        "storage",
        "villany-atesz",
        "elite-base"
    );

    public static final String CITIZEN_PROPERTY_MORALE = "MORALE";

    //SkyXplore skills
    public static final String SKILL_AIMING = "AIMING";
    public static final String SKILL_BUILDING = "BUILDING";
    public static final String CONSTRUCTION_AREA_DEPOT = "depot";
    public static final String CONSTRUCTION_AREA_EXTRACTOR = "extractor";

    //Building Modules
    public static final String BUILDING_MODULE_GARAGE = "garage";
    public static final String BUILDING_MODULE_SMALL_BATTERY = "small_battery";
    public static final String BUILDING_MODULE_HAMSTER_WHEEL = "hamster_wheel";
    public static final String BUILDING_MODULE_CATEGORY_BASIC_POWER_SUPPLY = "BASIC_POWER_SUPPLY";
    public static final String BUILDING_MODULE_CATEGORY_SMALL_STORAGE = "SMALL_STORAGE";
}
