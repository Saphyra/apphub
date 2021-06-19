package com.github.saphyra.apphub.service.skyxplore.game.service.chat.create;

import com.github.saphyra.apphub.api.skyxplore.request.CreateChatRoomRequest;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Player;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class CreateChatRoomRequestValidatorTest {
    private static final UUID PLAYER_ID = UUID.randomUUID();

    @InjectMocks
    private CreateChatRoomRequestValidator underTest;

    @Mock
    private Game game;

    @Mock
    private Player player;

    @Before
    public void setUp() {
        given(game.getPlayers()).willReturn(CollectionUtils.singleValueMap(PLAYER_ID, player));
    }

    @Test
    public void nullMembers() {
        CreateChatRoomRequest request = CreateChatRoomRequest.builder()
            .members(null)
            .roomTitle("asd")
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request, game));

        ExceptionValidator.validateInvalidParam(ex, "members", "must not be null");
    }

    @Test
    public void membersContainsNull() {
        CreateChatRoomRequest request = CreateChatRoomRequest.builder()
            .members(Arrays.asList(new UUID[]{null}))
            .roomTitle("asd")
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request, game));

        ExceptionValidator.validateInvalidParam(ex, "members", "must not contain null");
    }

    @Test
    public void memberFromDifferentGame() {
        CreateChatRoomRequest request = CreateChatRoomRequest.builder()
            .members(Arrays.asList(UUID.randomUUID()))
            .roomTitle(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request, game));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPERATION);
    }

    @Test
    public void nullTitle() {
        CreateChatRoomRequest request = CreateChatRoomRequest.builder()
            .members(Arrays.asList(PLAYER_ID))
            .roomTitle(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request, game));

        ExceptionValidator.validateInvalidParam(ex, "roomTitle", "must not be null");
    }

    @Test
    public void titleTooShort() {
        CreateChatRoomRequest request = CreateChatRoomRequest.builder()
            .members(Arrays.asList(PLAYER_ID))
            .roomTitle("aa")
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request, game));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.BAD_REQUEST, ErrorCode.CHAT_ROOM_TITLE_TOO_SHORT);
    }

    @Test
    public void titleTooLong() {
        CreateChatRoomRequest request = CreateChatRoomRequest.builder()
            .members(Arrays.asList(PLAYER_ID))
            .roomTitle(Stream.generate(() -> "a").limit(21).collect(Collectors.joining()))
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request, game));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.BAD_REQUEST, ErrorCode.CHAT_ROOM_TITLE_TOO_LONG);
    }

    @Test
    public void valid() {
        CreateChatRoomRequest request = CreateChatRoomRequest.builder()
            .members(Arrays.asList(PLAYER_ID))
            .roomTitle("asd")
            .build();

        underTest.validate(request, game);
    }
}