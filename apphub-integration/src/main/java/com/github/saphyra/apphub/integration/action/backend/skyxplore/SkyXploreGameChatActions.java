package com.github.saphyra.apphub.integration.action.backend.skyxplore;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.CreateChatRoomRequest;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class SkyXploreGameChatActions {
    public static List<SkyXploreCharacterModel> getPlayers(Language language, UUID accessTokenId) {
        Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .get(UrlFactory.create(Endpoints.SKYXPLORE_GAME_GET_PLAYERS));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.stream(response.getBody().as(SkyXploreCharacterModel[].class))
            .collect(Collectors.toList());
    }

    public static void createChatRoom(Language language, UUID accessTokenId, CreateChatRoomRequest request) {
        Response response = getCreateChatRoomResponse(language, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getCreateChatRoomResponse(Language language, UUID accessTokenId, CreateChatRoomRequest request) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(request)
            .put(UrlFactory.create(Endpoints.SKYXPLORE_GAME_CREATE_CHAT_ROOM));
    }

    public static Response getLeaveChatRoomResponse(Language language, UUID accessTokenId, String roomId) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .delete(UrlFactory.create(Endpoints.SKYXPLORE_GAME_LEAVE_CHAT_ROOM, "roomId", roomId));
    }
}
