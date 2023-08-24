import React, { useEffect, useState } from "react";
import Button from "../../../../../../common/component/input/Button";
import ListItemType from "../../../../common/ListItemType";
import Endpoints from "../../../../../../common/js/dao/dao";
import "./image.css";
import OpenedListItemHeader from "../OpenedListItemHeader";

const Image = ({ localizationHandler, openedListItem, setOpenedListItem }) => {
    const [title, setTitle] = useState("");
    const [parent, setParent] = useState(null);
    const [storedFileId, setStoredFileId] = useState(null);

    useEffect(() => loadListItem(), null);

    const loadListItem = () => {
        const fetch = async () => {
            const listItemData = await Endpoints.NOTEBOOK_GET_LIST_ITEM.createRequest(null, { listItemId: openedListItem.id })
                .send();

            setTitle(listItemData.title);
            setParent(listItemData.parentId);
            setStoredFileId(listItemData.value);
        }
        fetch();
    }

    const download = () => {
        window.open(Endpoints.STORAGE_DOWNLOAD_FILE.assembleUrl({ storedFileId: storedFileId }));
    }

    return (
        <div id="notebook-content-image" className="notebook-content notebook-content-view">
            <OpenedListItemHeader
                localizationHandler={localizationHandler}
                title={title}
                setTitle={setTitle}
                editingEnabled={false}
                close={() => setOpenedListItem({ id: parent, type: ListItemType.CATEGORY })}
            />

            <div id="notebook-content-image-content" className="notebook-content-view-main">
                {storedFileId &&
                    <img
                        id="notebook-content-image-src"
                        src={Endpoints.STORAGE_DOWNLOAD_FILE.assembleUrl({ storedFileId: storedFileId })}
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