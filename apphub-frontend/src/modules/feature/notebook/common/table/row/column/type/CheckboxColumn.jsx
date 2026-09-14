import Button from "common/component/input/Button";
import InputField from "common/component/input/InputField";
import { isTrue } from "common/js/Utils";
import { NOTEBOOK_TABLE_SET_CHECKBOX_COLUMN_STATUS } from "modules/feature/notebook/NotebookEndpoints";

const CheckboxColumn = ({
    openedItem,
    rowId,
    columnData,
    updateColumn,
    editingEnabled = true,
    selectType,
    localizationHandler,
    setDisplaySpinner
}) => {
    const updateData = (checked) => {
        if (!editingEnabled) {
            NOTEBOOK_TABLE_SET_CHECKBOX_COLUMN_STATUS.createRequest({ value: checked }, { listItemId: openedItem.id, rowId: rowId, columnId: columnData.columnId })
                .send(setDisplaySpinner);
        }

        columnData.data = checked;
        updateColumn();
    }

    return (
        <td className={"table-column notebook-table-column-type-" + columnData.columnType.toLowerCase() + (editingEnabled ? " editable" : "")}>
            <div className="notebook-table-column-wrapper">
                <div className="notebook-table-column-content">
                    <InputField
                        type="checkbox"
                        onchangeCallback={updateData}
                        checked={isTrue(columnData.data)}
                    />
                </div>

                {editingEnabled &&
                    <Button
                        className="notebook-table-change-column-type-button"
                        onclick={selectType}
                        title={localizationHandler.get("change-column-type")}
                    />
                }
            </div>
        </td >
    )
}

export default CheckboxColumn;