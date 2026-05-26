package com.github.saphyra.apphub.service.notebook.service.table.column_data;

import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ColumnDataServiceProviderTest {
    @Mock
    private ColumnDataService columnDataService;

    private ColumnDataServiceProvider underTest;

    @BeforeEach
    void setUp() {
        underTest = new ColumnDataServiceProvider(List.of(columnDataService));
    }

    @Test
    void getForType_notFound() {
        given(columnDataService.canProcess(ColumnType.DATE)).willReturn(false);

        assertThat(catchThrowable(() -> underTest.getForType(ColumnType.DATE))).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void getForType() {
        given(columnDataService.canProcess(ColumnType.DATE)).willReturn(true);

        assertThat(underTest.getForType(ColumnType.DATE)).isEqualTo(columnDataService);
    }

    @Test
    void validate_missingType() {
        given(columnDataService.canProcess(any(ColumnType.class))).willReturn(false);

        assertThat(catchThrowable(() -> underTest.validate())).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void validate_allTypesHandled() {
        given(columnDataService.canProcess(any(ColumnType.class))).willReturn(true);

        underTest.validate();

        // No exception thrown
    }
}