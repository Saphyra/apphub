package com.github.saphyra.apphub.service.calendar.domain.label.dao;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.common_util.dao.AbstractDao;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
//TODO unit test
public class LabelDao extends AbstractDao<LabelEntity, Label, String, LabelRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;

     LabelDao(LabelConverter converter, LabelRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
         this.uuidConverter = uuidConverter;
     }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }
}
