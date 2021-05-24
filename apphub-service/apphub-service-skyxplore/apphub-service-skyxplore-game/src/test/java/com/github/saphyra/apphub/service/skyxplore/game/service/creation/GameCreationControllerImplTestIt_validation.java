package com.github.saphyra.apphub.service.skyxplore.game.service.creation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.saphyra.apphub.api.platform.localization.client.LocalizationApiClient;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.AiPresence;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.PlanetSize;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.SystemAmount;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.SystemSize;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.UniverseSize;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationRequest;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationSettingsRequest;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.service.skyxplore.game.SkyxploreGameApplication;
import com.github.saphyra.apphub.test.common.TestConstants;
import com.github.saphyra.apphub.test.common.rest_assured.RequestFactory;
import com.github.saphyra.apphub.test.common.rest_assured.UrlFactory;
import io.restassured.response.Response;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(classes = {SkyxploreGameApplication.class})
public class GameCreationControllerImplTestIt_validation {
    private static final UUID HOST = UUID.randomUUID();
    private static final UUID PLAYER_ID = UUID.randomUUID();
    private static final UUID ALLIANCE_ID = UUID.randomUUID();
    private static final String ALLIANCE_NAME = "alliance-name";
    private static final String GAME_NAME = "game-name";
    private static final String LOCALIZED_MESSAGE = "localized-message";

    @LocalServerPort
    private int serverPort;

    @MockBean
    private LocalizationApiClient localizationApiClient;

    @Before
    public void setUp() {
        given(localizationApiClient.translate(anyString(), eq(TestConstants.DEFAULT_LOCALE))).willReturn(LOCALIZED_MESSAGE);
    }

    @Test
    public void nullSetting() {
        SkyXploreGameCreationRequest request = validRequest()
            .toBuilder()
            .settings(null)
            .build();

        nullCheckTest(request, "settings");
    }

    @Test
    public void nullUniverseSize() {
        SkyXploreGameCreationRequest request = validRequest()
            .toBuilder()
            .settings(validSettings().toBuilder().universeSize(null).build())
            .build();

        nullCheckTest(request, "universeSize");
    }

    @Test
    public void nullSystemAmount() {
        SkyXploreGameCreationRequest request = validRequest()
            .toBuilder()
            .settings(validSettings().toBuilder().systemAmount(null).build())
            .build();

        nullCheckTest(request, "systemAmount");
    }

    @Test
    public void nullSystemSize() {
        SkyXploreGameCreationRequest request = validRequest()
            .toBuilder()
            .settings(validSettings().toBuilder().systemSize(null).build())
            .build();

        nullCheckTest(request, "systemSize");
    }

    @Test
    public void nullPlanetSize() {
        SkyXploreGameCreationRequest request = validRequest()
            .toBuilder()
            .settings(validSettings().toBuilder().planetSize(null).build())
            .build();

        nullCheckTest(request, "planetSize");
    }

    @Test
    public void nullAiPresence() {
        SkyXploreGameCreationRequest request = validRequest()
            .toBuilder()
            .settings(validSettings().toBuilder().aiPresence(null).build())
            .build();

        nullCheckTest(request, "aiPresence");
    }

    @Test
    public void nullHost() {
        SkyXploreGameCreationRequest request = validRequest()
            .toBuilder()
            .host(null)
            .build();

        nullCheckTest(request, "host");
    }

    @Test
    public void nullMembers() {
        SkyXploreGameCreationRequest request = validRequest()
            .toBuilder()
            .members(null)
            .build();

        nullCheckTest(request, "members");
    }

    @Test
    public void membersDoesNotContainHost() {
        SkyXploreGameCreationRequest request = validRequest()
            .toBuilder()
            .members(CollectionUtils.singleValueMap(UUID.randomUUID(), null))
            .build();

        performTest(request, "host", "unknown id");
    }

    @Test
    public void nullAlliances() {
        SkyXploreGameCreationRequest request = validRequest()
            .toBuilder()
            .alliances(null)
            .build();

        nullCheckTest(request, "alliances");
    }

    @Test
    public void nullAllianceName() {
        SkyXploreGameCreationRequest request = validRequest()
            .toBuilder()
            .alliances(CollectionUtils.singleValueMap(ALLIANCE_ID, null))
            .build();

        nullCheckTest(request, "allianceName");
    }

    @Test
    public void allianceNameEmpty() {
        SkyXploreGameCreationRequest request = validRequest()
            .toBuilder()
            .alliances(CollectionUtils.singleValueMap(ALLIANCE_ID, ""))
            .build();

        performTest(request, "allianceName", "empty");
    }

    @Test
    public void allianceNameTooLong() {
        SkyXploreGameCreationRequest request = validRequest()
            .toBuilder()
            .alliances(CollectionUtils.singleValueMap(ALLIANCE_ID, Stream.generate(() -> "a").limit(31).collect(Collectors.joining())))
            .build();

        performTest(request, "allianceName", "too long");
    }

    @Test
    public void allianceNameNotUnique() {
        SkyXploreGameCreationRequest request = validRequest()
            .toBuilder()
            .alliances(CollectionUtils.toMap(
                new BiWrapper<>(ALLIANCE_ID, ALLIANCE_NAME),
                new BiWrapper<>(UUID.randomUUID(), ALLIANCE_NAME)
            ))
            .build();

        performTest(request, "allianceName", "not unique");
    }

    @Test
    public void playerInUnknownAlliance() {
        SkyXploreGameCreationRequest request = validRequest()
            .toBuilder()
            .members(CollectionUtils.singleValueMap(HOST, UUID.randomUUID()))
            .build();

        performTest(request, "members", "contains unknown allianceId");
    }

    @Test
    public void nullGameName() {
        SkyXploreGameCreationRequest request = validRequest()
            .toBuilder()
            .gameName(null)
            .build();

        nullCheckTest(request, "gameName");
    }

    @Test
    public void gameNameTooShort() {
        SkyXploreGameCreationRequest request = validRequest()
            .toBuilder()
            .gameName("a")
            .build();

        performTest(request, ErrorCode.GAME_NAME_TOO_SHORT);
    }

    @Test
    public void gameNameTooLong() {
        SkyXploreGameCreationRequest request = validRequest()
            .toBuilder()
            .gameName(Stream.generate(() -> "a").limit(31).collect(Collectors.joining()))
            .build();

        performTest(request, ErrorCode.GAME_NAME_TOO_LONG);
    }

    private void nullCheckTest(SkyXploreGameCreationRequest request, String paramName) {
        performTest(request, paramName, "must not be null");
    }

    private void performTest(SkyXploreGameCreationRequest request, String paramName, String paramValue) {
        ErrorResponse errorResponse = performTest(request, ErrorCode.INVALID_PARAM);

        assertThat(errorResponse.getParams()).containsEntry(paramName, paramValue);
    }

    private ErrorResponse performTest(SkyXploreGameCreationRequest request, ErrorCode errorCode) {
        Response response = RequestFactory.createRequest()
            .body(request)
            .put(UrlFactory.create(serverPort, Endpoints.INTERNAL_SKYXPLORE_CREATE_GAME));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(errorCode.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        return errorResponse;
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
            .settings(validSettings())
            .gameName(GAME_NAME)
            .build();
    }

    private SkyXploreGameCreationSettingsRequest validSettings() {
        return SkyXploreGameCreationSettingsRequest.builder()
            .universeSize(UniverseSize.SMALL)
            .systemAmount(SystemAmount.RANDOM)
            .systemSize(SystemSize.SMALL)
            .planetSize(PlanetSize.LARGE)
            .aiPresence(AiPresence.COMMON)
            .build();
    }
}