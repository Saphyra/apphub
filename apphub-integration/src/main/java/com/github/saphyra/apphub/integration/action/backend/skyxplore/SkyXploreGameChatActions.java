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
    public static List<SkyXploreCharacterModel> getPlayers(UUID accessTokenId) {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.SKYXPLORE_GAME_GET_PLAYERS, Collections.emptyMap(), Map.of("excludeSelf", true)));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.stream(response.getBody().as(SkyXploreCharacterModel[].class))
            .collect(Collectors.toList());
    }

    public static void createChatRoom(UUID accessTokenId, CreateChatRoomRequest request) {
        Response response = getCreateChatRoomResponse(accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getCreateChatRoomResponse(UUID accessTokenId, CreateChatRoomRequest request) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(request)
            .put(UrlFactory.create(Endpoints.SKYXPLORE_GAME_CREATE_CHAT_ROOM));
    }

    public static Response getLeaveChatRoomResponse(UUID accessTokenId, String roomId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(Endpoints.SKYXPLORE_GAME_LEAVE_CHAT_ROOM, "roomId", roomId));
    }

    public static List<ChatRoomResponse> getChatRooms(UUID accessTokenId) {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.SKYXPLORE_GAME_GET_CHAT_ROOMS));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(ChatRoomResponse[].class));
    }
}
