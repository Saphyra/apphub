package com.github.saphyra.apphub.service.platform.encryption.shared_data;

import com.github.saphyra.apphub.api.platform.encryption.model.AccessMode;
import com.github.saphyra.apphub.api.platform.encryption.model.DataType;
import com.github.saphyra.apphub.api.platform.encryption.model.EncryptionKey;
import com.github.saphyra.apphub.api.platform.encryption.model.SharedData;
import com.github.saphyra.apphub.api.platform.web_content.client.LocalizationClient;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.converter.AccessTokenHeaderConverter;
import com.github.saphyra.apphub.lib.config.common.endpoints.EncryptionEndpoints;
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
public class SharedDataControllerImplItTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String LOCALIZED_MESSAGE = "localized-message";
    private static final UUID EXTERNAL_ID_1 = UUID.randomUUID();
    private static final UUID EXTERNAL_ID_2 = UUID.randomUUID();
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
        SharedData sharedData = SharedData.builder()
            .externalId(null)
            .dataType(DataType.TEST)
            .publicData(true)
            .accessMode(AccessMode.DELETE)
            .build();

        Response response = RequestFactory.createRequest()
            .body(sharedData)
            .put(UrlFactory.create(serverPort, EncryptionEndpoints.ENCRYPTION_INTERNAL_CREATE_SHARED_DATA));

        ErrorResponseValidator.verifyInvalidParam(response, "externalId", "must not be null");
    }

    @Test
    public void create_nullDataType() {
        SharedData sharedData = SharedData.builder()
            .externalId(EXTERNAL_ID_1)
            .dataType(null)
            .publicData(true)
            .accessMode(AccessMode.DELETE)
            .build();

        Response response = RequestFactory.createRequest()
            .body(sharedData)
            .put(UrlFactory.create(serverPort, EncryptionEndpoints.ENCRYPTION_INTERNAL_CREATE_SHARED_DATA));

        ErrorResponseValidator.verifyInvalidParam(response, "dataType", "must not be null");
    }

    @Test
    public void create_sharedWithAndPublicData() {
        SharedData sharedData = SharedData.builder()
            .externalId(EXTERNAL_ID_1)
            .dataType(DataType.TEST)
            .publicData(null)
            .accessMode(AccessMode.DELETE)
            .build();

        Response response = RequestFactory.createRequest()
            .body(sharedData)
            .put(UrlFactory.create(serverPort, EncryptionEndpoints.ENCRYPTION_INTERNAL_CREATE_SHARED_DATA));

        ErrorResponseValidator.verifyInvalidParam(response, "sharedWith, publicData", "all values are null");
    }

    @Test
    public void create_nullAccessMode() {
        SharedData sharedData = SharedData.builder()
            .externalId(EXTERNAL_ID_1)
            .dataType(DataType.TEST)
            .publicData(true)
            .accessMode(null)
            .build();

        Response response = RequestFactory.createRequest()
            .body(sharedData)
            .put(UrlFactory.create(serverPort, EncryptionEndpoints.ENCRYPTION_INTERNAL_CREATE_SHARED_DATA));

        ErrorResponseValidator.verifyInvalidParam(response, "accessMode", "must not be null");
    }

    @Test
    public void create_encryptionKeyNotFound() {
        SharedData sharedData = SharedData.builder()
            .externalId(EXTERNAL_ID_1)
            .dataType(DataType.TEST)
            .publicData(true)
            .accessMode(AccessMode.DELETE)
            .build();

        Response response = RequestFactory.createRequest()
            .body(sharedData)
            .put(UrlFactory.create(serverPort, EncryptionEndpoints.ENCRYPTION_INTERNAL_CREATE_SHARED_DATA));

        ErrorResponseValidator.verifyErrorResponse(response, HttpStatus.NOT_FOUND.value(), ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    public void create() {
        EncryptionKey encryptionKey = EncryptionKey.builder()
            .externalId(EXTERNAL_ID_1)
            .dataType(DataType.TEST)
            .userId(USER_ID)
            .build();

        RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(encryptionKey)
            .put(UrlFactory.create(serverPort, EncryptionEndpoints.ENCRYPTION_INTERNAL_CREATE_KEY, "accessMode", AccessMode.EDIT));

        SharedData sharedData = SharedData.builder()
            .externalId(EXTERNAL_ID_1)
            .dataType(DataType.TEST)
            .publicData(true)
            .accessMode(AccessMode.DELETE)
            .build();

        Response response = RequestFactory.createRequest()
            .body(sharedData)
            .put(UrlFactory.create(serverPort, EncryptionEndpoints.ENCRYPTION_INTERNAL_CREATE_SHARED_DATA));

        assertThat(response.getStatusCode()).isEqualTo(200);

        assertThat(sharedDataDao.findAll()).hasSize(1);
    }

    @Test
    public void getSharedData() {
        EncryptionKey encryptionKey = EncryptionKey.builder()
            .externalId(EXTERNAL_ID_1)
            .dataType(DataType.TEST)
            .userId(USER_ID)
            .build();

        RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(encryptionKey)
            .put(UrlFactory.create(serverPort, EncryptionEndpoints.ENCRYPTION_INTERNAL_CREATE_KEY, "accessMode", AccessMode.EDIT));

        SharedData sharedData = SharedData.builder()
            .externalId(EXTERNAL_ID_1)
            .dataType(DataType.TEST)
            .publicData(true)
            .accessMode(AccessMode.DELETE)
            .build();

        RequestFactory.createRequest()
            .body(sharedData)
            .put(UrlFactory.create(serverPort, EncryptionEndpoints.ENCRYPTION_INTERNAL_CREATE_SHARED_DATA));

        Map<String, Object> pathVariables = CollectionUtils.toMap(
            new BiWrapper<>("externalId", EXTERNAL_ID_1),
            new BiWrapper<>("dataType", DataType.TEST)
        );

        Response response = RequestFactory.createRequest()
            .get(UrlFactory.create(serverPort, EncryptionEndpoints.ENCRYPTION_INTERNAL_GET_SHARED_DATA, pathVariables));

        assertThat(response.getStatusCode()).isEqualTo(200);

        assertThat(response.getBody().as(SharedData[].class)).hasSize(1);
    }

    @Test
    public void clone_nullExternalId() {
        SharedData sharedData = SharedData.builder()
            .externalId(null)
            .dataType(DataType.TEST)
            .build();

        Map<String, Object> pathVariables = CollectionUtils.toMap(
            new BiWrapper<>("externalId", EXTERNAL_ID_1),
            new BiWrapper<>("dataType", DataType.TEST)
        );

        Response response = RequestFactory.createRequest()
            .body(sharedData)
            .put(UrlFactory.create(serverPort, EncryptionEndpoints.ENCRYPTION_INTERNAL_CLONE_SHARED_DATA, pathVariables));

        ErrorResponseValidator.verifyInvalidParam(response, "externalId", "must not be null");
    }

    @Test
    public void clone_nullDataType() {
        SharedData sharedData = SharedData.builder()
            .externalId(EXTERNAL_ID_2)
            .dataType(null)
            .build();

        Map<String, Object> pathVariables = CollectionUtils.toMap(
            new BiWrapper<>("externalId", EXTERNAL_ID_1),
            new BiWrapper<>("dataType", DataType.TEST)
        );

        Response response = RequestFactory.createRequest()
            .body(sharedData)
            .put(UrlFactory.create(serverPort, EncryptionEndpoints.ENCRYPTION_INTERNAL_CLONE_SHARED_DATA, pathVariables));

        ErrorResponseValidator.verifyInvalidParam(response, "dataType", "must not be null");
    }

    @Test
    public void cloneSharedData() {
        EncryptionKey encryptionKey = EncryptionKey.builder()
            .externalId(EXTERNAL_ID_1)
            .dataType(DataType.TEST)
            .userId(USER_ID)
            .build();

        RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(encryptionKey)
            .put(UrlFactory.create(serverPort, EncryptionEndpoints.ENCRYPTION_INTERNAL_CREATE_KEY, "accessMode", AccessMode.EDIT));

        SharedData sharedData = SharedData.builder()
            .externalId(EXTERNAL_ID_1)
            .dataType(DataType.TEST)
            .publicData(true)
            .accessMode(AccessMode.DELETE)
            .build();

        RequestFactory.createRequest()
            .body(sharedData)
            .put(UrlFactory.create(serverPort, EncryptionEndpoints.ENCRYPTION_INTERNAL_CREATE_SHARED_DATA));

        Map<String, Object> pathVariables = CollectionUtils.toMap(
            new BiWrapper<>("externalId", EXTERNAL_ID_1),
            new BiWrapper<>("dataType", DataType.TEST)
        );

        SharedData newSharedData = SharedData.builder()
            .externalId(EXTERNAL_ID_2)
            .dataType(DataType.TEST)
            .build();

        Response response = RequestFactory.createRequest()
            .body(newSharedData)
            .put(UrlFactory.create(serverPort, EncryptionEndpoints.ENCRYPTION_INTERNAL_CLONE_SHARED_DATA, pathVariables));

        assertThat(response.getStatusCode()).isEqualTo(200);

        assertThat(sharedDataDao.getByExternalIdAndDataType(EXTERNAL_ID_2, DataType.TEST)).hasSize(1);
    }

    @Test
    public void deleteSharedDataEntity() {
        EncryptionKey encryptionKey = EncryptionKey.builder()
            .externalId(EXTERNAL_ID_1)
            .dataType(DataType.TEST)
            .userId(USER_ID)
            .build();

        RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(encryptionKey)
            .put(UrlFactory.create(serverPort, EncryptionEndpoints.ENCRYPTION_INTERNAL_CREATE_KEY, "accessMode", AccessMode.EDIT));

        SharedData sharedData = SharedData.builder()
            .externalId(EXTERNAL_ID_1)
            .dataType(DataType.TEST)
            .publicData(true)
            .accessMode(AccessMode.DELETE)
            .build();

        RequestFactory.createRequest()
            .body(sharedData)
            .put(UrlFactory.create(serverPort, EncryptionEndpoints.ENCRYPTION_INTERNAL_CREATE_SHARED_DATA));

        UUID sharedDataId = sharedDataDao.findAll()
            .get(0)
            .getSharedDataId();

        Response response = RequestFactory.createRequest()
            .delete(UrlFactory.create(serverPort, EncryptionEndpoints.ENCRYPTION_INTERNAL_DELETE_SHARED_DATA_ENTITY, "sharedDataId", sharedDataId));

        assertThat(response.getStatusCode()).isEqualTo(200);

        assertThat(sharedDataDao.findAll()).isEmpty();
    }

    @Test
    public void deleteSharedData() {
        EncryptionKey encryptionKey = EncryptionKey.builder()
            .externalId(EXTERNAL_ID_1)
            .dataType(DataType.TEST)
            .userId(USER_ID)
            .build();

        RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(encryptionKey)
            .put(UrlFactory.create(serverPort, EncryptionEndpoints.ENCRYPTION_INTERNAL_CREATE_KEY, "accessMode", AccessMode.EDIT));

        SharedData sharedData = SharedData.builder()
            .externalId(EXTERNAL_ID_1)
            .dataType(DataType.TEST)
            .publicData(true)
            .accessMode(AccessMode.DELETE)
            .build();

        RequestFactory.createRequest()
            .body(sharedData)
            .put(UrlFactory.create(serverPort, EncryptionEndpoints.ENCRYPTION_INTERNAL_CREATE_SHARED_DATA));

        Map<String, Object> pathVariables = CollectionUtils.toMap(
            new BiWrapper<>("externalId", EXTERNAL_ID_1),
            new BiWrapper<>("dataType", DataType.TEST)
        );

        Response response = RequestFactory.createRequest()
            .delete(UrlFactory.create(serverPort, EncryptionEndpoints.ENCRYPTION_INTERNAL_DELETE_SHARED_DATA, pathVariables));

        assertThat(response.getStatusCode()).isEqualTo(200);

        assertThat(sharedDataDao.findAll()).isEmpty();
    }
}