package com.github.saphyra.apphub.service.notebook.dao.image;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class ImageConverter extends ConverterBase<ImageEntity, Image> {
    private final UuidConverter uuidConverter;

    @Override
    protected ImageEntity processDomainConversion(Image domain) {
        return ImageEntity.builder()
            .imageId(uuidConverter.convertDomain(domain.getImageId()))
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .parent(uuidConverter.convertDomain(domain.getParent()))
            .fileId(uuidConverter.convertDomain(domain.getFileId()))
            .build();
    }

    @Override
    protected Image processEntityConversion(ImageEntity entity) {
        return Image.builder()
            .imageId(uuidConverter.convertEntity(entity.getImageId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .parent(uuidConverter.convertEntity(entity.getParent()))
            .fileId(uuidConverter.convertEntity(entity.getFileId()))
            .build();
    }
}
