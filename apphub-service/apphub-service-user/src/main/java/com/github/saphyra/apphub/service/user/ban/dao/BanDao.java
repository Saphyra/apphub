package com.github.saphyra.apphub.service.user.ban.dao;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class BanDao extends AbstractDao<BanEntity, Ban, String, BanRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;

    public BanDao(BanConverter converter, BanRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }

    public void deleteById(UUID banId) {
        deleteById(uuidConverter.convertDomain(banId));
    }

    public List<Ban> getByUserId(UUID userId) {
        return converter.convertEntity(repository.getByUserId(uuidConverter.convertDomain(userId)));
    }

    public void deleteExpired(LocalDateTime expiration) {
        repository.deleteExpired(expiration);
    }
}
