package com.github.saphyra.apphub.service.notebook.dao.text;

import com.github.saphyra.apphub.lib.common_util.UuidConverter;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.converter.ConverterBase;
import com.github.saphyra.encryption.impl.StringEncryptor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TextConverter extends ConverterBase<TextEntity, Text> {
    private final AccessTokenProvider accessTokenProvider;
    private final StringEncryptor stringEncryptor;
    private final UuidConverter uuidConverter;

    @Override
    protected Text processEntityConversion(TextEntity entity) {
        return Text.builder()
            .textId(uuidConverter.convertEntity(entity.getTextId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .parent(uuidConverter.convertEntity(entity.getParent()))
            .content(stringEncryptor.decryptEntity(entity.getContent(), uuidConverter.convertDomain(accessTokenProvider.get().getUserId())))
            .build();
    }

    @Override
    protected TextEntity processDomainConversion(Text text) {
        return TextEntity.builder()
            .textId(uuidConverter.convertDomain(text.getTextId()))
            .userId(uuidConverter.convertDomain(text.getUserId()))
            .parent(uuidConverter.convertDomain(text.getParent()))
            .content(stringEncryptor.encryptEntity(text.getContent(), uuidConverter.convertDomain(accessTokenProvider.get().getUserId())))
            .build();
    }
}
