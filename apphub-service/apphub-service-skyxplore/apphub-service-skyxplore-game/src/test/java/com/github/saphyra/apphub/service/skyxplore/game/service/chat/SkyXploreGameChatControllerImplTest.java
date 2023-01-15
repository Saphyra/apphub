package com.github.saphyra.apphub.service.skyxplore.game.service.chat;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.api.skyxplore.request.CreateChatRoomRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Player;
import com.github.saphyra.apphub.service.skyxplore.game.service.chat.create.CreateChatRoomService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SkyXploreGameChatControllerImplTest {
    private static final UUID USER_ID_1 = UUID.randomUUID();
    public static final AccessTokenHeader ACCESS_TOKEN_HEADER = AccessTokenHeader.builder().userId(USER_ID_1).build();
    private static final UUID USER_ID_2 = UUID.randomUUID();
    private static final String CHARACTER_NAME = "character-name";
    private static final String ROOM_ID = "room-id";

    @Mock
    private GameDao gameDao;

    @Mock
    private CreateChatRoomService createChatRoomService;

    @Mock
    private LeaveChatRoomService leaveChatRoomService;

    @InjectMocks
    private SkyXploreGameChatControllerImpl underTest;

    @Mock
    private Game game;

    @Mock
    private Player player1;

    @Mock
    private Player player2;

    @Mock
    private Player player3;

    @Mock
    private CreateChatRoomRequest createChatRoomRequest;

    @Test
    public void getPlayers() {
        given(player1.isAi()).willReturn(true);
        given(player2.isAi()).willReturn(false);
        given(player3.isAi()).willReturn(false);
        given(player2.isConnected()).willReturn(false);
        given(player3.isConnected()).willReturn(true);

        given(player3.getUserId()).willReturn(USER_ID_2);
        given(player3.getPlayerName()).willReturn(CHARACTER_NAME);

        given(gameDao.findByUserIdValidated(USER_ID_1)).willReturn(game);
        given(game.getPlayers()).willReturn(CollectionUtils.toMap(
            new BiWrapper<>(UUID.randomUUID(), player1),
            new BiWrapper<>(UUID.randomUUID(), player2),
            new BiWrapper<>(UUID.randomUUID(), player3)
        ));
        List<SkyXploreCharacterModel> result = underTest.getPlayers(ACCESS_TOKEN_HEADER);

        assertThat(result).containsExactly(SkyXploreCharacterModel.builder().id(USER_ID_2).name(CHARACTER_NAME).build());
    }

    @Test
    public void createChatRoom() {
        underTest.createChatRoom(createChatRoomRequest, ACCESS_TOKEN_HEADER);

        verify(createChatRoomService).createChatRoom(USER_ID_1, createChatRoomRequest);
    }

    @Test
    public void leaveChatRoom() {
        underTest.leaveChatRoom(ROOM_ID, ACCESS_TOKEN_HEADER);

        verify(leaveChatRoomService).leave(USER_ID_1, ROOM_ID);
    }
}