package com.github.saphyra.apphub.service.notebook.dao.deprecated_content;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.dao.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Deprecated(forRemoval = true)
public class DeprecatedContentDao extends AbstractDao<ContentEntity, DeprecatedContent, String, DeprecatedContentRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;

    public DeprecatedContentDao(DeprecatedContentConverter converter, DeprecatedContentRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public void deleteByParent(UUID parent) {
        repository.deleteByParent(uuidConverter.convertDomain(parent));
    }

    public DeprecatedContent findByParentValidated(UUID parent) {
        return findByParent(parent)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Content not found by parent " + parent));
    }

    public Optional<DeprecatedContent> findByParent(UUID parent) {
        return converter.convertEntity(repository.findByParent(uuidConverter.convertDomain(parent)));
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }

    public List<DeprecatedContent> getByUserId(UUID userId) {
        return converter.convertEntity(repository.getByUserId(uuidConverter.convertDomain(userId)));
    }
}
