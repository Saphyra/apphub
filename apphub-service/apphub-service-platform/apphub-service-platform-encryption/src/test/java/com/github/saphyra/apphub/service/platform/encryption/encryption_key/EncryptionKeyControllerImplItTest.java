package com.github.saphyra.apphub.service.platform.encryption.encryption_key;

import com.github.saphyra.apphub.api.platform.encryption.model.AccessMode;
import com.github.saphyra.apphub.api.platform.encryption.model.DataType;
import com.github.saphyra.apphub.api.platform.encryption.model.EncryptionKey;
import com.github.saphyra.apphub.api.platform.encryption.model.SharedData;
import com.github.saphyra.apphub.api.platform.localization.client.LocalizationClient;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.converter.AccessTokenHeaderConverter;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import com.github.saphyra.apphub.service.platform.encryption.encryption_key.dao.EncryptionKeyDao;
import com.github.saphyra.apphub.service.platform.encryption.shared_data.dao.SharedDataDao;
import com.github.saphyra.apphub.test.common.api.ApiTestConfiguration;
import com.github.saphyra.apphub.test.common.rest_assured.ErrorResponseValidator;
import com.github.saphyra.apphub.test.common.rest_assured.RequestFactory;
import com.github.saphyra.apphub.test.common.rest_assured.UrlFactory;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(classes = ApiTestConfiguration.class)
@Slf4j
public class EncryptionKeyControllerImplItTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String LOCALIZED_MESSAGE = "localized-message";
    private static final UUID EXTERNAL_ID = UUID.randomUUID();
    private static final AccessTokenHeader ACCESS_TOKEN_HEADER = AccessTokenHeader.builder()
        .userId(USER_ID)
        .build();

    @LocalServerPort
    private int serverPort;

    @SuppressWarnings("rawtypes")
    @Autowired
    private List<CrudRepository> repositories;

    @Autowired
    private AccessTokenHeaderConverter accessTokenHeaderConverter;

    @MockBean
    private LocalizationClient localizationClient;

    @Autowired
    private SharedDataDao sharedDataDao;

    @Autowired
    private EncryptionKeyDao encryptionKeyDao;

    @BeforeEach
    public void setUp() {
        given(localizationClient.translate(any(), any())).willReturn(LOCALIZED_MESSAGE);
    }

    @AfterEach
    public void clear() {
        repositories.forEach(CrudRepository::deleteAll);
    }

    @Test
    public void create_nullExternalId() {
        EncryptionKey request = EncryptionKey.builder()
            .externalId(null)
            .dataType(DataType.TEST)
            .userId(USER_ID)
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(request)
            .put(UrlFactory.create(serverPort, Endpoints.ENCRYPTION_INTERNAL_CREATE_KEY, "accessMode", AccessMode.EDIT));

        ErrorResponseValidator.verifyInvalidParam(response, "externalId", "must not be null");
    }

    @Test
    public void create_nullDataType() {
        EncryptionKey request = EncryptionKey.builder()
            .externalId(EXTERNAL_ID)
            .dataType(null)
            .userId(USER_ID)
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(request)
            .put(UrlFactory.create(serverPort, Endpoints.ENCRYPTION_INTERNAL_CREATE_KEY, "accessMode", AccessMode.EDIT));

        ErrorResponseValidator.verifyInvalidParam(response, "dataType", "must not be null");
    }

    @Test
    public void create_nullUserId() {
        EncryptionKey request = EncryptionKey.builder()
            .externalId(EXTERNAL_ID)
            .dataType(DataType.TEST)
            .userId(null)
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(request)
            .put(UrlFactory.create(serverPort, Endpoints.ENCRYPTION_INTERNAL_CREATE_KEY, "accessMode", AccessMode.EDIT));

        ErrorResponseValidator.verifyInvalidParam(response, "userId", "must not be null");
    }

    @Test
    public void createAndRecreate() {
        EncryptionKey request = EncryptionKey.builder()
            .externalId(EXTERNAL_ID)
            .dataType(DataType.TEST)
            .userId(USER_ID)
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(request)
            .put(UrlFactory.create(serverPort, Endpoints.ENCRYPTION_INTERNAL_CREATE_KEY, "accessMode", AccessMode.EDIT));

        assertThat(response.getStatusCode()).isEqualTo(200);

        String encryptionKey = response.getBody().asString();
        assertThat(encryptionKey).isNotNull();

        RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(request)
            .put(UrlFactory.create(serverPort, Endpoints.ENCRYPTION_INTERNAL_CREATE_KEY, "accessMode", AccessMode.EDIT));

        assertThat(response.getStatusCode()).isEqualTo(200);

        assertThat(response.getBody().asString()).isEqualTo(encryptionKey);
    }

    @Test
    public void delete_unsupportedAccessMode() {
        Map<String, Object> pathVariables = CollectionUtils.toMap(
            new BiWrapper<>("dataType", DataType.TEST),
            new BiWrapper<>("externalId", EXTERNAL_ID),
            new BiWrapper<>("accessMode", AccessMode.READ)
        );

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .delete(UrlFactory.create(serverPort, Endpoints.ENCRYPTION_INTERNAL_DELETE_KEY, pathVariables));

        ErrorResponseValidator.verifyInvalidParam(response, "accessMode", "must be one of [EDIT, DELETE]");
    }

    @Test
    public void delete_notFound() {
        Map<String, Object> pathVariables = CollectionUtils.toMap(
            new BiWrapper<>("dataType", DataType.TEST),
            new BiWrapper<>("externalId", EXTERNAL_ID),
            new BiWrapper<>("accessMode", AccessMode.EDIT)
        );

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .delete(UrlFactory.create(serverPort, Endpoints.ENCRYPTION_INTERNAL_DELETE_KEY, pathVariables));

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    @Test
    public void deleteOwn() {
        EncryptionKey encryptionKey = EncryptionKey.builder()
            .externalId(EXTERNAL_ID)
            .dataType(DataType.TEST)
            .userId(USER_ID)
            .build();

        RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(encryptionKey)
            .put(UrlFactory.create(serverPort, Endpoints.ENCRYPTION_INTERNAL_CREATE_KEY, "accessMode", AccessMode.EDIT));

        SharedData sharedData = SharedData.builder()
            .externalId(EXTERNAL_ID)
            .dataType(DataType.TEST)
            .publicData(true)
            .accessMode(AccessMode.EDIT)
            .build();
        RequestFactory.createRequest()
            .body(sharedData)
            .put(UrlFactory.create(serverPort, Endpoints.ENCRYPTION_INTERNAL_CREATE_SHARED_DATA));

        Map<String, Object> pathVariables = CollectionUtils.toMap(
            new BiWrapper<>("dataType", DataType.TEST),
            new BiWrapper<>("externalId", EXTERNAL_ID),
            new BiWrapper<>("accessMode", AccessMode.EDIT)
        );

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .delete(UrlFactory.create(serverPort, Endpoints.ENCRYPTION_INTERNAL_DELETE_KEY, pathVariables));

        assertThat(response.getStatusCode()).isEqualTo(200);

        assertThat(sharedDataDao.findAll()).isEmpty();
        assertThat(encryptionKeyDao.findAll()).isEmpty();
    }

    @Test
    public void delete_hasNoAccess() {
        EncryptionKey encryptionKey = EncryptionKey.builder()
            .externalId(EXTERNAL_ID)
            .dataType(DataType.TEST)
            .userId(UUID.randomUUID())
            .build();

        RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(encryptionKey)
            .put(UrlFactory.create(serverPort, Endpoints.ENCRYPTION_INTERNAL_CREATE_KEY, "accessMode", AccessMode.EDIT));

        SharedData sharedData = SharedData.builder()
            .externalId(EXTERNAL_ID)
            .dataType(DataType.TEST)
            .publicData(true)
            .accessMode(AccessMode.READ)
            .build();
        RequestFactory.createRequest()
            .body(sharedData)
            .put(UrlFactory.create(serverPort, Endpoints.ENCRYPTION_INTERNAL_CREATE_SHARED_DATA));

        Map<String, Object> pathVariables = CollectionUtils.toMap(
            new BiWrapper<>("dataType", DataType.TEST),
            new BiWrapper<>("externalId", EXTERNAL_ID),
            new BiWrapper<>("accessMode", AccessMode.EDIT)
        );

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .delete(UrlFactory.create(serverPort, Endpoints.ENCRYPTION_INTERNAL_DELETE_KEY, pathVariables));

        ErrorResponseValidator.verifyErrorResponse(response, HttpStatus.FORBIDDEN.value(), ErrorCode.FORBIDDEN_OPERATION);

        assertThat(sharedDataDao.findAll()).hasSize(1);
        assertThat(encryptionKeyDao.findAll()).hasSize(1);
    }

    @Test
    public void delete_hasAccess() {
        EncryptionKey encryptionKey = EncryptionKey.builder()
            .externalId(EXTERNAL_ID)
            .dataType(DataType.TEST)
            .userId(UUID.randomUUID())
            .build();

        RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(encryptionKey)
            .put(UrlFactory.create(serverPort, Endpoints.ENCRYPTION_INTERNAL_CREATE_KEY, "accessMode", AccessMode.EDIT));

        SharedData sharedData = SharedData.builder()
            .externalId(EXTERNAL_ID)
            .dataType(DataType.TEST)
            .publicData(true)
            .accessMode(AccessMode.EDIT)
            .build();
        RequestFactory.createRequest()
            .body(sharedData)
            .put(UrlFactory.create(serverPort, Endpoints.ENCRYPTION_INTERNAL_CREATE_SHARED_DATA));

        Map<String, Object> pathVariables = CollectionUtils.toMap(
            new BiWrapper<>("dataType", DataType.TEST),
            new BiWrapper<>("externalId", EXTERNAL_ID),
            new BiWrapper<>("accessMode", AccessMode.EDIT)
        );

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .delete(UrlFactory.create(serverPort, Endpoints.ENCRYPTION_INTERNAL_DELETE_KEY, pathVariables));


        assertThat(response.getStatusCode()).isEqualTo(200);

        assertThat(sharedDataDao.findAll()).isEmpty();
        assertThat(encryptionKeyDao.findAll()).isEmpty();
    }

    @Test
    public void get_notFound() {
        Map<String, Object> pathVariables = CollectionUtils.toMap(
            new BiWrapper<>("dataType", DataType.TEST),
            new BiWrapper<>("externalId", EXTERNAL_ID),
            new BiWrapper<>("accessMode", AccessMode.EDIT)
        );

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .get(UrlFactory.create(serverPort, Endpoints.ENCRYPTION_INTERNAL_GET_KEY, pathVariables));

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getBody().asString()).isEmpty();
    }

    @Test
    public void queryOwn() {
        EncryptionKey encryptionKey = EncryptionKey.builder()
            .externalId(EXTERNAL_ID)
            .dataType(DataType.TEST)
            .userId(USER_ID)
            .build();

        String encryptionKeyString = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(encryptionKey)
            .put(UrlFactory.create(serverPort, Endpoints.ENCRYPTION_INTERNAL_CREATE_KEY, "accessMode", AccessMode.EDIT))
            .getBody()
            .asString();

        SharedData sharedData = SharedData.builder()
            .externalId(EXTERNAL_ID)
            .dataType(DataType.TEST)
            .publicData(true)
            .accessMode(AccessMode.EDIT)
            .build();
        RequestFactory.createRequest()
            .body(sharedData)
            .put(UrlFactory.create(serverPort, Endpoints.ENCRYPTION_INTERNAL_CREATE_SHARED_DATA));

        Map<String, Object> pathVariables = CollectionUtils.toMap(
            new BiWrapper<>("dataType", DataType.TEST),
            new BiWrapper<>("externalId", EXTERNAL_ID),
            new BiWrapper<>("accessMode", AccessMode.EDIT)
        );

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .get(UrlFactory.create(serverPort, Endpoints.ENCRYPTION_INTERNAL_GET_KEY, pathVariables));

        assertThat(response.getStatusCode()).isEqualTo(200);

        assertThat(response.getBody().asString()).isEqualTo(encryptionKeyString);
    }

    @Test
    public void query_hasNoAccess() {
        EncryptionKey encryptionKey = EncryptionKey.builder()
            .externalId(EXTERNAL_ID)
            .dataType(DataType.TEST)
            .userId(UUID.randomUUID())
            .build();

        RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(encryptionKey)
            .put(UrlFactory.create(serverPort, Endpoints.ENCRYPTION_INTERNAL_CREATE_KEY, "accessMode", AccessMode.EDIT))
            .getBody()
            .asString();

        SharedData sharedData = SharedData.builder()
            .externalId(EXTERNAL_ID)
            .dataType(DataType.TEST)
            .publicData(true)
            .accessMode(AccessMode.READ)
            .build();
        RequestFactory.createRequest()
            .body(sharedData)
            .put(UrlFactory.create(serverPort, Endpoints.ENCRYPTION_INTERNAL_CREATE_SHARED_DATA));

        Map<String, Object> pathVariables = CollectionUtils.toMap(
            new BiWrapper<>("dataType", DataType.TEST),
            new BiWrapper<>("externalId", EXTERNAL_ID),
            new BiWrapper<>("accessMode", AccessMode.EDIT)
        );

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .get(UrlFactory.create(serverPort, Endpoints.ENCRYPTION_INTERNAL_GET_KEY, pathVariables));

        ErrorResponseValidator.verifyErrorResponse(response, HttpStatus.FORBIDDEN.value(), ErrorCode.FORBIDDEN_OPERATION);
    }

    @Test
    public void query_hasAccess() {
        EncryptionKey encryptionKey = EncryptionKey.builder()
            .externalId(EXTERNAL_ID)
            .dataType(DataType.TEST)
            .userId(UUID.randomUUID())
            .build();

        String encryptionKeyString = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(encryptionKey)
            .put(UrlFactory.create(serverPort, Endpoints.ENCRYPTION_INTERNAL_CREATE_KEY, "accessMode", AccessMode.EDIT))
            .getBody()
            .asString();

        SharedData sharedData = SharedData.builder()
            .externalId(EXTERNAL_ID)
            .dataType(DataType.TEST)
            .publicData(true)
            .accessMode(AccessMode.EDIT)
            .build();
        RequestFactory.createRequest()
            .body(sharedData)
            .put(UrlFactory.create(serverPort, Endpoints.ENCRYPTION_INTERNAL_CREATE_SHARED_DATA));

        Map<String, Object> pathVariables = CollectionUtils.toMap(
            new BiWrapper<>("dataType", DataType.TEST),
            new BiWrapper<>("externalId", EXTERNAL_ID),
            new BiWrapper<>("accessMode", AccessMode.EDIT)
        );

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .get(UrlFactory.create(serverPort, Endpoints.ENCRYPTION_INTERNAL_GET_KEY, pathVariables));

        assertThat(response.getStatusCode()).isEqualTo(200);

        assertThat(response.getBody().asString()).isEqualTo(encryptionKeyString);
    }
}