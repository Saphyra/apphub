package com.github.saphyra.apphub.service.community.blacklist.dao;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_util.dao.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class BlacklistDao extends AbstractDao<BlacklistEntity, Blacklist, String, BlacklistRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;

    public BlacklistDao(BlacklistConverter converter, BlacklistRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public List<Blacklist> getByUserId(UUID userId) {
        return converter.convertEntity(repository.getByUserId(uuidConverter.convertDomain(userId)));
    }

    public Optional<Blacklist> findByUserIdOrBlockedUserId(UUID userId, UUID blockedUserId) {
        return converter.convertEntity(repository.findByUserIdOrBlockedUserId(uuidConverter.convertDomain(userId), uuidConverter.convertDomain(blockedUserId)));
    }

    public Optional<Blacklist> findById(UUID blacklistId) {
        return findById(uuidConverter.convertDomain(blacklistId));
    }

    public List<Blacklist> getByUserIdOrBlockedUserId(UUID userId) {
        return converter.convertEntity(repository.getByUserIdOrBlockedUserId(uuidConverter.convertDomain(userId)));
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }
}
