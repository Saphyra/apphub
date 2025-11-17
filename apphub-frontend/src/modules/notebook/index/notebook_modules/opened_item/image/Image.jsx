import React, { useEffect, useState } from "react";
import Button from "../../../../../../common/component/input/Button";
import OpenedPageType from "../../../../common/OpenedPageType";
import "./image.css";
import OpenedListItemHeader from "../OpenedListItemHeader";
import { NOTEBOOK_GET_LIST_ITEM } from "../../../../../../common/js/dao/endpoints/NotebookEndpoints";
import { STORAGE_DOWNLOAD_FILE } from "../../../../../../common/js/dao/endpoints/StorageEndpoints";

const Image = ({ localizationHandler, openedListItem, setOpenedListItem, setDisplaySpinner }) => {
    const [title, setTitle] = useState("");
    const [parent, setParent] = useState(null);
    const [storedFileId, setStoredFileId] = useState(null);

    useEffect(() => loadListItem(), null);

    const loadListItem = () => {
        const fetch = async () => {
            const listItemData = await NOTEBOOK_GET_LIST_ITEM.createRequest(null, { listItemId: openedListItem.id })
                .send(setDisplaySpinner);

            setTitle(listItemData.title);
            setParent(listItemData.parentId);
            setStoredFileId(listItemData.value);
        }
        fetch();
    }

    const download = () => {
        window.open(STORAGE_DOWNLOAD_FILE.assembleUrl({ storedFileId: storedFileId }));
    }

    return (
        <div id="notebook-content-image" className="notebook-content notebook-content-view">
            <OpenedListItemHeader
                localizationHandler={localizationHandler}
                title={title}
                setTitle={setTitle}
                editingEnabled={false}
                close={() => setOpenedListItem({ id: parent, type: OpenedPageType.CATEGORY })}
            />

            <div id="notebook-content-image-content" className="notebook-content-view-main">
                {storedFileId &&
                    <img
                        id="notebook-content-image-src"
                        src={STORAGE_DOWNLOAD_FILE.assembleUrl({ storedFileId: storedFileId })}
                    />}
            </div>

            <div className="notebook-content-buttons">
                <Button
                    id="notebook-content-image-download-button"
                    label={localizationHandler.get("download")}
                    onclick={download}
                />
            </div>
        </div>
    );
}

export default Image;