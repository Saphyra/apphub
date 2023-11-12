package com.github.saphyra.apphub.lib.encryption.impl;

import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.collection.StringStringMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.github.saphyra.apphub.lib.encryption.impl.RevealingStringEncryptor.ENCRYPTED;
import static com.github.saphyra.apphub.lib.encryption.impl.RevealingStringEncryptor.RAW;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class RevealingStringEncryptorTest {
    private static final String ENTITY = "entity";
    private static final String KEY = "key";
    private static final String ENCRYPTED_ENTITY = "encrypted-entity";
    private static final String SERIALIZED = "serialized";

    @Mock
    private DefaultStringEncryptor defaultStringEncryptor;

    @Mock
    private ObjectMapperWrapper objectMapperWrapper;

    @InjectMocks
    private RevealingStringEncryptor underTest;

    @Test
    void encrypt() {
        given(defaultStringEncryptor.encryptEntity(ENTITY, KEY)).willReturn(ENCRYPTED_ENTITY);
        given(objectMapperWrapper.writeValueAsString(any(StringStringMap.class))).willReturn(SERIALIZED);

        assertThat(underTest.encrypt(ENTITY, KEY)).isEqualTo(SERIALIZED);

        ArgumentCaptor<StringStringMap> argumentCaptor = ArgumentCaptor.forClass(StringStringMap.class);
        then(objectMapperWrapper).should().writeValueAsString(argumentCaptor.capture());
        StringStringMap map = argumentCaptor.getValue();
        assertThat(map).hasSize(3);
        assertThat(map).containsEntry(ENCRYPTED, ENCRYPTED_ENTITY);
        assertThat(map).containsEntry(RAW, ENTITY);
        assertThat(map).containsEntry(RevealingStringEncryptor.KEY, KEY);
    }

    @Test
    void decrypt() {
        given(objectMapperWrapper.readValue(SERIALIZED, StringStringMap.class)).willReturn(CollectionUtils.singleValueMap(ENCRYPTED, ENCRYPTED_ENTITY, new StringStringMap()));
        given(defaultStringEncryptor.decryptEntity(ENCRYPTED_ENTITY, KEY)).willReturn(ENTITY);

        assertThat(underTest.decrypt(SERIALIZED, KEY)).isEqualTo(ENTITY);
    }
}