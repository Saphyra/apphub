package com.github.saphyra.apphub.integration.action.backend.skyxplore;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.ChatRoomResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.CreateChatRoomRequest;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class SkyXploreGameChatActions {
    public static List<SkyXploreCharacterModel> getPlayers(int serverPort, UUID accessTokenId) {
        Response response = getPlayersResponse(serverPort, accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.stream(response.getBody().as(SkyXploreCharacterModel[].class))
            .collect(Collectors.toList());
    }

    public static Response getPlayersResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_GAME_GET_PLAYERS, Collections.emptyMap(), Map.of("excludeSelf", true)));
    }

    public static void createChatRoom(int serverPort, UUID accessTokenId, CreateChatRoomRequest request) {
        Response response = getCreateChatRoomResponse(serverPort, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getCreateChatRoomResponse(int serverPort, UUID accessTokenId, CreateChatRoomRequest request) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(request)
            .put(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_GAME_CREATE_CHAT_ROOM));
    }

    public static Response getLeaveChatRoomResponse(int serverPort, UUID accessTokenId, String roomId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_GAME_LEAVE_CHAT_ROOM, "roomId", roomId));
    }

    public static List<ChatRoomResponse> getChatRooms(int serverPort, UUID accessTokenId) {
        Response response = getChatRoomsResponse(serverPort, accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(ChatRoomResponse[].class));
    }

    public static Response getChatRoomsResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_GAME_GET_CHAT_ROOMS));
    }
}
