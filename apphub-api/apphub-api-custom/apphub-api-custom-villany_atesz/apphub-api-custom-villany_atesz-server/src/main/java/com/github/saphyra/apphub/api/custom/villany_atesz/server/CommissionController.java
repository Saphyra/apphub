package com.github.saphyra.apphub.api.custom.villany_atesz.server;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.CommissionRequest;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.CommissionResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.CommissionView;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.common.endpoints.VillanyAteszEndpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

//TODO role protection test
//TODO BE test
public interface CommissionController {
    @PostMapping(VillanyAteszEndpoints.VILLANY_ATESZ_COMMISSION_CREATE_OR_UPDATE)
    CommissionResponse createOrUpdateCommission(@RequestBody CommissionRequest request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(VillanyAteszEndpoints.VILLANY_ATESZ_COMMISSION_DELETE)
    List<CommissionView> deleteCommission(@PathVariable("commissionId") UUID commissionId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(VillanyAteszEndpoints.VILLANY_ATESZ_COMMISSION_GET)
    List<CommissionView> getCommissions(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
