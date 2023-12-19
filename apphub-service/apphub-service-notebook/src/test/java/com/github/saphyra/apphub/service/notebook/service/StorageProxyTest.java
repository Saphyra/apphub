package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.api.platform.storage.client.StorageClient;
import com.github.saphyra.apphub.api.platform.storage.model.CreateFileRequest;
import com.github.saphyra.apphub.api.platform.storage.model.StoredFileResponse;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StorageProxyTest {
    private static final String FILE_NAME = "file-name";
    private static final Long SIZE = 2345L;
    private static final String LOCALE = "locale";
    private static final String ACCESS_TOKEN = "access-token";
    private static final UUID STORED_FILE_ID = UUID.randomUUID();
    private static final UUID NEW_FILE_ID = UUID.randomUUID();

    @Mock
    private StorageClient storageClient;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private LocaleProvider localeProvider;

    @InjectMocks
    private StorageProxy underTest;

    @Mock
    private StoredFileResponse storedFileResponse;

    @BeforeEach
    public void setUp() {
        given(localeProvider.getLocaleValidated()).willReturn(LOCALE);
        given(accessTokenProvider.getAsString()).willReturn(ACCESS_TOKEN);
    }

    @Test
    public void createFile() {
        given(storageClient.createFile(any(), eq(ACCESS_TOKEN), eq(LOCALE))).willReturn(STORED_FILE_ID);

        UUID result = underTest.createFile(FILE_NAME, SIZE);

        assertThat(result).isEqualTo(STORED_FILE_ID);

        ArgumentCaptor<CreateFileRequest> argumentCaptor = ArgumentCaptor.forClass(CreateFileRequest.class);
        verify(storageClient).createFile(argumentCaptor.capture(), eq(ACCESS_TOKEN), eq(LOCALE));

        CreateFileRequest request = argumentCaptor.getValue();
        assertThat(request.getFileName()).isEqualTo(FILE_NAME);
        assertThat(request.getSize()).isEqualTo(SIZE);
    }

    @Test
    public void deleteFile() {
        underTest.deleteFile(STORED_FILE_ID);

        verify(storageClient).deleteFile(STORED_FILE_ID, ACCESS_TOKEN, LOCALE);
    }

    @Test
    public void getFileMetadata() {
        given(storageClient.getFileMetadata(STORED_FILE_ID, ACCESS_TOKEN, LOCALE)).willReturn(storedFileResponse);

        StoredFileResponse result = underTest.getFileMetadata(STORED_FILE_ID);

        assertThat(result).isEqualTo(storedFileResponse);
    }

    @Test
    void metadataNotFound() {
        given(storageClient.getFileMetadata(STORED_FILE_ID, ACCESS_TOKEN, LOCALE)).willThrow(new TestFeignException(HttpStatus.NOT_FOUND));

        StoredFileResponse result = underTest.getFileMetadata(STORED_FILE_ID);

        assertThat(result.getFileUploaded()).isFalse();
    }

    @Test
    void randomException() {
        TestFeignException exception = new TestFeignException(HttpStatus.BAD_REQUEST);
        given(storageClient.getFileMetadata(STORED_FILE_ID, ACCESS_TOKEN, LOCALE)).willThrow(exception);

        Throwable ex = catchThrowable(() -> underTest.getFileMetadata(STORED_FILE_ID));

        assertThat(ex).isEqualTo(exception);
    }

    static class TestFeignException extends FeignException {
        TestFeignException(HttpStatus status) {
            super(status.value(), "asd");
        }
    }
}