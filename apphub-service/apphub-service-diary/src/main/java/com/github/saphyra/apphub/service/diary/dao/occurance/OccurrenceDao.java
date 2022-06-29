package com.github.saphyra.apphub.service.diary.dao.occurance;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
//TODO unit test
public class OccurrenceDao extends AbstractDao<OccurrenceEntity, Occurrence, String, OccurrenceRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;

    public OccurrenceDao(OccurrenceConverter converter, OccurrenceRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }

    public void deleteVirtualByUserId(UUID userId) {
        repository.deleteByUserIdAndStatus(uuidConverter.convertDomain(userId), OccurrenceStatus.VIRTUAL);
    }

    public List<Occurrence> getByEventId(UUID eventId) {
        return converter.convertEntity(repository.getByEventId(uuidConverter.convertDomain(eventId)));
    }
}
