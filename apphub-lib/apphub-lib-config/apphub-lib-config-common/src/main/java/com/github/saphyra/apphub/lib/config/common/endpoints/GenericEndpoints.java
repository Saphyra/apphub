package com.github.saphyra.apphub.lib.config.common.endpoints;

public class GenericEndpoints {
    //PAGES
    public static final String INDEX_PAGE = "/web";
    public static final String ERROR_PAGE = "/web/error";

    //PLATFORM
    public static final String HEALTH = "/platform/health";
    @Deprecated
    public static final String TRANSLATE_ERROR_CODE = "/internal/localization/error-code";

    //EVENT SENDING
    public static final String REGISTER_PROCESSOR = "/platform/event-gateway";
    public static final String HEARTBEAT = "/platform/event-gateway/{serviceName}";
    public static final String SEND_EVENT = "/internal/event-gateway";
    public static final String EVENT_DELETE_ACCOUNT = "/event/delete-account";

    //WebSocket
    public static final String EVENT_WEB_SOCKET_SEND_PING_EVENT = "/event/web-socket/send-ping";
    public static final String EVENT_WEB_SOCKET_CONNECTION_CLEANUP = "/event/web-socket/connection-cleanup";

    //ETC
    public static final String GET_OWN_USER_ID = "/user/id";
}
