package com.github.saphyra.apphub.integration.framework;

import lombok.experimental.UtilityClass;

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

    public final String TEST_ROLE_NAME = "Teszt";
    public final String GENERAL_CHAT_ROOM_NAME = "general";
    public final String NEW_ALLIANCE_VALUE = "new-alliance";
    public final String NO_ALLIANCE_VALUE = "no-alliance";

    //SurfaceTypes
    public final String SURFACE_TYPE_DESERT = "DESERT";
    public final String SURFACE_TYPE_OIL_FIELD = "OIL_FIELD";
    public final String SURFACE_TYPE_LAKE = "LAKE";

    //DataIds
    public final String DATA_ID_SOLAR_PANEL = "solar_panel";
    public final String DATA_ID_BATTERY = "battery";
    public final String DATA_ID_WATER_PUMP = "water_pump";

    //QueueTypes
    public final String QUEUE_TYPE_CONSTRUCTION = "CONSTRUCTION";
    public final String QUEUE_TYPE_TERRAFORMATION = "TERRAFORMATION";

    //Etc
    public final int DEFAULT_PRIORITY = 5;

    //OpenedPageType
    public final String PAGE_TYPE_PLANET = "PLANET";
}
