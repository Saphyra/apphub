package com.github.saphyra.apphub.service.notebook.service.table.column_data;

import com.github.saphyra.apphub.api.feature.notebook.model.request.FileMetadata;
import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.notebook.service.StorageProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class FileColumnDataService implements ColumnDataService {
    private final ObjectMapper objectMapper;
    private final StorageProxy storageProxy;

    @Override
    public boolean canProcess(ColumnType type) {
        return type.isFile();
    }

    @Override
    public Object deserialize(String data) {
        return objectMapper.readValue(data, FileMetadata.class);
    }

    @Override
    public void validateData(Object data) {
        if (isNull(data)) {
            return;
        }
        FileMetadata request = ValidationUtil.parse(data, (d) -> objectMapper.convertValue(d, FileMetadata.class), "fileMetadata");

        if (isNull(request.getStoredFileId())) {
            ValidationUtil.notNull(request.getFileName(), "fileName");
            ValidationUtil.notNull(request.getSize(), "size");
        }
    }

    @Override
    public void deleteData(String data) {
        FileMetadata fileMetadata = objectMapper.readValue(data, FileMetadata.class);

        storageProxy.deleteFile(fileMetadata.getStoredFileId());
    }

    @Override
    public Optional<BiWrapper<String, Optional<UUID>>> serialize(Object data) {
        FileMetadata fileMetadata = objectMapper.convertValue(data, FileMetadata.class);

        return Optional.ofNullable(fileMetadata.getStoredFileId())
            .map(storedFileId -> FileMetadata.builder()
                .storedFileId(storedFileId)
                .build())
            .or(() -> Optional.of(FileMetadata.builder()
                .storedFileId(storageProxy.createFile(fileMetadata.getFileName(), fileMetadata.getSize()))
                .build()))
            .map(metadata -> new BiWrapper<>(objectMapper.writeValueAsString(metadata), Optional.of(metadata.getStoredFileId())));
    }
}
