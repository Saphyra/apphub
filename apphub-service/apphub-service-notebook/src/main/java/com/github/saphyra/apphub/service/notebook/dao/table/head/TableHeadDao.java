package com.github.saphyra.apphub.service.notebook.dao.table.head;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_util.UuidConverter;
import com.github.saphyra.converter.Converter;
import com.github.saphyra.dao.AbstractDao;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
//TODO unit test
public class TableHeadDao extends AbstractDao<TableHeadEntity, TableHead, String, TableHeadRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;

    public TableHeadDao(Converter<TableHeadEntity, TableHead> converter, TableHeadRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }
}
