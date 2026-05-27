package com.github.saphyra.apphub.service.notebook.dao.migration.migrator;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class MigratorProviderTest {
    @Mock
    private ListItemMigrator migrator;

    @Test
    void getForType() {
        MigratorProvider underTest = createWithAllTypes();

        assertThat(underTest.getForType(ListItemType.CATEGORY)).isNotNull();
        assertThat(underTest.getForType(ListItemType.TEXT)).isNotNull();
    }

    @Test
    void verifyMigrators_allTypesPresent() {
        MigratorProvider underTest = createWithAllTypes();

        underTest.verifyMigrators();
    }

    @Test
    void verifyMigrators_missingMigrator() {
        given(migrator.getType()).willReturn(ListItemType.CATEGORY);

        MigratorProvider underTest = new MigratorProvider(List.of(migrator));

        Throwable thrown = catchThrowable(underTest::verifyMigrators);

        assertThat(thrown).isInstanceOf(IllegalStateException.class);
    }

    private MigratorProvider createWithAllTypes() {
        List<ListItemMigrator> migrators = Arrays.stream(ListItemType.values())
            .map(type -> {
                ListItemMigrator migrator = mock(ListItemMigrator.class);
                given(migrator.getType()).willReturn(type);
                return migrator;
            })
            .collect(Collectors.toList());

        return new MigratorProvider(migrators);
    }
}

