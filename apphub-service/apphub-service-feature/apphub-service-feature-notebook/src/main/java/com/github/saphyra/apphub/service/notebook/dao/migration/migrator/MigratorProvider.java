package com.github.saphyra.apphub.service.notebook.dao.migration.migrator;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MigratorProvider {
    private final Map<ListItemType, ListItemMigrator> migrators;

    public MigratorProvider(List<ListItemMigrator> migrators) {
        this.migrators = migrators.stream()
            .collect(Collectors.toMap(ListItemMigrator::getType, migrator -> migrator));
    }

    public ListItemMigrator getForType(ListItemType listItemType) {
        return migrators.get(listItemType);
    }

    @PostConstruct
    void verifyMigrators() {
        List<ListItemType> missingMigrators = Arrays.stream(ListItemType.values())
            .filter(listItemType -> !migrators.containsKey(listItemType))
            .toList();

        if (!missingMigrators.isEmpty()) {
            throw new IllegalStateException("Missing migrators for ListItemTypes: " + missingMigrators);
        }
    }
}
