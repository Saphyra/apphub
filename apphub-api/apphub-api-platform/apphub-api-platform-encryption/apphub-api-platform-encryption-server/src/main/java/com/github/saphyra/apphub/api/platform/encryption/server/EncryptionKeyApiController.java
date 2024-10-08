package com.github.saphyra.apphub.api.platform.encryption.server;

import com.github.saphyra.apphub.api.platform.encryption.model.AccessMode;
import com.github.saphyra.apphub.api.platform.encryption.model.DataType;
import com.github.saphyra.apphub.api.platform.encryption.model.EncryptionKey;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.common.endpoints.EncryptionEndpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

public interface EncryptionKeyApiController {
    /**
     * A new data was created. A corresponding encryption key needs to be created.
     */
    @PutMapping(EncryptionEndpoints.ENCRYPTION_INTERNAL_CREATE_KEY)
    String createEncryptionKey(@RequestBody EncryptionKey request, @PathVariable("accessMode") AccessMode accessMode, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    /**
     * Data was deleted. The corresponding encryption key needs to be deleted.
     */
    @DeleteMapping(EncryptionEndpoints.ENCRYPTION_INTERNAL_DELETE_KEY)
    void deleteEncryptionKey(@PathVariable("dataType") DataType dataType, @PathVariable("externalId") UUID externalId, @PathVariable("accessMode") AccessMode accessMode, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    /**
     * Caller wants to decrypt some data. Returning the corresponding encryption key.
     */
    @GetMapping(EncryptionEndpoints.ENCRYPTION_INTERNAL_GET_KEY)
    String getEncryptionKey(@PathVariable("dataType") DataType dataType, @PathVariable("externalId") UUID externalId, @PathVariable("accessMode") AccessMode accessMode, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
