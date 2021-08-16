package com.github.saphyra.apphub.service.notebook.dao.content;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ContentDao extends AbstractDao<ContentEntity, Content, String, ContentRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;

    public ContentDao(ContentConverter converter, ContentRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public void deleteByParent(UUID parent) {
        repository.deleteByParent(uuidConverter.convertDomain(parent));
    }

    public Content findByParentValidated(UUID parent) {
        return findByParent(parent)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.LIST_ITEM_NOT_FOUND, "Content not found by parent."));
    }

    public Optional<Content> findByParent(UUID parent) {
        return converter.convertEntity(repository.findByParent(uuidConverter.convertDomain(parent)));
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }

    public List<Content> getByUserId(UUID userId) {
        return converter.convertEntity(repository.getByUserId(uuidConverter.convertDomain(userId)));
    }
}
