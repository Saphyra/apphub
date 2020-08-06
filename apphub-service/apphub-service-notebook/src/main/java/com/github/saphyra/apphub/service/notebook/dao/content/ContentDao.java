package com.github.saphyra.apphub.service.notebook.dao.content;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.UuidConverter;
import com.github.saphyra.apphub.lib.exception.NotFoundException;
import com.github.saphyra.dao.AbstractDao;
import org.springframework.stereotype.Component;

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
        return converter.convertEntity(repository.findByParent(uuidConverter.convertDomain(parent)))
            .orElseThrow(() -> new NotFoundException(new ErrorMessage(ErrorCode.LIST_ITEM_NOT_FOUND.name()), "Text not found by parent " + parent));
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }
}
