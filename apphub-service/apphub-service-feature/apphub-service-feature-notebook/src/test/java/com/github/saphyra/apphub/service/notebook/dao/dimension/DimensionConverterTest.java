package com.github.saphyra.apphub.service.notebook.dao.dimension;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.IntegerEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionConverter.COLUMN_INDEX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class DimensionConverterTest {
    private static final UUID DIMENSION_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final Integer INDEX = 42;
    private static final String DIMENSION_ID_STRING = "dimension-id";
    private static final String USER_ID_STRING = "user-id";
    private static final String EXTERNAL_REFERENCE_STRING = "external-reference";
    private static final String USER_ID_FROM_ACCESS_TOKEN = "user-id-from-access-token";
    private static final String ENCRYPTED_INDEX = "encrypted-index";

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private IntegerEncryptor integerEncryptor;

    @InjectMocks
    private DimensionConverter underTest;

    @Test
    void convertDomain() {
        Dimension domain = Dimension.builder()
            .dimensionId(DIMENSION_ID)
            .userId(USER_ID)
            .externalReference(EXTERNAL_REFERENCE)
            .index(INDEX)
            .build();
        given(uuidConverter.convertDomain(DIMENSION_ID)).willReturn(DIMENSION_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(EXTERNAL_REFERENCE)).willReturn(EXTERNAL_REFERENCE_STRING);
        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID_FROM_ACCESS_TOKEN);
        given(integerEncryptor.encrypt(INDEX, USER_ID_FROM_ACCESS_TOKEN, DIMENSION_ID_STRING, COLUMN_INDEX)).willReturn(ENCRYPTED_INDEX);

        DimensionEntity result = underTest.convertDomain(domain);

        assertThat(result.getDimensionId()).isEqualTo(DIMENSION_ID_STRING);
        assertThat(result.getUserId()).isEqualTo(USER_ID_STRING);
        assertThat(result.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE_STRING);
        assertThat(result.getIndex()).isEqualTo(ENCRYPTED_INDEX);
    }

    @Test
    void convertEntity() {
        DimensionEntity entity = DimensionEntity.builder()
            .dimensionId(DIMENSION_ID_STRING)
            .userId(USER_ID_STRING)
            .externalReference(EXTERNAL_REFERENCE_STRING)
            .index(ENCRYPTED_INDEX)
            .build();
        given(uuidConverter.convertEntity(DIMENSION_ID_STRING)).willReturn(DIMENSION_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(uuidConverter.convertEntity(EXTERNAL_REFERENCE_STRING)).willReturn(EXTERNAL_REFERENCE);
        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID_FROM_ACCESS_TOKEN);
        given(integerEncryptor.decrypt(ENCRYPTED_INDEX, USER_ID_FROM_ACCESS_TOKEN, DIMENSION_ID_STRING, COLUMN_INDEX)).willReturn(INDEX);

        Dimension result = underTest.convertEntity(entity);

        assertThat(result.getDimensionId()).isEqualTo(DIMENSION_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE);
        assertThat(result.getIndex()).isEqualTo(INDEX);
    }
}