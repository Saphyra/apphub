package com.github.saphyra.apphub.service.feature.task_manager.domain;

import com.github.saphyra.apphub.lib.dynamodb.MonitoringFunctionality;

public enum TaskManagerMonitoringFunctionality implements MonitoringFunctionality {
    //ALM
    SAVE_ALM,
    GET_ALM_BY_PRINCIPAL_AND_OBJECT_TYPE,
    FIND_ALM_FOR_OBJECT,
    GET_ALMS_BY_PRINCIPAL,
    GET_ALMS_BY_OBJECTS,
    GET_ALMS_BY_OBJECT,
    DELETE_ALMS,

    //Invitation
    SAVE_INVITATIONS,
    GET_INVITATIONS_OF_USER,
    GET_INVITATIONS_BY_USER_ID_AND_ORGANIZATION_ID,
    DELETE_INVITATIONS,
    GET_INVITATIONS_BY_INVITED_BY,
    GET_INVITATIONS_BY_ORGANIZATION_ID,

    //Notification
    SAVE_NOTIFICATIONS,
    GET_NOTIFICATIONS_BY_USER_ID,
    DELETE_NOTIFICATIONS,
    GET_NOTIFICATIONS,
    GET_NOTIFICATIONS_BY_USER_ID_AND_ORGANIZATION_ID,
    GET_NOTIFICATIONS_BY_ORGANIZATION_ID,

    //Organization
    SAVE_ORGANIZATION,
    GET_ORGANIZATIONS,
    GET_ORGANIZATION,
    DELETE_ORGANIZATION,
    ;

    @Override
    public String assemble(String tableName) {
        return tableName + "_" + this.name();
    }
}
