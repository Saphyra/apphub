package com.github.saphyra.apphub.service.feature.calendar.domain.share.service;

import com.github.saphyra.apphub.api.feature.calendar.model.request.ShareObjectRequest;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.Alm;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmFactory;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.PrincipalType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
//TODO unit test
public class ShareObjectService {
    private final ShareObjectRequestValidator shareObjectRequestValidator;
    private final AlmFactory almFactory;
    private final AlmDao almDao;

    public void share(UUID userId, ShareObjectRequest request) {
        shareObjectRequestValidator.validate(userId, request);

        Alm alm = almFactory.createAlm(request.getSharedWith(), PrincipalType.USER, request.getObjectId(), request.getType(), request.getOwner(), request.getParent(), request.getGrants());
        almDao.save(alm);
    }
}
