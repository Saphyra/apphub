package com.github.saphyra.apphub.service.notebook.migration;

import com.github.saphyra.apphub.api.notebook.model.ListItemType;
import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.RegisterProcessorRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.ForRemoval;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.event.processor.EventProcessorRegistry;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeDao;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.service.table.dto.Link;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@ForRemoval("custom-table-link-migration")
public class NotebookMigrationEventController implements EventProcessorRegistry {
    private static final String EVENT_NAME = "notebook-custom-table-link-migration";
    private static final String URL = "/event/notebook/custom-table/link/migration";

    private final String host;
    private final ListItemDao listItemDao;
    private final AccessTokenProvider accessTokenProvider;
    private final ErrorReporterService errorReporterService;
    private final DimensionDao dimensionDao;
    private final ContentDao contentDao;
    private final ColumnTypeDao columnTypeDao;
    private final ObjectMapperWrapper objectMapperWrapper;

    public NotebookMigrationEventController(
        @Value("${event.serviceHost}") String host,
        ListItemDao listItemDao,
        AccessTokenProvider accessTokenProvider,
        ErrorReporterService errorReporterService,
        DimensionDao dimensionDao,
        ContentDao contentDao,
        ColumnTypeDao columnTypeDao,
        ObjectMapperWrapper objectMapperWrapper
    ) {
        this.host = host;
        this.listItemDao = listItemDao;
        this.accessTokenProvider = accessTokenProvider;
        this.errorReporterService = errorReporterService;
        this.dimensionDao = dimensionDao;
        this.contentDao = contentDao;
        this.columnTypeDao = columnTypeDao;
        this.objectMapperWrapper = objectMapperWrapper;
    }

    @Override
    public List<RegisterProcessorRequest> getRequests() {
        return List.of(
            RegisterProcessorRequest.builder()
                .host(host)
                .eventName(EVENT_NAME)
                .url(URL)
                .build()
        );
    }

    @PostMapping(URL)
    void migration() {
        log.info("Migrating CustomTable links...");
        listItemDao.getByListItemTypeUnencrypted(ListItemType.CUSTOM_TABLE)
            .forEach(bw -> wrapMigrate(bw.getEntity1(), bw.getEntity2()));
    }

    private void wrapMigrate(UUID userId, UUID listItemId) {
        try {
            accessTokenProvider.set(AccessTokenHeader.builder().userId(userId).build());

            migrate(listItemId);
        } catch (Exception e) {
            log.error("Failed migrating CustomTable {}", listItemId, e);
            errorReporterService.report("Failed migrating CustomTable " + listItemId, e);
        } finally {
            accessTokenProvider.clear();
        }
    }

    private void migrate(UUID listItemId) {
        dimensionDao.getByExternalReference(listItemId)
            .stream()
            .flatMap(row -> dimensionDao.getByExternalReference(row.getDimensionId()).stream())
            .filter(column -> columnTypeDao.findByIdValidated(column.getDimensionId()).getType() == ColumnType.LINK)
            .forEach(this::migrate);
    }

    private void migrate(Dimension column) {
        log.info("Migrating column {}", column.getDimensionId());

        Content content = contentDao.findByParentValidated(column.getDimensionId());
        content.setContent(migrate(content.getContent()));
        contentDao.save(content);
    }

    private String migrate(String url) {
        Link link = Link.builder()
            .url(url)
            .label(url)
            .build();

        return objectMapperWrapper.writeValueAsString(link);
    }
}
