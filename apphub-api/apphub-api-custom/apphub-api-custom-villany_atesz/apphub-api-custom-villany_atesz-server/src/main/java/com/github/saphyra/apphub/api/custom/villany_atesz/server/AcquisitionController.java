package com.github.saphyra.apphub.api.custom.villany_atesz.server;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.AcquisitionResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.time.LocalDate;
import java.util.List;

public interface AcquisitionController {
    @GetMapping(Endpoints.VILLANY_ATESZ_GET_ACQUISITION_DATES)
    List<String> getDates(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(Endpoints.VILLANY_ATESZ_GET_ACQUISITIONS)
    List<AcquisitionResponse> getAcquisitionsOnDay(@PathVariable("acquiredAt") LocalDate acquiredAt, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
