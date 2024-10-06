import React, { useEffect, useState } from "react";
import FileInput from "../../../../../../../common/component/input/FileInput";
import Button from "../../../../../../../common/component/input/Button";
import downloadFile from "../../../../FileDownloader";
import { formatFileSize, hasValue } from "../../../../../../../common/js/Utils";
import { STORAGE_GET_METADATA } from "../../../../../../../common/js/dao/endpoints/StorageEndpoints";

const FileColumn = ({
    columnData,
    updateColumn,
    editingEnabled = true,
    selectType,
    localizationHandler,
    addFileToColum
}) => {
    const [file, setFile] = useState(null);
    const [fileMetadata, setFileMetadata] = useState(null);
    const [overwriteFile, setOverwriteFile] = useState(editingEnabled);

    useEffect(() => updateData(), [file]);
    useEffect(() => loadFileMetadata(), [columnData.data]);
    useEffect(
        () => {
            if (overwriteFile && hasValue(columnData.data)) {
                columnData.data.storedFileId = null;
            }
        },
        [overwriteFile]
    );

    const loadFileMetadata = () => {
        if (!hasValue(columnData.data) || !hasValue(columnData.data.storedFileId)) {
            return;
        }

        const fetch = async () => {
            const response = await STORAGE_GET_METADATA.createRequest(null, { storedFileId: columnData.data.storedFileId })
                .send();
            setFileMetadata(response);
        }
        fetch();
    }

    const updateData = () => {
        if (hasValue(columnData.data) && hasValue(columnData.data.storedFileId)) {
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
                <div className="notebook-table-column-wrapper">
                    <div className="notebook-table-column-content">
                        {overwriteFile &&
                            <FileInput
                                onchangeCallback={setFile}
                            />
                        }

                        {!overwriteFile && fileMetadata &&
                            <FileMetadataTable
                                fileMetadata={fileMetadata}
                                localizationHandler={localizationHandler}
                                editingEnabled={editingEnabled}
                                storedFileId={columnData.data.storedFileId}
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
                {fileMetadata &&
                    <div className="notebook-table-column-wrapper">
                        <div className="notebook-table-column-content">
                            <FileMetadataTable
                                fileMetadata={fileMetadata}
                                localizationHandler={localizationHandler}
                                editingEnabled={editingEnabled}
                                storedFileId={columnData.data.storedFileId}
                            />
                        </div>
                    </div>
                }
            </td>
        )
    }
}

const FileMetadataTable = ({ fileMetadata, localizationHandler, editingEnabled, storedFileId }) => {
    return (
        <table className="formatted-table content-selectable">
            <tbody>
                <tr>
                    <td >{localizationHandler.get("file-name")}</td>
                    <td >{fileMetadata.fileName}</td>
                    {!editingEnabled &&
                        <td rowSpan={2}>
                            <Button
                                className="notebook-table-download-file-button"
                                onclick={() => downloadFile(storedFileId)}
                                title={localizationHandler.get("download-file")}
                            />
                        </td>
                    }
                </tr>
                <tr>
                    <td >{localizationHandler.get("file-size")}</td>
                    <td >{formatFileSize(fileMetadata.size)}</td>
                </tr>
            </tbody>
        </table>
    )
}

export default FileColumn;