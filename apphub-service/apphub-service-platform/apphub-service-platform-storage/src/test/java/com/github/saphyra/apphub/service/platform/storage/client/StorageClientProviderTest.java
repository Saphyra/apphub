package com.github.saphyra.apphub.service.platform.storage.client;

import com.github.saphyra.apphub.service.platform.storage.dao.stored_file.Storage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StorageClientProviderTest {
    @Mock
    private StorageClient storageClient;

    private StorageClientProvider underTest;

    @BeforeEach
    void setUp() {
        underTest = new StorageClientProvider(List.of(storageClient));
    }

    @Test
    void getClientForType() {
        given(storageClient.getType()).willReturn(Storage.FTP);

        assertThat(underTest.getClientForType(Storage.FTP)).isEqualTo(storageClient);
    }

    @Test
    void noClientForType() {
        given(storageClient.getType()).willReturn(Storage.FTP);

        assertThat(catchThrowable(() -> underTest.getClientForType(Storage.S3))).isInstanceOf(IllegalArgumentException.class);
    }
}