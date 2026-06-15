package com.github.saphyra.apphub.service.notebook.dao;

import com.github.saphyra.apphub.lib.dynamodb.MonitoringFunctionality;

public enum NotebookMonitoringFunctionality implements MonitoringFunctionality {
    //Common
    DELETE_BY_LIST_ITEM_ID_QUERY,
    DELETE_BY_LIST_ITEM_ID_DELETE,

    //List Item
    SAVE_LIST_ITEM,
    GET_LIST_ITEMS_BY_USER_ID_AND_PARENT,
    GET_LIST_ITEMS_BY_USER_ID_AND_TYPE,
    FIND_LIST_ITEM_BY_ID,
    GET_LIST_ITEMS_BY_USER_ID,
    DELETE_LIST_ITEM,

    //Checklist Item
    FIND_CHECKLIST_ITEM_BY_ID,
    DELETE_CHECKLIST_ITEM,
    SAVE_CHECKLIST_ITEM,
    GET_CHECKLIST_ITEMS_BY_LIST_ITEM_ID,
    DELETE_CHECKLIST_ITEMS,
    SAVE_CHECKLIST_ITEMS,

    //Table Head
    SAVE_TABLE_HEAD,
    DELETE_TABLE_HEAD,
    FIND_TABLE_HEAD_BY_LIST_ITEM_ID,

    //Table Row
    DELETE_TABLE_ROW,
    SAVE_TABLE_ROW,
    SAVE_TABLE_ROWS,
    DELETE_TABLE_ROWS,
    FIND_TABLE_ROW_BY_ID,
    GET_TABLE_ROWS_BY_LIST_ITEM_ID,

    //Content
    SAVE_CONTENTS,
    DELETE_CONTENTS_BY_LIST_ITEM_ID_AND_BATCH_INDEXES,
    GET_CONTENTS_BY_LIST_ITEM_ID,
    ;

    @Override
    public String assemble(String tableName){
        return tableName + "_" + this.name();
    }
}
