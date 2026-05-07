import Button from "common/component/input/Button";
import InputField from "common/component/input/InputField";
import MoveDirection from "../../MoveDirection";

const TableHead = ({ localizationHandler, tableHeadData, updateTableHead, moveColumn, removeColumn, editingEnabled = true }) => {
    const updateContent = (content) => {
        tableHeadData.content = content;
        updateTableHead();
    }

    if (editingEnabled) {
        return (
            <th className="table-head editable">
                <InputField
                    className="notebook-table-head-content"
                    type="text"
                    placeholder={localizationHandler.get("table-head-title")}
                    onchangeCallback={updateContent}
                    value={tableHeadData.content}
                />

                <Button
                    className="notebook-table-head-move-left-button"
                    label="<"
                    onclick={() => moveColumn(tableHeadData, MoveDirection.LEFT)}
                />

                <Button
                    className="notebook-table-head-move-right-button"
                    label=">"
                    onclick={() => moveColumn(tableHeadData, MoveDirection.RIGHT)}
                />

                <Button
                    className="notebook-table-head-remove-button"
                    label="X"
                    onclick={() => removeColumn(tableHeadData)}
                />
            </th>
        );
    } else {
        return (
            <th className="table-head">
                <div className="notebook-table-head-content">{tableHeadData.content}</div>
            </th>
        )
    }
}

export default TableHead;