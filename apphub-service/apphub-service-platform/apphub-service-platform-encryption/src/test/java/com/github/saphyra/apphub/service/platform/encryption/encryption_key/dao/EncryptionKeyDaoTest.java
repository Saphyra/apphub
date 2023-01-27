package com.github.saphyra.apphub.service.platform.encryption.encryption_key.dao;

import com.github.saphyra.apphub.api.platform.encryption.model.DataType;
import com.github.saphyra.apphub.api.platform.encryption.model.EncryptionKey;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class EncryptionKeyDaoTest {
    private static final UUID EXTERNAL_ID = UUID.randomUUID();
    private static final String EXTERNAL_ID_STRING = "external-id";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private EncryptionKeyConverter converter;

    @Mock
    private EncryptionKeyRepository repository;

    @InjectMocks
    private EncryptionKeyDao underTest;

    @Mock
    private EncryptionKey encryptionKey;

    @Mock
    private EncryptionKeyEntity entity;

    @Test
    public void findById() {
        given(uuidConverter.convertDomain(EXTERNAL_ID)).willReturn(EXTERNAL_ID_STRING);
        given(repository.findById(new EncryptionKeyPk(EXTERNAL_ID_STRING, DataType.TEST.name()))).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(encryptionKey));

        Optional<EncryptionKey> result = underTest.findById(EXTERNAL_ID, DataType.TEST);

        assertThat(result).contains(encryptionKey);
    }

    @Test
    public void getByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(repository.getByUserId(USER_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(encryptionKey));

        List<EncryptionKey> result = underTest.getByUserId(USER_ID);

        assertThat(result).containsExactly(encryptionKey);
    }
}