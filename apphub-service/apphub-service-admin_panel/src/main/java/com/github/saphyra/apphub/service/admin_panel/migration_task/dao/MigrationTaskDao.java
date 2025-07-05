package com.github.saphyra.apphub.service.admin_panel.migration_task.dao;

import com.github.saphyra.apphub.lib.common_util.dao.AbstractDao;
import org.springframework.stereotype.Component;

@Component
public class MigrationTaskDao extends AbstractDao<MigrationTaskEntity, MigrationTask, String, MigrationTaskRepository> {
    public MigrationTaskDao(MigrationTaskConverter converter, MigrationTaskRepository repository) {
        super(converter, repository);
    }
}
