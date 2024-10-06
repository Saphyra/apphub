package com.github.saphyra.apphub.integration.framework.endpoints;

public class NotebookEndpoints {
    public static final String NOTEBOOK_PAGE = "/web/notebook";
    public static final String NOTEBOOK_NEW_PAGE = "/web/notebook/new/{parent}";
    public static final String NOTEBOOK_NEW_LIST_ITEM_PAGE = "/web/notebook/new/{listItemType}/"; //{parent}
    public static final String NOTEBOOK_EDIT_LIST_ITEM_PAGE = "/web/notebook/edit";

    //Category
    public static final String NOTEBOOK_GET_CATEGORY_TREE = "/api/notebook/category/tree";
    public static final String NOTEBOOK_CREATE_CATEGORY = "/api/notebook/category";
    public static final String NOTEBOOK_GET_CHILDREN_OF_CATEGORY = "/api/notebook/category/children";

    //Text
    public static final String NOTEBOOK_CREATE_TEXT = "/api/notebook/text";
    public static final String NOTEBOOK_GET_TEXT = "/api/notebook/text/{listItemId}";
    public static final String NOTEBOOK_EDIT_TEXT = "/api/notebook/text/{listItemId}";

    //ListItem
    public static final String NOTEBOOK_DELETE_LIST_ITEM = "/api/notebook/item/{listItemId}";
    public static final String NOTEBOOK_EDIT_LIST_ITEM = "/api/notebook/item/{listItemId}";
    public static final String NOTEBOOK_CLONE_LIST_ITEM = "/api/notebook/{listItemId}/clone";
    public static final String NOTEBOOK_MOVE_LIST_ITEM = "/api/notebook/{listItemId}/move";
    public static final String NOTEBOOK_GET_LIST_ITEM = "/api/notebook/list-item/{listItemId}";

    //Pin
    public static final String NOTEBOOK_PIN_LIST_ITEM = "/api/notebook/item/{listItemId}/pin";
    public static final String NOTEBOOK_GET_PINNED_ITEMS = "/api/notebook/item/pinned";

    //Link
    public static final String NOTEBOOK_CREATE_LINK = "/api/notebook/link";

    //Checklist
    public static final String NOTEBOOK_CREATE_CHECKLIST = "/api/notebook/checklist";
    public static final String NOTEBOOK_EDIT_CHECKLIST = "/api/notebook/checklist/{listItemId}";
    public static final String NOTEBOOK_GET_CHECKLIST = "/api/notebook/checklist/{listItemId}";
    public static final String NOTEBOOK_UPDATE_CHECKLIST_ITEM_STATUS = "/api/notebook/checklist/item/{checklistItemId}/status";
    public static final String NOTEBOOK_EDIT_CHECKLIST_ITEM = "/api/notebook/checklist/item/{checklistItemId}/content";
    public static final String NOTEBOOK_ADD_CHECKLIST_ITEM = "/api/notebook/checklist/{listItemId}/item";
    public static final String NOTEBOOK_DELETE_CHECKLIST_ITEM = "/api/notebook/checklist/item/{checklistItemId}";
    public static final String NOTEBOOK_CHECKLIST_DELETE_CHECKED = "/api/notebook/checklist/{listItemId}/checked";
    public static final String NOTEBOOK_ORDER_CHECKLIST_ITEMS = "/api/notebook/checklist/{listItemId}/order";

    //Table
    public static final String NOTEBOOK_CREATE_TABLE = "/api/notebook/table";
    public static final String NOTEBOOK_EDIT_TABLE = "/api/notebook/table/{listItemId}";
    public static final String NOTEBOOK_GET_TABLE = "/api/notebook/table/{listItemId}";
    public static final String NOTEBOOK_TABLE_SET_ROW_STATUS = "/api/notebook/table/row/{rowId}/status";
    public static final String NOTEBOOK_TABLE_DELETE_CHECKED = "/api/notebook/table/{listItemId}/checked";
    public static final String NOTEBOOK_TABLE_SET_CHECKBOX_COLUMN_STATUS = "/api/notebook/table/column/{columnId}/status";

    //Etc
    public static final String NOTEBOOK_SEARCH = "/api/notebook/item/search";
    public static final String NOTEBOOK_ARCHIVE_ITEM = "/api/notebook/item/{listItemId}/archive";

    //Only-title
    public static final String NOTEBOOK_CREATE_ONLY_TITLE = "/api/notebook/only-title";

    //Image
    public static final String NOTEBOOK_CREATE_IMAGE = "/api/notebook/image";

    //File
    public static final String NOTEBOOK_CREATE_FILE = "/api/notebook/file";

    //Pin Group
    public static final String NOTEBOOK_CREATE_PIN_GROUP = "/api/notebook/pin-group";
    public static final String NOTEBOOK_GET_PIN_GROUPS = "/api/notebook/pin-group";
    public static final String NOTEBOOK_DELETE_PIN_GROUP = "/api/notebook/pin-group/{pinGroupId}";
    public static final String NOTEBOOK_RENAME_PIN_GROUP = "/api/notebook/pin-group/{pinGroupId}";
    public static final String NOTEBOOK_ADD_ITEM_TO_PIN_GROUP = "/api/notebook/pin-group/{pinGroupId}/add/{listItemId}";
    public static final String NOTEBOOK_REMOVE_ITEM_FROM_PIN_GROUP = "/api/notebook/pin-group/{pinGroupId}/remove/{listItemId}";
    public static final String NOTEBOOK_PIN_GROUP_OPENED = "/api/notebook/pin-group/{pinGroupId}";
}
