package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreLoadGameRequest;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
public class LoadGameRequestValidatorTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID HOST = UUID.randomUUID();

    @InjectMocks
    private LoadGameRequestValidator underTest;

    @Test
    public void nullHost() {
        SkyXploreLoadGameRequest request = SkyXploreLoadGameRequest.builder()
            .host(null)
            .gameId(GAME_ID)
            .members(Arrays.asList(HOST))
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "host", "must not be null");
    }

    @Test
    public void nullGameId() {
        SkyXploreLoadGameRequest request = SkyXploreLoadGameRequest.builder()
            .host(HOST)
            .gameId(null)
            .members(Arrays.asList(HOST))
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "gameId", "must not be null");
    }

    @Test
    public void nullMembers() {
        SkyXploreLoadGameRequest request = SkyXploreLoadGameRequest.builder()
            .host(HOST)
            .gameId(GAME_ID)
            .members(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "members", "must not be null");
    }

    @Test
    public void membersDoesNotContainHost() {
        SkyXploreLoadGameRequest request = SkyXploreLoadGameRequest.builder()
            .host(HOST)
            .gameId(GAME_ID)
            .members(Collections.emptyList())
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "members", "does not contain host");
    }

    @Test
    public void valid() {
        SkyXploreLoadGameRequest request = SkyXploreLoadGameRequest.builder()
            .host(HOST)
            .gameId(GAME_ID)
            .members(Arrays.asList(HOST))
            .build();

        underTest.validate(request);
    }
}