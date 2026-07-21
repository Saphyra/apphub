package com.github.saphyra.apphub.service.feature.calendar.domain.label.service;

import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.api.feature.calendar.model.response.LabelResponse;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.feature.calendar.common.dao.CommonCalendarDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.Label;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.LabelDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.LabelFactory;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.Alm;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.PrincipalType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class LabelService {
    private final LabelDao labelDao;
    private final LabelFactory labelFactory;
    private final LabelValidator labelValidator;
    private final CommonCalendarDao commonCalendarDao;
    private final AlmDao almDao;
    private final AccessTokenProvider accessTokenProvider;
    private final LabelToResponseMapper labelToResponseMapper;

    public LabelResponse createLabel(UUID userId, String label) {
        labelValidator.validate(label);

        Label domain = labelFactory.create(userId, label);
        commonCalendarDao.saveLabel(domain);

        return labelToResponseMapper.toResponse(domain, false);
    }

    public void deleteLabel(UUID userId, UUID labelId) {
        labelDao.findById(userId, labelId)
            .ifPresentOrElse(
                _ -> commonCalendarDao.deleteLabel(userId, labelId),
                () -> {
                    Optional<Alm> maybeAlm = almDao.findForObject(userId, PrincipalType.USER, labelId, SharedObjectType.LABEL)
                        .filter(alm -> true); //TODO check permission
                    if (maybeAlm.isEmpty()) {
                        throw ExceptionFactory.forbiddenOperation("%s has no delete permission for label %s".formatted(userId, labelId));
                    }

                    Alm alm = maybeAlm.get();
                    commonCalendarDao.deleteLabel(alm.getOwner(), labelId);
                });
    }

    public void editLabel(UUID userId, UUID labelId, String labelText) {
        labelValidator.validate(labelText);

        labelDao.findById(userId, labelId)
            .ifPresentOrElse(
                label -> {
                    label.setLabel(labelText);

                    labelDao.save(label);
                },
                () -> {
                    Optional<Alm> maybeAlm = almDao.findForObject(userId, PrincipalType.USER, labelId, SharedObjectType.LABEL)
                        .filter(alm -> true); //TODO check permission
                    if (maybeAlm.isEmpty()) {
                        throw ExceptionFactory.forbiddenOperation("%s has no edit permission for label %s".formatted(userId, labelId));
                    }

                    Alm alm = maybeAlm.get();
                    try (var _ = accessTokenProvider.set(alm.getOwner())) {
                        Label label = labelDao.findByIdValidated(alm.getOwner(), labelId);

                        label.setLabel(labelText);

                        labelDao.save(label);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }
}
