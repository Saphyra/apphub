package com.github.saphyra.apphub.api.platform.encryption.server;

import com.github.saphyra.apphub.api.platform.encryption.model.AccessMode;
import com.github.saphyra.apphub.api.platform.encryption.model.DataType;
import com.github.saphyra.apphub.api.platform.encryption.model.EncryptionKey;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

public interface EncryptionKeyApiController {
    @PutMapping(Endpoints.ENCRYPTION_INTERNAL_CREATE_KEY)
        //TODO integration test
    String createEncryptionKey(@RequestBody EncryptionKey request, @PathVariable("accessMode") AccessMode accessMode, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(Endpoints.ENCRYPTION_INTERNAL_DELETE_KEY)
        //TODO integration test
    void deleteEncryptionKey(@PathVariable("dataType") DataType dataType, @PathVariable("externalId") UUID externalId, @PathVariable("accessMode") AccessMode accessMode, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(Endpoints.ENCRYPTION_INTERNAL_GET_KEY)
        //TODO integration test
    String getEncryptionKey(@PathVariable("dataType") DataType dataType, @PathVariable("externalId") UUID externalId, @PathVariable("accessMode") AccessMode accessMode, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
