package com.github.saphyra.apphub.service.skyxplore.game.service.creation;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationRequest;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationSettingsRequest;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GameCreationRequestValidatorTest {
    private static final UUID HOST = UUID.randomUUID();
    private static final UUID PLAYER_ID = UUID.randomUUID();
    private static final UUID ALLIANCE_ID = UUID.randomUUID();
    private static final String ALLIANCE_NAME = "alliance-name";
    private static final String GAME_NAME = "game-name";

    @Mock
    private GameCreationSettingsValidator gameCreationSettingsValidator;

    @Mock
    private AllianceNameValidator allianceNameValidator;

    @InjectMocks
    private GameCreationRequestValidator underTest;

    @Mock
    private SkyXploreGameCreationSettingsRequest settings;

    @AfterEach
    public void validate() {
        verify(gameCreationSettingsValidator).validate(settings);
    }

    @Test
    public void nullHost() {
        SkyXploreGameCreationRequest request = validRequest()
            .toBuilder()
            .host(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "host", "must not be null");
    }

    @Test
    public void nullMembers() {
        SkyXploreGameCreationRequest request = validRequest()
            .toBuilder()
            .members(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "members", "must not be null");
    }

    @Test
    public void unknownHost() {
        SkyXploreGameCreationRequest request = validRequest()
            .toBuilder()
            .members(CollectionUtils.singleValueMap(UUID.randomUUID(), null))
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "host", "unknown id");
    }

    @Test
    public void nullAlliances() {
        SkyXploreGameCreationRequest request = validRequest()
            .toBuilder()
            .alliances(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "alliances", "must not be null");
    }

    @Test
    public void notUniqueAllianceName() {
        SkyXploreGameCreationRequest request = validRequest()
            .toBuilder()
            .alliances(CollectionUtils.toMap(
                new BiWrapper<>(ALLIANCE_ID, ALLIANCE_NAME),
                new BiWrapper<>(UUID.randomUUID(), ALLIANCE_NAME)
            ))
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "allianceName", "not unique");

        verify(allianceNameValidator, times(2)).validate(ALLIANCE_NAME);
    }

    @Test
    public void unknownAllianceId() {
        SkyXploreGameCreationRequest request = validRequest()
            .toBuilder()
            .members(CollectionUtils.singleValueMap(HOST, UUID.randomUUID()))
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "members", "contains unknown allianceId");

        verify(allianceNameValidator).validate(ALLIANCE_NAME);
    }

    @Test
    public void gameNameTooShort() {
        SkyXploreGameCreationRequest request = validRequest()
            .toBuilder()
            .gameName("a")
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "gameName", "too short");

        verify(allianceNameValidator).validate(ALLIANCE_NAME);
    }

    @Test
    public void gameNameTooLong() {
        SkyXploreGameCreationRequest request = validRequest()
            .toBuilder()
            .gameName(Stream.generate(() -> "a").limit(31).collect(Collectors.joining()))
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "gameName", "too long");

        verify(allianceNameValidator).validate(ALLIANCE_NAME);
    }

    private SkyXploreGameCreationRequest validRequest() {
        return SkyXploreGameCreationRequest.builder()
            .host(HOST)
            .members(
                CollectionUtils.toMap(
                    new BiWrapper<>(HOST, null),
                    new BiWrapper<>(PLAYER_ID, ALLIANCE_ID)
                )
            )
            .alliances(CollectionUtils.singleValueMap(ALLIANCE_ID, ALLIANCE_NAME))
            .settings(settings)
            .gameName(GAME_NAME)
            .build();
    }
}