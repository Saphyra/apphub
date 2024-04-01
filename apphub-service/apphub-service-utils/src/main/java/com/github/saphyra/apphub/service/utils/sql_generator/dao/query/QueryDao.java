package com.github.saphyra.apphub.service.utils.sql_generator.dao.query;

import com.github.saphyra.apphub.api.platform.encryption.model.DataType;
import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption_dao.EncryptionDao;
import com.github.saphyra.apphub.lib.encryption_dao.EncryptionProxy;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
//TODO unit test
public class QueryDao extends EncryptionDao<QueryEntity, Query, QueryRepository> implements DeleteByUserIdDao {
    public QueryDao(QueryConverter converter, QueryRepository repository, EncryptionProxy encryptionProxy, UuidConverter uuidConverter) {
        super(converter, repository, encryptionProxy, DataType.UTILS_SQL_GENERATOR_QUERY, uuidConverter);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }
}
