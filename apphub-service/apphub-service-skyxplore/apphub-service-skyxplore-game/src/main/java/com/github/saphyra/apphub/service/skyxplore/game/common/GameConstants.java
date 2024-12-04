package com.github.saphyra.apphub.service.skyxplore.game.common;

import com.github.saphyra.apphub.lib.geometry.Coordinate;

public class GameConstants {
    //Chat
    public static final String CHAT_ROOM_GENERAL = "general";
    public static final String CHAT_ROOM_ALLIANCE = "alliance";
    public static final int MAXIMUM_CHAT_MESSAGE_LENGTH = 1024;

    public static final Coordinate ORIGO = new Coordinate(0, 0);
    public static final Integer DEFAULT_PRIORITY = 5;
    public static final Integer PROCESS_PRIORITY_MULTIPLIER = 100;

    //DataIds
    public static final String DATA_ID_RAW_FOOD = "raw_food";
    public static final String DATA_ID_HEADQUARTERS = "headquarters";

    //Message Sender item keys
    public static final String ITEM_KEY_SURFACES = "surfaces";
    public static final String ITEM_KEY_STORAGE = "storage";
    public static final String ITEM_KEY_QUEUE = "queue";
    public static final String ITEM_KEY_BUILDINGS = "buildings";
    public static final String ITEM_KEY_POPULATION = "population";

    public static final String CITIZEN_ASSIGNMENT_TYPE_IDLE = "IDLE";
    public static final String ITEM_KEY_BUILDING_MODULES = "buildingModules";
}
