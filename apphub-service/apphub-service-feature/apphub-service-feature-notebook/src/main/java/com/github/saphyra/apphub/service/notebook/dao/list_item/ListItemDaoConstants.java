package com.github.saphyra.apphub.service.notebook.dao.list_item;

public class ListItemDaoConstants {
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_SK = "sk";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_PINNED = "pinned";
    public static final String COLUMN_ARCHIVED = "archived";
    public static final String COLUMN_PARENT = "parent";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_DATA = "data";
    public static final String COLUMN_CHECKED = "checked";
    public static final String COLUMN_INDEX = "index";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_COLUMNS = "columns";

    public static final String PREFIX_USER = "USER#";
    public static final String PREFIX_LIST_ITEM = "LIST_ITEM#";
    public static final String PREFIX_CHECKLIST_ITEM = "CHECKLIST_ITEM#";
    public static final String PREFIX_CONTENT = "CONTENT#";
    public static final String PREFIX_BATCH = "BATCH#";
    public static final String PREFIX_TABLE_HEAD = "TABLE_HEAD#";
    public static final String PREFIX_TABLE_ROW = "TABLE_ROW#";

    public static final String GSI_USER_ID_PARENT = "user_id-parent";
    public static final String GSI_USER_ID_TYPE = "user_id-type";
}
