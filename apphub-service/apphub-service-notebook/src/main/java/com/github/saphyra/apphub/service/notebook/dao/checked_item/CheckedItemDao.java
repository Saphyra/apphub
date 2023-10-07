package com.github.saphyra.apphub.service.notebook.dao.checked_item;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
//TODO unit test
public class CheckedItemDao extends AbstractDao<CheckedItemEntity, CheckedItem, String, CheckedItemRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;

    public CheckedItemDao(CheckedItemConverter converter, CheckedItemRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }

    public CheckedItem findByIdValidated(UUID checkedItemId) {
        return findById(checkedItemId)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "CheckedItem not found with id " + checkedItemId));
    }

    private Optional<CheckedItem> findById(UUID checkedItemId) {
        return findById(uuidConverter.convertDomain(checkedItemId));
    }

    public void deleteById(UUID dimensionId) {
        deleteById(uuidConverter.convertDomain(dimensionId));
    }
}
