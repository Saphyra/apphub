import React, { useEffect, useState } from "react";
import localizationData from "./new_table_localization.json";
import LocalizationHandler from "../../../../common/js/LocalizationHandler";
import { useParams } from "react-router";
import sessionChecker from "../../../../common/js/SessionChecker";
import NotificationService from "../../../../common/js/notification/NotificationService";
import Header from "../../../../common/component/Header";
import ListItemTitle from "../../common/list_item_title/ListItemTitle";
import ParentSelector from "../../common/parent_selector/ParentSelector";
import { ToastContainer } from "react-toastify";
import Footer from "../../../../common/component/Footer";
import Button from "../../../../common/component/input/Button";
import Constants from "../../../../common/js/Constants";
import TableHeadData from "../../common/table/table_head/TableHeadData";
import "./new_table.css";
import Utils from "../../../../common/js/Utils";
import TableColumnData from "../../common/table/row/column/TableColumnData";
import TableRowData from "../../common/table/row/TableRowData";
import create from "./service/NewTableSaver";
import getTable from "./service/NewTableAssembler";
import ColumnType from "../../common/table/row/column/type/ColumnType";
import Spinner from "../../../../common/component/Spinner";
import Stream from "../../../../common/js/collection/Stream";

const NewTablePage = ({ checklist, custom }) => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");

    const { parent } = useParams();
    const [parentId, setParentId] = useState(parent === "null" ? null : parent);
    const [listItemTitle, setListItemTitle] = useState("");
    const [tableHeads, setTableHeads] = useState([new TableHeadData(0)]);
    const [rows, setRows] = useState([new TableRowData(0, [new TableColumnData(0, (custom ? ColumnType.EMPTY : ColumnType.TEXT))])]);
    const [displaySpinner, setDisplaySpinner] = useState(false);
    const [files, setFiles] = useState([]);

    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);

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
        <div id="notebook-new-table" className="main-page">
            <Header label={localizationHandler.get("page-title")} />

            <main id="notebook-new-table-main">
                <ListItemTitle
                    inputId="notebook-new-table-title"
                    placeholder={localizationHandler.get("table-title")}
                    setListItemTitle={setListItemTitle}
                    value={listItemTitle}
                />

                <ParentSelector
                    parentId={parentId}
                    setParentId={setParentId}
                />

                <div id="notebook-new-table-content-wrapper">
                    {getTable(checklist, localizationHandler, tableHeads, setTableHeads, rows, setRows, custom, addFile)}
                </div>
            </main>

            <Footer
                centerButtons={
                    <Button
                        id="notebook-new-table-create-button"
                        label={localizationHandler.get("create")}
                        onclick={() => create(listItemTitle, tableHeads, parentId, checklist, rows, custom, setDisplaySpinner, files)}
                    />
                }

                rightButtons={[
                    <Button
                        key="back-button"
                        id="notebook-new-checklist-back-button"
                        label={localizationHandler.get("back")}
                        onclick={() => window.location.href = Constants.NOTEBOOK_NEW_PAGE + "/" + parent}
                    />,
                    <Button
                        key="home-button"
                        id="notebook-new-checklist-home-button"
                        label={localizationHandler.get("home")}
                        onclick={() => window.location.href = Constants.NOTEBOOK_PAGE}
                    />
                ]}
            />

            {displaySpinner && <Spinner />}

            <ToastContainer />
        </div>
    )
}

export default NewTablePage;