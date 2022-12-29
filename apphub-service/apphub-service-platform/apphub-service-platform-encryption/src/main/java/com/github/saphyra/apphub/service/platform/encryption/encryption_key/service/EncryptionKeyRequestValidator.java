package com.github.saphyra.apphub.service.platform.encryption.encryption_key.service;

import com.github.saphyra.apphub.api.platform.encryption.model.EncryptionKey;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class EncryptionKeyRequestValidator {
    public void validate(EncryptionKey request) {
        ValidationUtil.notNull(request.getExternalId(), "externalId");
        ValidationUtil.notNull(request.getDataType(), "dataType");
        ValidationUtil.notNull(request.getUserId(), "userId");
    }
}
