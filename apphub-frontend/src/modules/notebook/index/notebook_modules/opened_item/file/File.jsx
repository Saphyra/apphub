import React, { useEffect, useState } from "react";
import Button from "../../../../../../common/component/input/Button";
import OpenedPageType from "../../../../common/OpenedPageType";
import "./file.css";
import LocalDateTime from "../../../../../../common/js/date/LocalDateTime";
import OpenedListItemHeader from "../OpenedListItemHeader";
import { formatFileSize } from "../../../../../../common/js/Utils";
import { NOTEBOOK_GET_LIST_ITEM } from "../../../../../../common/js/dao/endpoints/NotebookEndpoints";
import { STORAGE_DOWNLOAD_FILE, STORAGE_GET_METADATA } from "../../../../../../common/js/dao/endpoints/StorageEndpoints";

const File = ({ localizationHandler, openedListItem, setOpenedListItem }) => {
    const [title, setTitle] = useState("");
    const [parent, setParent] = useState(null);
    const [storedFileId, setStoredFileId] = useState(null);
    const [fileMetadata, setFileMetadata] = useState(null);

    useEffect(() => loadListItem(), null);
    useEffect(() => loadMetadata(), [storedFileId]);

    const loadListItem = () => {
        const fetch = async () => {
            const listItemData = await NOTEBOOK_GET_LIST_ITEM.createRequest(null, { listItemId: openedListItem.id })
                .send();

            setTitle(listItemData.title);
            setParent(listItemData.parentId);
            setStoredFileId(listItemData.value);
        }
        fetch();
    }

    const loadMetadata = () => {
        if (!storedFileId) {
            return;
        }

        const fetch = async () => {
            const response = await STORAGE_GET_METADATA.createRequest(null, { storedFileId: storedFileId })
                .send();
            setFileMetadata(response);
        }
        fetch();
    }

    const download = () => {
        window.open(STORAGE_DOWNLOAD_FILE.assembleUrl({ storedFileId: storedFileId }));
    }

    return (
        <div id="notebook-content-file" className="notebook-content notebook-content-view">
            <OpenedListItemHeader
                localizationHandler={localizationHandler}
                title={title}
                setTitle={setTitle}
                editingEnabled={false}
                close={() => setOpenedListItem({ id: parent, type: OpenedPageType.CATEGORY })}
            />

            <div id="notebook-content-file-content" className="notebook-content-view-main">
                {fileMetadata &&
                    <table id="notebook-content-file-content-table" className="formatted-table content-selectable">
                        <tbody>
                            <tr>
                                <td id="notebook-content-file-name-label">{localizationHandler.get("file-name")}</td>
                                <td id="notebook-content-file-name">{fileMetadata.fileName}</td>
                            </tr>
                            <tr>
                                <td id="notebook-content-file-size-label">{localizationHandler.get("file-size")}</td>
                                <td id="notebook-content-file-size">{formatFileSize(fileMetadata.size)}</td>
                            </tr>
                            <tr>
                                <td id="notebook-content-file-created-at-label">{localizationHandler.get("created-at")}</td>
                                <td id="notebook-content-file-created-at">{LocalDateTime.fromEpochSeconds(fileMetadata.createdAt).format()}</td>
                            </tr>
                        </tbody>
                    </table>
                }
            </div>

            <div className="notebook-content-buttons">
                <Button
                    id="notebook-content-file-download-button"
                    label={localizationHandler.get("download")}
                    onclick={download}
                />
            </div>
        </div>
    );
}

export default File;