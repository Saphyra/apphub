import React, { useEffect, useState } from "react";
import FileInput from "../../../../../../../common/component/input/FileInput";
import Button from "../../../../../../../common/component/input/Button";
import Utils from "../../../../../../../common/js/Utils";
import Endpoints from "../../../../../../../common/js/dao/dao";
import downloadFile from "../../../../FileDownloader";

const ImageColumn = ({
    columnData,
    updateColumn,
    editingEnabled = true,
    selectType,
    localizationHandler,
    addFileToColum
}) => {
    const [file, setFile] = useState(null);
    const [preview, setPreview] = useState(null);
    const [overwriteFile, setOverwriteFile] = useState(editingEnabled);

    useEffect(() => updateData(), [file]);
    useEffect(() => displayPreview(), [columnData.data]);
    useEffect(
        () => {
            if (overwriteFile && Utils.hasValue(columnData.data)) {
                columnData.data.storedFileId = null;
            }
        },
        [overwriteFile]
    );

    const displayPreview = () => {
        if (overwriteFile) {
            if (file) {
                const objectUrl = URL.createObjectURL(file.file);
                setPreview(objectUrl);

                return () => URL.revokeObjectURL(objectUrl)
            } else {
                setPreview(null);
            }
        } else if (Utils.hasValue(columnData.data) && Utils.hasValue(columnData.data.storedFileId)) {
            setPreview(Endpoints.STORAGE_DOWNLOAD_FILE.assembleUrl({ storedFileId: columnData.data.storedFileId }));
        }
    }

    const updateData = () => {
        if (Utils.hasValue(columnData.data) && Utils.hasValue(columnData.data.storedFileId)) {
            return;
        }

        if (file == null) {
            columnData.data = null;
        } else {
            addFileToColum(columnData.columnIndex, file);

            columnData.data = {
                fileName: file.fileName,
                size: file.size
            }
        }

        updateColumn();
    }

    if (editingEnabled) {
        return (
            <td className={"table-column editable notebook-table-column-type-" + columnData.columnType.toLowerCase()}>
                <div className="table-column-wrapper">
                    <div className="table-column-content">
                        {overwriteFile &&
                            <FileInput
                                onchangeCallback={setFile}
                                accept="image/png, image/gif, image/jpeg, image/jpg, image/bmp"
                            />
                        }

                        {preview &&
                            <img
                                className="notebook-table-image-preview"
                                src={preview}
                            />
                        }
                    </div>

                    {!overwriteFile &&
                        <Button
                            className="notebook-table-overwrite-file-button"
                            onclick={() => setOverwriteFile(true)}
                            title={localizationHandler.get("overwrite-file")}
                        />
                    }


                    <Button
                        className="notebook-table-change-column-type-button"
                        onclick={selectType}
                        title={localizationHandler.get("change-column-type")}
                    />
                </div>
            </td>
        );
    } else {
        return (
            <td className={"table-column editable notebook-table-column-type-" + columnData.columnType.toLowerCase()}>
                {preview &&
                    <div className="table-column-wrapper">
                        <div className="table-column-content">
                            <img
                                className="notebook-table-image-preview"
                                src={preview}
                            />

                            <Button
                                className="notebook-table-download-file-button"
                                onclick={() => downloadFile(columnData.data.storedFileId)}
                                title={localizationHandler.get("overwrite-file")}
                            />
                        </div>
                    </div>
                }
            </td>
        )
    }
}

export default ImageColumn;