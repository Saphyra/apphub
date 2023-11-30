package com.github.saphyra.apphub.service.notebook.service.table.column_data.base.file;

import com.github.saphyra.apphub.service.notebook.dao.file.File;
import com.github.saphyra.apphub.service.notebook.dao.file.FileDao;
import com.github.saphyra.apphub.service.notebook.service.StorageProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class FileDeleter {
    private final FileDao fileDao;
    private final StorageProxy storageProxy;

    void deleteFile(UUID columnId) {
        File file = fileDao.findByParentValidated(columnId);
        storageProxy.deleteFile(file.getStoredFileId());
        fileDao.delete(file);
    }
}
