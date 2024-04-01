package com.github.saphyra.apphub.api.platform.encryption.server;

import com.github.saphyra.apphub.api.platform.encryption.model.DataType;
import com.github.saphyra.apphub.api.platform.encryption.model.EncryptionKey;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

public interface EncryptionKeyApiController {
    /**
     * Get encryption key for existing data, or creating a new one if not exists for a newly created record.
     */
    @PostMapping(Endpoints.ENCRYPTION_INTERNAL_GET_OR_CREATE_KEY)
    EncryptionKey getOrCreateEncryptionKey(@RequestBody EncryptionKey request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    /**
     * Data was deleted. The corresponding encryption key needs to be deleted.
     */
    @DeleteMapping(Endpoints.ENCRYPTION_INTERNAL_DELETE_KEY)
    void deleteEncryptionKey(@PathVariable("dataType") DataType dataType, @PathVariable("externalId") UUID externalId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
