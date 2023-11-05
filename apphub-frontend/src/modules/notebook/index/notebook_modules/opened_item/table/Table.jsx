import React, { useEffect, useState } from "react";
import Button from "../../../../../../common/component/input/Button";
import "./table.css";
import useHasFocus from "../../../../../../common/js/UseHasFocus";
import { useUpdateEffect } from "react-use";
import OpenedListItemHeader from "../OpenedListItemHeader";
import { confirmDeleteChcecked, loadTable, save } from "./service/TableDao";
import { getTableHeads, getTableRows } from "./service/TableAssembler";
import { close, discard } from "./service/TableUtils";
import { newRow } from "./service/TableRowCrudService";
import { newColumn } from "./service/TableColumnCrudService";

const Table = ({
    localizationHandler,
    openedListItem,
    setOpenedListItem,
    setLastEvent,
    checklist,
    setConfirmationDialogData
}) => {
    const [editingEnabled, setEditingEnabled] = useState(false);
    const [parent, setParent] = useState(null);
    const [title, setTitle] = useState("");
    const [tableHeads, setTableHeads] = useState([]);
    const [rows, setRows] = useState([]);

    useEffect(() => loadTable(openedListItem.id, setDataFromResponse), [openedListItem]);

    const isInFocus = useHasFocus();
    useUpdateEffect(() => {
        if (isInFocus && !editingEnabled) {
            loadTable(openedListItem.id, setDataFromResponse);
        }
    }, [isInFocus]);

    const setDataFromResponse = (response) => {
        setTitle(response.title);
        setParent(response.parent);
        setTableHeads(response.tableHeads);
        setRows(response.rows);
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
                            {editingEnabled && <th></th>}
                            {checklist && <th>{localizationHandler.get("checked")}</th>}
                            {getTableHeads(tableHeads, localizationHandler, editingEnabled, setTableHeads, rows, setRows)}
                        </tr>
                    </thead>

                    <tbody>
                        {getTableRows(rows, checklist, editingEnabled, setRows)}
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
                        onclick={() => confirmDeleteChcecked(setConfirmationDialogData, localizationHandler, openedListItem.id, setDataFromResponse)}
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
                        onclick={() => save(title, tableHeads, rows, openedListItem.id, setEditingEnabled, setLastEvent, setDataFromResponse)}
                    />
                }

                {editingEnabled &&
                    <Button
                        id="notebook-content-table-new-row-button"
                        label={localizationHandler.get("new-row")}
                        onclick={() => newRow(rows, tableHeads, setRows)}
                    />
                }

                {editingEnabled &&
                    <Button
                        id="notebook-content-table-new-column-button"
                        label={localizationHandler.get("new-column")}
                        onclick={() => newColumn(tableHeads, setTableHeads, rows, setRows)}
                    />
                }
            </div>
        </div>
    );
}

export default Table;