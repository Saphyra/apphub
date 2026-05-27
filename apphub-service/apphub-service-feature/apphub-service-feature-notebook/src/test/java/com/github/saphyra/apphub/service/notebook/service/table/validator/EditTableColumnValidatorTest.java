package com.github.saphyra.apphub.service.notebook.service.table.validator;

import com.github.saphyra.apphub.api.feature.notebook.model.ItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class EditTableColumnValidatorTest {

	private static final Object DATA = "data";

	@Mock
	private TableColumnDataValidator tableColumnDataValidator;

	@InjectMocks
	private EditTableColumnValidator underTest;

	@Test
	void nullColumns() {
		Throwable ex = catchThrowable(() -> underTest.validateColumns(ItemType.EXISTING, null));

		ExceptionValidator.validateInvalidParam(ex, "row.columns", "must not be null");
	}

	@Test
	void nullColumnIndex() {
		TableColumnModel model = TableColumnModel.builder()
			.columnIndex(null)
			.columnType(ColumnType.TEXT)
			.itemType(ItemType.EXISTING)
			.data(DATA)
			.build();

		Throwable ex = catchThrowable(() -> underTest.validateColumns(ItemType.EXISTING, List.of(model)));

		ExceptionValidator.validateInvalidParam(ex, "row.column.columnIndex", "must not be null");
	}

	@Test
	void nullColumnType() {
		TableColumnModel model = TableColumnModel.builder()
			.columnIndex(1)
			.columnType(null)
			.itemType(ItemType.EXISTING)
			.data(DATA)
			.build();

		Throwable ex = catchThrowable(() -> underTest.validateColumns(ItemType.EXISTING, List.of(model)));

		ExceptionValidator.validateInvalidParam(ex, "row.column.columnType", "must not be null");
	}

	@Test
	void nullItemType() {
		TableColumnModel model = TableColumnModel.builder()
			.columnIndex(1)
			.columnType(ColumnType.TEXT)
			.itemType(null)
			.data(DATA)
			.build();

		Throwable ex = catchThrowable(() -> underTest.validateColumns(ItemType.EXISTING, List.of(model)));

		ExceptionValidator.validateInvalidParam(ex, "row.column.itemType", "must not be null");
	}

	@Test
	void newRowContainExistingColumn() {
		TableColumnModel model = TableColumnModel.builder()
			.columnIndex(1)
			.columnType(ColumnType.TEXT)
			.itemType(ItemType.EXISTING)
			.data(DATA)
			.build();

		Throwable ex = catchThrowable(() -> underTest.validateColumns(ItemType.NEW, List.of(model)));

		ExceptionValidator.validateInvalidParam(ex, "row.column.itemType", "must be NEW");
	}

	@Test
	void validExistingRowDelegatesColumnValidation() {
		TableColumnModel model = TableColumnModel.builder()
			.columnIndex(1)
			.columnType(ColumnType.TEXT)
			.itemType(ItemType.EXISTING)
			.data(DATA)
			.build();

		underTest.validateColumns(ItemType.EXISTING, List.of(model));

		then(tableColumnDataValidator).should().validate(ColumnType.TEXT, DATA);
	}

}