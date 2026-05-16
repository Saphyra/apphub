package com.github.saphyra.apphub.service.platform.storage.dao;

import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.platform.storage.dao.deprecated.DeprecatedStoredFileDao;
import com.github.saphyra.apphub.service.platform.storage.dao.deprecated.StoredFileView;
import com.github.saphyra.apphub.service.platform.storage.dao.stored_file.Storage;
import com.github.saphyra.apphub.service.platform.storage.dao.stored_file.StoredFile;
import com.github.saphyra.apphub.service.platform.storage.dao.stored_file.StoredFileDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

@ExtendWith(MockitoExtension.class)
class StoredFileMigrationServiceTest {
    private static final String STORED_FILE_ID_STRING = "stored-file-id";
    private static final String USER_ID_STRING = "user-id";
    private static final UUID STORED_FILE_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private DeprecatedStoredFileDao deprecatedStoredFileDao;

    @Mock
    private StoredFileDao storedFileDao;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private ErrorReporterService errorReporterService;

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private StoredFileMigrationService underTest;

    @Mock
    private StoredFile storedFile;

    @Mock
    private AutoCloseable autoCloseable;

    @Test
    void migrate_success() throws Exception {
        StoredFileView view = StoredFileView.builder()
            .storedFileId(STORED_FILE_ID_STRING)
            .userId(USER_ID_STRING)
            .build();

        given(deprecatedStoredFileDao.getViewsByStorage(Storage.FTP)).willReturn(List.of(view));
        given(deprecatedStoredFileDao.getViewsByStorage(Storage.S3)).willReturn(List.of());
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(uuidConverter.convertEntity(STORED_FILE_ID_STRING)).willReturn(STORED_FILE_ID);
        given(accessTokenProvider.set(AccessToken.builder().userId(USER_ID).build())).willReturn(autoCloseable);
        given(deprecatedStoredFileDao.findByIdValidated(STORED_FILE_ID)).willReturn(storedFile);

        underTest.migrate();

        then(storedFileDao).should().save(storedFile);
        then(deprecatedStoredFileDao).should().deleteById(STORED_FILE_ID_STRING);
        then(autoCloseable).should().close();
    }

    @Test
    void migrate_exception() {
        StoredFileView view = StoredFileView.builder()
            .storedFileId(STORED_FILE_ID_STRING)
            .userId(USER_ID_STRING)
            .build();

        RuntimeException exception = new RuntimeException("test-error");

        given(deprecatedStoredFileDao.getViewsByStorage(Storage.FTP)).willReturn(List.of(view));
        given(deprecatedStoredFileDao.getViewsByStorage(Storage.S3)).willReturn(List.of());
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(uuidConverter.convertEntity(STORED_FILE_ID_STRING)).willReturn(STORED_FILE_ID);
        given(accessTokenProvider.set(AccessToken.builder().userId(USER_ID).build())).willReturn(autoCloseable);
        given(deprecatedStoredFileDao.findByIdValidated(STORED_FILE_ID)).willReturn(storedFile);
        willThrow(exception).given(storedFileDao).save(storedFile);

        underTest.migrate();

        then(errorReporterService).should().report(anyString(), eq(exception));
        then(deprecatedStoredFileDao).shouldHaveNoMoreInteractions();
    }
}