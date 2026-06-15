package com.github.saphyra.apphub.service.user.common;

import com.github.saphyra.apphub.lib.dynamodb.MonitoringFunctionality;

public enum UserMonitoringFunctionality implements MonitoringFunctionality {
    //Common
    GET_ALL_USER_IDS,
    FIND_USER,

    //Profile
    DELETE_PROFILE,
    SAVE_PROFILE,

    //Marked for deletion
    MARK_USER_FOR_DELETION,
    UNMARK_USER_FOR_DELETION,
    GET_USER_IDS_MARKED_FOR_DELETION,

    //Credential
    SAVE_CREDENTIAL,
    DELETE_CREDENTIAL,
    FIND_CREDENTIAL,

    //Role
    SAVE_ROLE,
    DELETE_ROLE,
    ;

    @Override
    public String assemble(String tableName) {
        return tableName + "_" + this.name();
    }
}
