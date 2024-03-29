package com.github.saphyra.apphub.service.platform.storage.service;

import com.github.saphyra.apphub.api.platform.storage.model.CreateFileRequest;
import com.github.saphyra.apphub.api.platform.storage.model.StoredFileResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.platform.storage.service.store.StoreFileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StorageControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String FILE_NAME = "file-name";
    private static final Long SIZE = 234L;
    private static final UUID STORED_FILE_ID = UUID.randomUUID();
    private static final UUID NEW_STORED_FILE_ID = UUID.randomUUID();

    @Mock
    private StoreFileService storeFileService;

    @Mock
    private DownloadFileService downloadFileService;

    @Mock
    private DeleteFileService deleteFileService;

    @Mock
    private StoredFileMetadataQueryService metadataQueryService;

    @InjectMocks
    private StorageControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private MultipartFile multipartFile;

    @Mock
    private InputStream inputStream;

    @Mock
    private StoredFileResponse storedFileResponse;

    @BeforeEach
    public void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    public void createFile() {
        CreateFileRequest request = CreateFileRequest.builder()
            .fileName(FILE_NAME)
            .size(SIZE)
            .build();

        given(storeFileService.createFile(USER_ID, FILE_NAME, SIZE)).willReturn(STORED_FILE_ID);

        UUID result = underTest.createFile(request, accessTokenHeader);

        assertThat(result).isEqualTo(STORED_FILE_ID);
    }

    @Test
    @Disabled
    public void uploadFile() throws IOException {
        given(multipartFile.getSize()).willReturn(SIZE);
        given(multipartFile.getInputStream()).willReturn(inputStream);

        underTest.uploadFile(STORED_FILE_ID, multipartFile, accessTokenHeader);

        verify(storeFileService).uploadFile(USER_ID, STORED_FILE_ID, inputStream, SIZE);
    }

    @Test
    public void deleteFile() {
        underTest.deleteFile(STORED_FILE_ID, accessTokenHeader);

        verify(deleteFileService).deleteFile(USER_ID, STORED_FILE_ID);
    }

    @Test
    public void getFileMetadata() {
        given(metadataQueryService.getMetadata(USER_ID, STORED_FILE_ID)).willReturn(storedFileResponse);

        StoredFileResponse result = underTest.getFileMetadata(STORED_FILE_ID, accessTokenHeader);

        assertThat(result).isEqualTo(storedFileResponse);
    }
}