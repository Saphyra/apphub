package com.github.saphyra.apphub.service.notebook.dao.file;

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
public class FileDao extends AbstractDao<FileEntity, File, String, FileRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;

    public FileDao(FileConverter converter, FileRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public Optional<File> findByParent(UUID parent) {
        return converter.convertEntity(repository.findByParent(uuidConverter.convertDomain(parent)));
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }

    public File findByParentValidated(UUID parent) {
        return findByParent(parent)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Image not found by parent " + parent));
    }

    public void deleteByParent(UUID parent) {
        repository.deleteByParent(uuidConverter.convertDomain(parent));
    }
}
