import { useEffect, useState } from "react";
import "./table.css";
import { confirmDeleteChcecked, loadTable, save } from "./service/TableDao";
import useHasFocus from "common/hook/UseHasFocus";
import { useUpdateEffect } from "react-use";
import Stream from "common/js/collection/Stream";
import OpenedListItemHeader from "../OpenedListItemHeader";
import { close, discard } from "./service/TableUtils";
import AddColumnButton from "modules/feature/notebook/common/table/table_head/AddColumnButton";
import { newColumn } from "./service/TableColumnCrudService";
import TableHeadIndexRange from "modules/feature/notebook/common/table/table_head/TableHeadIndexRange";
import { getTableHeads, getTableRows } from "./service/TableAssembler";
import AddRowButton from "modules/feature/notebook/common/table/row/AddRowButton";
import { newRow } from "./service/TableRowCrudService";
import RowIndexRange from "modules/feature/notebook/common/table/row/RowIndexRange";
import Button from "common/component/input/Button";

const Table = ({
    localizationHandler,
    openedListItem,
    setOpenedListItem,
    setLastEvent,
    checklist,
    setConfirmationDialogData,
    custom = false,
    setDisplaySpinner
}) => {
    const [editingEnabled, setEditingEnabled] = useState(false);
    const [parent, setParent] = useState(null);
    const [title, setTitle] = useState("");
    const [tableHeads, setTableHeads] = useState([]);
    const [rows, setRows] = useState([]);
    const [files, setFiles] = useState([]);

    useEffect(() => loadTable(openedListItem.id, setDataFromResponse, setDisplaySpinner), [openedListItem]);

    const isInFocus = useHasFocus();
    useUpdateEffect(() => {
        if (isInFocus && !editingEnabled) {
            loadTable(openedListItem.id, setDataFromResponse, setDisplaySpinner);
        }
    }, [isInFocus]);

    const setDataFromResponse = (response) => {
        setTitle(response.title);
        setParent(response.parent);
        setTableHeads(response.tableHeads);
        setRows(response.rows);
    }

    const addFile = (rowIndex, columnIndex, file) => {
        const clone = new Stream(files)
            .remove(file => file.rowIndex == rowIndex && file.columnIndex == columnIndex)
            .add({
                rowIndex: rowIndex,
                columnIndex: columnIndex,
                file: file
            })
            .toList();

        setFiles(clone);
    }

    return (
        <div id="notebook-content-table" className="notebook-content notebook-content-view">
            <OpenedListItemHeader
                localizationHandler={localizationHandler}
                title={title}
                setTitle={setTitle}
                editingEnabled={editingEnabled}
                close={() => close(editingEnabled, setConfirmationDialogData, localizationHandler, setOpenedListItem, parent)}
            />

            <div id="notebook-content-table-content" className="notebook-content-view-main">
                <table id="notebook-content-table-content-table" className="formatted-table content-selectable">
                    <thead>
                        <tr>
                            {editingEnabled &&
                                <th>
                                    <AddColumnButton
                                        id="notebook-content-table-add-column-to-start"
                                        label="|<+"
                                        callback={() => newColumn(tableHeads, setTableHeads, rows, setRows, TableHeadIndexRange.MIN, custom)}
                                    />

                                    <AddColumnButton
                                        id="notebook-content-table-add-column-to-end"
                                        label="+>|"
                                        callback={() => newColumn(tableHeads, setTableHeads, rows, setRows, TableHeadIndexRange.MAX, custom)}
                                    />
                                </th>
                            }
                            {checklist && <th>{localizationHandler.get("checked")}</th>}
                            {getTableHeads(tableHeads, localizationHandler, editingEnabled, setTableHeads, rows, setRows)}
                        </tr>
                    </thead>

                    <tbody>
                        {editingEnabled &&
                            <AddRowButton
                                id="notebook-content-table-add-row-to-start"
                                className={"notebook-content-table-add-row-button"}
                                tableHeads={tableHeads}
                                checklist={checklist}
                                callback={() => newRow(rows, tableHeads, setRows, RowIndexRange.MIN, custom)}
                            />
                        }

                        {getTableRows(openedListItem, rows, checklist, editingEnabled, setRows, custom, addFile, setDisplaySpinner)}

                        {editingEnabled &&
                            <AddRowButton
                                id="notebook-content-table-add-row-to-end"
                                className={"notebook-content-table-add-row-button"}
                                tableHeads={tableHeads}
                                checklist={checklist}
                                callback={() => newRow(rows, tableHeads, setRows, RowIndexRange.MAX, custom)}
                            />
                        }
                    </tbody>
                </table>
            </div>

            <div className="notebook-content-buttons">
                {!editingEnabled &&
                    <Button
                        id="notebook-content-table-edit-button"
                        label={localizationHandler.get("edit")}
                        onclick={() => setEditingEnabled(true)}
                    />
                }

                {checklist && !editingEnabled &&
                    <Button
                        id="notebook-content-table-delete-checked-button"
                        label={localizationHandler.get("delete-checked")}
                        onclick={() => confirmDeleteChcecked(setConfirmationDialogData, localizationHandler, openedListItem.id, setDataFromResponse, setDisplaySpinner)}
                    />
                }

                {editingEnabled &&
                    <Button
                        id="notebook-content-table-discard-button"
                        label={localizationHandler.get("discard")}
                        onclick={() => discard(setConfirmationDialogData, localizationHandler, setEditingEnabled, openedListItem.id, setDataFromResponse)}
                    />
                }

                {editingEnabled &&
                    <Button
                        id="notebook-content-table-save-button"
                        label={localizationHandler.get("save")}
                        onclick={() => save(title, tableHeads, rows, openedListItem.id, setEditingEnabled, setLastEvent, setDataFromResponse, files, setDisplaySpinner)}
                    />
                }
            </div>
        </div>
    );
}

export default Table;