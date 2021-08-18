package com.github.saphyra.apphub.service.notebook.dao.content;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContentConverter extends ConverterBase<ContentEntity, Content> {
    private final AccessTokenProvider accessTokenProvider;
    private final StringEncryptor stringEncryptor;
    private final UuidConverter uuidConverter;

    @Override
    protected Content processEntityConversion(ContentEntity entity) {
        return Content.builder()
            .contentId(uuidConverter.convertEntity(entity.getContentId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .parent(uuidConverter.convertEntity(entity.getParent()))
            .listItemId(uuidConverter.convertEntity(entity.getListItemId()))
            .content(stringEncryptor.decryptEntity(entity.getContent(), uuidConverter.convertDomain(accessTokenProvider.get().getUserId())))
            .build();
    }

    @Override
    protected ContentEntity processDomainConversion(Content content) {
        return ContentEntity.builder()
            .contentId(uuidConverter.convertDomain(content.getContentId()))
            .userId(uuidConverter.convertDomain(content.getUserId()))
            .parent(uuidConverter.convertDomain(content.getParent()))
            .listItemId(uuidConverter.convertDomain(content.getListItemId()))
            .content(stringEncryptor.encryptEntity(content.getContent(), uuidConverter.convertDomain(accessTokenProvider.get().getUserId())))
            .build();
    }
}
