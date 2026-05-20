package com.github.saphyra.apphub.service.notebook.service.table.deprecated_column_data;

import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_file.FileDao;
import com.github.saphyra.apphub.service.notebook.service.table.deprecated_column_data.base.file.FileBasedColumnDataService;
import com.github.saphyra.apphub.service.notebook.service.table.deprecated_column_data.base.file.FileBasedColumnProxy;
import com.github.saphyra.apphub.service.notebook.service.validator.FileMetadataValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@Slf4j
@Deprecated(forRemoval = true)
class FileColumnDataService extends FileBasedColumnDataService {
    FileColumnDataService(FileBasedColumnProxy proxy, FileDao fileDao, ObjectMapper objectMapper, FileMetadataValidator fileMetadataValidator) {
        super(ColumnType.FILE, proxy, fileDao, objectMapper, fileMetadataValidator);
    }
}
