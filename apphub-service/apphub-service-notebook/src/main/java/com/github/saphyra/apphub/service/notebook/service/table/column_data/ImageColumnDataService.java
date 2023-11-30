package com.github.saphyra.apphub.service.notebook.service.table.column_data;

import com.github.saphyra.apphub.api.notebook.model.request.FileMetadata;
import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.notebook.dao.file.FileDao;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.base.file.FileBasedColumnDataService;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.base.file.FileBasedColumnProxy;
import com.github.saphyra.apphub.service.notebook.service.validator.FileMetadataValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@Slf4j
//TODO unit test
class ImageColumnDataService extends FileBasedColumnDataService {
    private final ObjectMapperWrapper objectMapperWrapper;
    private final FileMetadataValidator fileMetadataValidator;

    ImageColumnDataService(FileBasedColumnProxy proxy, FileDao fileDao, ObjectMapperWrapper objectMapperWrapper, FileMetadataValidator fileMetadataValidator) {
        super(ColumnType.IMAGE, proxy, fileDao);
        this.objectMapperWrapper = objectMapperWrapper;
        this.fileMetadataValidator = fileMetadataValidator;
    }

    @Override
    public void validateData(Object data) {
        if (isNull(data)) {
            return;
        }
        FileMetadata request = ValidationUtil.parse(data, (d) -> objectMapperWrapper.convertValue(d, FileMetadata.class), "fileMetadata");

        fileMetadataValidator.validate(request);
    }
}
