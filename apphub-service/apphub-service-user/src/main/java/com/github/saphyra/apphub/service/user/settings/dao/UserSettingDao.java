package com.github.saphyra.apphub.service.user.settings.dao;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_util.dao.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class UserSettingDao extends AbstractDao<UserSettingEntity, UserSetting, UserSettingEntityId, UserSettingRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;

    public UserSettingDao(UserSettingConverter converter, UserSettingRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public List<UserSetting> getByUserIdAndCategory(UUID userId, String category) {
        return converter.convertEntity(repository.getByUserIdAndCategory(uuidConverter.convertDomain(userId), category));
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }
}
