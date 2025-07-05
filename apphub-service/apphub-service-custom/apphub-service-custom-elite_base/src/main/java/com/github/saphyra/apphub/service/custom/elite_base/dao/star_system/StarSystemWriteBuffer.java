package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.dao.WriteBuffer;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.google.common.cache.Cache;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
class StarSystemWriteBuffer extends WriteBuffer<UUID, StarSystem> {
    private final StarSystemRepository starSystemRepository;
    private final StarSystemConverter starSystemConverter;
    private final ErrorReporterService errorReporterService;
    private final ExecutorServiceBean executorServiceBean;

    protected StarSystemWriteBuffer(
        DateTimeUtil dateTimeUtil,
        Cache<UUID, StarSystem> starSystemReadCache,
        StarSystemRepository starSystemRepository,
        StarSystemConverter starSystemConverter,
        ErrorReporterService errorReporterService,
        ExecutorServiceBean executorServiceBean
    ) {
        super(dateTimeUtil, starSystemReadCache);
        this.starSystemRepository = starSystemRepository;
        this.starSystemConverter = starSystemConverter;
        this.errorReporterService = errorReporterService;
        this.executorServiceBean = executorServiceBean;
    }

    @Override
    protected void doSynchronize() {
        try {
            starSystemRepository.saveAll(starSystemConverter.convertDomain(buffer.values()));
        } catch (Exception e) {
            errorReporterService.report("Failed saving StarSystems in batch. Saving them one by one...", e);
            buffer.values()
                .forEach(starSystem -> executorServiceBean.execute(() -> save(starSystem)));
        }
    }

    private void save(StarSystem starSystem) {
        try {
            starSystemRepository.save(starSystemConverter.convertDomain(starSystem));
        } catch (Exception e) {
            errorReporterService.report("Failed saving StarSystem " + starSystem, e);
        }
    }
}
