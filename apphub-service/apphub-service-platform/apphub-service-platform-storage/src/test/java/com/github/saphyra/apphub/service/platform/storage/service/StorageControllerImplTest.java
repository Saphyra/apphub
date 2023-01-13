package com.github.saphyra.apphub.service.platform.storage.service;

import com.github.saphyra.apphub.api.platform.storage.model.CreateFileRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.platform.storage.service.store.StoreFileService;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class StorageControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String FILE_NAME = "file-name";
    private static final String EXTENSION = "extension";
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
    private DuplicateFileService duplicateFileService;

    @Mock
    private FileUploadHelper fileUploadHelper;

    @InjectMocks
    private StorageControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ServletFileUpload servletFileUpload;

    @Mock
    private FileItemIterator fileItemIterator;

    @Mock
    private FileItemStream formFieldStream;

    @Mock
    private FileItemStream notFormFieldStream;

    @Mock
    private InputStream inputStream;

    @Before
    public void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    public void createFile() {
        CreateFileRequest request = CreateFileRequest.builder()
            .fileName(FILE_NAME)
            .extension(EXTENSION)
            .size(SIZE)
            .build();

        given(storeFileService.createFile(USER_ID, FILE_NAME, EXTENSION, SIZE)).willReturn(STORED_FILE_ID);

        UUID result = underTest.createFile(request, accessTokenHeader);

        assertThat(result).isEqualTo(STORED_FILE_ID);
    }

    @SuppressWarnings("resource")
    @Test
    public void uploadFile() throws IOException, FileUploadException {
        given(fileUploadHelper.servletFileUpload()).willReturn(servletFileUpload);
        given(servletFileUpload.getItemIterator(request)).willReturn(fileItemIterator);

        given(fileItemIterator.hasNext())
            .willReturn(true)
            .willReturn(true)
            .willReturn(false);
        given(fileItemIterator.next())
            .willReturn(formFieldStream)
            .willReturn(notFormFieldStream);

        given(formFieldStream.isFormField()).willReturn(true);
        given(notFormFieldStream.isFormField()).willReturn(false);

        given(notFormFieldStream.openStream()).willReturn(inputStream);

        underTest.uploadFile(STORED_FILE_ID, request, accessTokenHeader);

        verify(formFieldStream, times(0)).openStream();
        verify(storeFileService).uploadFile(USER_ID, STORED_FILE_ID, inputStream);
    }

    @Test
    public void deleteFile() {
        underTest.deleteFile(STORED_FILE_ID, accessTokenHeader);

        verify(deleteFileService).deleteFile(USER_ID, STORED_FILE_ID);
    }

    @Test
    public void duplicateFile() {
        given(duplicateFileService.duplicateFile(USER_ID, STORED_FILE_ID)).willReturn(NEW_STORED_FILE_ID);

        UUID result = underTest.duplicateFile(STORED_FILE_ID, accessTokenHeader);

        assertThat(result).isEqualTo(NEW_STORED_FILE_ID);
    }
}