package com.github.saphyra.apphub.service.feature.task_manager.domain;

import com.github.saphyra.apphub.lib.dynamodb.MonitoringFunctionality;

public enum TaskManagerMonitoringFunctionality implements MonitoringFunctionality {
    //ALM
    SAVE_ALM,
    GET_ALM_BY_PRINCIPAL_AND_OBJECT_TYPE,
    FIND_ALM_FOR_OBJECT,

    //Invitation
    SAVE_INVITATIONS,
    GET_INVITATIONS_OF_USER,
    GET_INVITATIONS_BY_USER_ID_AND_ORGANIZATION_ID,
    DELETE_INVITATIONS,

    //Notification
    SAVE_NOTIFICATIONS,
    GET_NOTIFICATIONS_BY_USER_ID,
    DELETE_NOTIFICATIONS,
    GET_NOTIFICATIONS,
    ;

    @Override
    public String assemble(String tableName) {
        return tableName + "_" + this.name();
    }
}
