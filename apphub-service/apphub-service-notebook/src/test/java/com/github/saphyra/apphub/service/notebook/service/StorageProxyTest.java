package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.api.platform.storage.client.StorageClient;
import com.github.saphyra.apphub.api.platform.storage.model.CreateFileRequest;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class StorageProxyTest {
    private static final String FILE_NAME = "file-name";
    private static final String EXTENSION = "extension";
    private static final Long SIZE = 2345L;
    private static final String LOCALE = "locale";
    private static final String ACCESS_TOKEN = "access-token";
    private static final UUID FILE_ID = UUID.randomUUID();
    private static final UUID NEW_FILE_ID = UUID.randomUUID();

    @Mock
    private StorageClient storageClient;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private LocaleProvider localeProvider;

    @InjectMocks
    private StorageProxy underTest;

    @Before
    public void setUp() {
        given(localeProvider.getLocaleValidated()).willReturn(LOCALE);
        given(accessTokenProvider.getAsString()).willReturn(ACCESS_TOKEN);
    }

    @Test
    public void createFile() {
        given(storageClient.createFile(any(), eq(ACCESS_TOKEN), eq(LOCALE))).willReturn(FILE_ID);

        UUID result = underTest.createFile(FILE_NAME, EXTENSION, SIZE);

        assertThat(result).isEqualTo(FILE_ID);

        ArgumentCaptor<CreateFileRequest> argumentCaptor = ArgumentCaptor.forClass(CreateFileRequest.class);
        verify(storageClient).createFile(argumentCaptor.capture(), eq(ACCESS_TOKEN), eq(LOCALE));

        CreateFileRequest request = argumentCaptor.getValue();
        assertThat(request.getFileName()).isEqualTo(FILE_NAME);
        assertThat(request.getExtension()).isEqualTo(EXTENSION);
        assertThat(request.getSize()).isEqualTo(SIZE);
    }

    @Test
    public void deleteFile() {
        underTest.deleteFile(FILE_ID);

        verify(storageClient).deleteFile(FILE_ID, ACCESS_TOKEN, LOCALE);
    }

    @Test
    public void duplicateFile() {
        given(storageClient.duplicateFile(FILE_ID, ACCESS_TOKEN, LOCALE)).willReturn(NEW_FILE_ID);

        UUID result = underTest.duplicateFile(FILE_ID);

        assertThat(result).isEqualTo(NEW_FILE_ID);
    }
}