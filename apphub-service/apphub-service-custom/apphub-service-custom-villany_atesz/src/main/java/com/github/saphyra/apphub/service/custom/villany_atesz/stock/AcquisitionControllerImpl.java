package com.github.saphyra.apphub.service.custom.villany_atesz.stock;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.AcquisitionResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.server.AcquisitionController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.acquisition.AcquisitionDateListProvider;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.acquisition.AcquisitionQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AcquisitionControllerImpl implements AcquisitionController {
    private final AcquisitionDateListProvider acquisitionDateListProvider;
    private final AcquisitionQueryService acquisitionQueryService;

    @Override
    public List<String> getDates(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know their acquisition dates", accessTokenHeader.getUserId());

        return acquisitionDateListProvider.getDates(accessTokenHeader.getUserId());
    }

    @Override
    public List<AcquisitionResponse> getAcquisitionsOnDay(LocalDate acquiredAt, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know their acquisitions", accessTokenHeader.getUserId());

        return acquisitionQueryService.getAcquisitionsOnDay(accessTokenHeader.getUserId(), acquiredAt);
    }
}
