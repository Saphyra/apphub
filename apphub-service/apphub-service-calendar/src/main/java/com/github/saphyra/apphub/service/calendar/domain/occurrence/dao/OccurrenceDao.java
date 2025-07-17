package com.github.saphyra.apphub.service.calendar.domain.occurrence.dao;

import com.github.saphyra.apphub.lib.common_util.converter.Converter;
import com.github.saphyra.apphub.lib.common_util.dao.AbstractDao;
import org.springframework.stereotype.Component;

@Component
public class OccurrenceDao extends AbstractDao<OccurrenceEntity, Occurrence, String, OccurrenceRepository> {
    OccurrenceDao(Converter<OccurrenceEntity, Occurrence> converter, OccurrenceRepository repository) {
        super(converter, repository);
    }
}
