package com.github.saphyra.apphub.integration.framework.endpoints;

public class CommunityEndpoints {
    public static final String COMMUNITY_PAGE = "/web/community";
    public static final String COMMUNITY_BLACKLIST_SEARCH = "/api/community/blacklist/search";
    public static final String COMMUNITY_GET_BLACKLIST = "/api/community/blacklist";
    public static final String COMMUNITY_CREATE_BLACKLIST = "/api/community/blacklist";
    public static final String COMMUNITY_DELETE_BLACKLIST = "/api/community/blacklist/{blacklistId}";
    public static final String COMMUNITY_GET_SENT_FRIEND_REQUESTS = "/api/community/friend-request/sent";
    public static final String COMMUNITY_GET_RECEIVED_FRIEND_REQUESTS = "/api/community/friend-request/received";
    public static final String COMMUNITY_FRIEND_REQUEST_SEARCH = "/api/community/friend-request/search";
    public static final String COMMUNITY_FRIEND_REQUEST_CREATE = "/api/community/friend-request";
    public static final String COMMUNITY_FRIEND_REQUEST_DELETE = "/api/community/friend-request/{friendRequestId}";
    public static final String COMMUNITY_FRIEND_REQUEST_ACCEPT = "/api/community/friend-request/{friendRequestId}";
    public static final String COMMUNITY_GET_FRIENDS = "/api/community/friendship";
    public static final String COMMUNITY_DELETE_FRIENDSHIP = "/api/community/friendship/{friendshipId}";
    public static final String COMMUNITY_GET_GROUPS = "/api/community/group";
    public static final String COMMUNITY_GROUP_CREATE = "/api/community/group";
    public static final String COMMUNITY_GROUP_RENAME = "/api/community/group/{groupId}/name";
    public static final String COMMUNITY_GROUP_GET_MEMBERS = "/api/community/group/{groupId}/member";
    public static final String COMMUNITY_GROUP_SEARCH_MEMBER_CANDIDATES = "/api/community/group/{groupId}/member/search";
    public static final String COMMUNITY_GROUP_CREATE_MEMBER = "/api/community/group/{groupId}/member";
    public static final String COMMUNITY_GROUP_CHANGE_INVITATION_TYPE = "/api/community/group/{groupId}/invitation-type";
    public static final String COMMUNITY_GROUP_DELETE_MEMBER = "/api/community/group/{groupId}/member/{groupMemberId}";
    public static final String COMMUNITY_GROUP_MEMBER_ROLES = "/api/community/group/{groupId}/member/{groupMemberId}";
    public static final String COMMUNITY_GROUP_DELETE = "/api/community/group/{groupId}";
    public static final String COMMUNITY_GROUP_CHANGE_OWNER = "/api/community/group/{groupId}/owner";
}
