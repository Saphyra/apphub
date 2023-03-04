package com.github.saphyra.apphub.service.platform.encryption.common;

import com.github.saphyra.apphub.api.platform.encryption.model.AccessMode;
import com.github.saphyra.apphub.api.platform.encryption.model.DataType;
import com.github.saphyra.apphub.api.platform.encryption.model.EncryptionKey;
import com.github.saphyra.apphub.api.platform.encryption.model.SharedData;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import com.github.saphyra.apphub.service.platform.encryption.encryption_key.dao.EncryptionKeyDao;
import com.github.saphyra.apphub.service.platform.encryption.shared_data.dao.SharedDataDao;
import com.github.saphyra.apphub.test.common.api.ApiTestConfiguration;
import com.github.saphyra.apphub.test.common.rest_assured.RequestFactory;
import com.github.saphyra.apphub.test.common.rest_assured.UrlFactory;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(classes = ApiTestConfiguration.class)
public class EncryptionEventControllerImplItTest {
    private static final UUID EXTERNAL_ID_1 = UUID.randomUUID();
    private static final UUID EXTERNAL_ID_2 = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String ENCRYPTION_KEY = "encryption-key";
    private static final UUID SHARED_DATA_ID_1 = UUID.randomUUID();
    private static final UUID SHARED_DATA_ID_2 = UUID.randomUUID();

    @LocalServerPort
    private int serverPort;

    @Autowired
    private EncryptionKeyDao encryptionKeyDao;

    @Autowired
    private SharedDataDao sharedDataDao;

    @Test
    public void processUserDeletedEvent() {
        EncryptionKey encryptionKey = EncryptionKey.builder()
            .externalId(EXTERNAL_ID_1)
            .dataType(DataType.TEST)
            .userId(USER_ID)
            .encryptionKey(ENCRYPTION_KEY)
            .build();
        encryptionKeyDao.save(encryptionKey);

        SharedData publicSharedData = SharedData.builder()
            .sharedDataId(SHARED_DATA_ID_1)
            .externalId(EXTERNAL_ID_1)
            .dataType(DataType.TEST)
            .publicData(true)
            .accessMode(AccessMode.EDIT)
            .build();
        sharedDataDao.save(publicSharedData);

        SharedData sharedData = SharedData.builder()
            .sharedDataId(SHARED_DATA_ID_2)
            .externalId(EXTERNAL_ID_2)
            .dataType(DataType.TEST)
            .sharedWith(USER_ID)
            .accessMode(AccessMode.EDIT)
            .build();
        sharedDataDao.save(sharedData);

        SendEventRequest<DeleteAccountEvent> sendEventRequest = SendEventRequest.<DeleteAccountEvent>builder()
            .payload(new DeleteAccountEvent(USER_ID))
            .build();

        Response response = RequestFactory.createRequest()
            .body(sendEventRequest)
            .post(UrlFactory.create(serverPort, Endpoints.EVENT_DELETE_ACCOUNT));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        assertThat(encryptionKeyDao.findAll()).isEmpty();
        assertThat(sharedDataDao.findAll()).isEmpty();
    }
}