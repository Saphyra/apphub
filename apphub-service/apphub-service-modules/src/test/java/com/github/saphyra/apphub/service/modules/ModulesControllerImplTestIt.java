package com.github.saphyra.apphub.service.modules;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.saphyra.apphub.api.modules.model.response.ModuleResponse;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.api.platform.localization.client.LocalizationApiClient;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.config.access_token.AccessTokenHeaderConverter;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.modules.dao.favorite.Favorite;
import com.github.saphyra.apphub.service.modules.dao.favorite.FavoriteDao;
import com.github.saphyra.apphub.test.common.api.AccessTokenProtectedOperation;
import com.github.saphyra.apphub.test.common.api.ApiTestConfiguration;
import com.github.saphyra.apphub.test.common.rest_assured.UrlFactory;
import com.github.saphyra.util.ObjectMapperWrapper;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.github.saphyra.apphub.test.common.rest_assured.RequestFactory.createRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(classes = ApiTestConfiguration.class)
public class ModulesControllerImplTestIt {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String MODULE = "module";
    private static final String ROLE = "role";
    private static final String MODULE_FAVORITE = "favorite";
    private static final String LOCALIZED_MESSAGE = "localized_message";

    @LocalServerPort
    private int serverPort;

    @Autowired
    private AccessTokenHeaderConverter accessTokenHeaderConverter;

    @Autowired
    private AccessTokenProvider accessTokenProvider;

    @Autowired
    private FavoriteDao favoriteDao;

    @Autowired
    private ObjectMapperWrapper objectMapperWrapper;

    @MockBean
    public LocalizationApiClient localizationApi;

    private AccessTokenProtectedOperation accessTokenProtectedOperation;

    @Before
    public void setUp() {
        accessTokenProtectedOperation = new AccessTokenProtectedOperation(accessTokenProvider);

        given(localizationApi.translate(ErrorCode.INVALID_PARAM.name(), "hu")).willReturn(LOCALIZED_MESSAGE);
    }

    @After
    public void clear() {
        favoriteDao.deleteAll();
    }

    @Test
    public void deleteAccountEvent() {
        AccessTokenHeader accessTokenHeader = AccessTokenHeader.builder()
            .accessTokenId(UUID.randomUUID())
            .userId(USER_ID)
            .build();

        Favorite favorite = Favorite.builder()
            .userId(USER_ID)
            .module(MODULE)
            .favorite(true)
            .build();

        accessTokenProtectedOperation.operate(accessTokenHeader, () -> favoriteDao.save(favorite));

        SendEventRequest<DeleteAccountEvent> sendEventRequest = SendEventRequest.<DeleteAccountEvent>builder()
            .eventName(DeleteAccountEvent.EVENT_NAME)
            .payload(new DeleteAccountEvent(USER_ID))
            .build();

        Response response = createRequest()
            .body(sendEventRequest)
            .post(UrlFactory.create(serverPort, Endpoints.DELETE_ACCOUNT_EVENT));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        assertThat(accessTokenProtectedOperation.getResult(accessTokenHeader, () -> favoriteDao.findAll())).isEmpty();
    }

    @Test
    public void getModules() {
        AccessTokenHeader accessTokenHeader = AccessTokenHeader.builder()
            .accessTokenId(UUID.randomUUID())
            .userId(USER_ID)
            .roles(Arrays.asList(ROLE))
            .build();

        Favorite favorite = Favorite.builder()
            .userId(USER_ID)
            .module(MODULE_FAVORITE)
            .favorite(true)
            .build();

        accessTokenProtectedOperation.operate(accessTokenHeader, () -> favoriteDao.save(favorite));

        Response response = createRequest()
            .header(Constants.ACCESS_TOKEN_HEADER, accessTokenHeaderConverter.convertDomain(accessTokenHeader))
            .get(UrlFactory.create(serverPort, Endpoints.GET_MODULES_OF_USER))
            .andReturn();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        TypeReference<Map<String, List<ModuleResponse>>> responseReference = new TypeReference<Map<String, List<ModuleResponse>>>() {
        };
        Map<String, List<ModuleResponse>> moduleResponses = objectMapperWrapper.readValue(response.getBody().asString(), responseReference);

        assertThat(moduleResponses).containsKey("default");
        assertThat(moduleResponses.get("default")).containsExactlyInAnyOrder(
            ModuleResponse.builder().name("allowed-by-default").url("url").favorite(false).build(),
            ModuleResponse.builder().name("not-allowed-by-default").url("url").favorite(false).build()
        );

        assertThat(moduleResponses).containsKey("favorite");
        assertThat(moduleResponses.get("favorite")).containsExactlyInAnyOrder(
            ModuleResponse.builder().name("favorite").url("url").favorite(true).build(),
            ModuleResponse.builder().name("not-favorite").url("url").favorite(false).build()
        );
        assertThat(moduleResponses).doesNotContainKey("empty");
    }

