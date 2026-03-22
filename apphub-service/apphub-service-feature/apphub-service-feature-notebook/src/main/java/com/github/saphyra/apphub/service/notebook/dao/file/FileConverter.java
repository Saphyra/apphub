package com.github.saphyra.apphub.service.notebook.dao.file;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class FileConverter extends ConverterBase<FileEntity, File> {
    private final UuidConverter uuidConverter;

    @Override
    protected FileEntity processDomainConversion(File domain) {
        return FileEntity.builder()
            .fileId(uuidConverter.convertDomain(domain.getFileId()))
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .parent(uuidConverter.convertDomain(domain.getParent()))
            .storedFileId(uuidConverter.convertDomain(domain.getStoredFileId()))
            .build();
    }

    @Override
    protected File processEntityConversion(FileEntity entity) {
        return File.builder()
            .fileId(uuidConverter.convertEntity(entity.getFileId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .parent(uuidConverter.convertEntity(entity.getParent()))
            .storedFileId(uuidConverter.convertEntity(entity.getStoredFileId()))
            .build();
    }
}