    @Test
    public void setFavorite_invalidModule() {
        AccessTokenHeader accessTokenHeader = AccessTokenHeader.builder()
            .accessTokenId(UUID.randomUUID())
            .userId(USER_ID)
            .build();

        OneParamRequest<Boolean> request = new OneParamRequest<>(true);

        Map<String, Object> pathVariables = new HashMap<>();
        pathVariables.put("module", "unknown-module");

        Response response = createRequest()
            .header(Constants.ACCESS_TOKEN_HEADER, accessTokenHeaderConverter.convertDomain(accessTokenHeader))
            .body(request)
            .post(UrlFactory.create(serverPort, Endpoints.SET_FAVORITE, pathVariables));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        ErrorResponse errorResponse = response.getBody()
            .as(ErrorResponse.class);

        assertThat(errorResponse.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(errorResponse.getParams().get("module")).isEqualTo("does not exist");
    }

    @Test
    public void setFavorite_nullFavorite() {
        AccessTokenHeader accessTokenHeader = AccessTokenHeader.builder()
            .accessTokenId(UUID.randomUUID())
            .userId(USER_ID)
            .build();

        OneParamRequest<Boolean> request = new OneParamRequest<>(null);

        Map<String, Object> pathVariables = new HashMap<>();
        pathVariables.put("module", "favorite");

        Response response = createRequest()
            .header(Constants.ACCESS_TOKEN_HEADER, accessTokenHeaderConverter.convertDomain(accessTokenHeader))
            .body(request)
            .post(UrlFactory.create(serverPort, Endpoints.SET_FAVORITE, pathVariables));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        ErrorResponse errorResponse = response.getBody()
            .as(ErrorResponse.class);

        assertThat(errorResponse.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(errorResponse.getParams().get("value")).isEqualTo("must not be null");
    }

    @Test
    public void setFavorite() {
        AccessTokenHeader accessTokenHeader = AccessTokenHeader.builder()
            .accessTokenId(UUID.randomUUID())
            .userId(USER_ID)
            .roles(Arrays.asList(ROLE))
            .build();

        OneParamRequest<Boolean> request = new OneParamRequest<>(true);

        Map<String, Object> pathVariables = new HashMap<>();
        pathVariables.put("module", "favorite");

        Response response = createRequest()
            .header(Constants.ACCESS_TOKEN_HEADER, accessTokenHeaderConverter.convertDomain(accessTokenHeader))
            .body(request)
            .post(UrlFactory.create(serverPort, Endpoints.SET_FAVORITE, pathVariables));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        TypeReference<Map<String, List<ModuleResponse>>> responseReference = new TypeReference<Map<String, List<ModuleResponse>>>() {
        };
        Map<String, List<ModuleResponse>> moduleResponses = objectMapperWrapper.readValue(response.getBody().asString(), responseReference);

        assertThat(moduleResponses).containsKey("default");
        assertThat(moduleResponses.get("default")).containsExactlyInAnyOrder(
            ModuleResponse.builder().name("allowed-by-default").url("url").favorite(false).build(),
            ModuleResponse.builder().name("not-allowed-by-default").url("url").favorite(false).build()
        );

        assertThat(moduleResponses).containsKey("favorite");
        assertThat(moduleResponses.get("favorite")).containsExactlyInAnyOrder(
            ModuleResponse.builder().name("favorite").url("url").favorite(true).build(),
            ModuleResponse.builder().name("not-favorite").url("url").favorite(false).build()
        );
        assertThat(moduleResponses).doesNotContainKey("empty");
    }
}