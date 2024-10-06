import React, { useState } from "react";
import FileInput from "../../../../../common/component/input/FileInput";
import Stream from "../../../../../common/js/collection/Stream";
import "./file_group.css";
import { copyAndSet, formatFileSize } from "../../../../../common/js/Utils";

const FileGroup = ({ fileGroup, fileGroups, setFileGroups }) => {
    const [files, setFiles] = useState(null);

    const updateFile = (file) => {
        setFiles(file);

        fileGroup.files = file;

        copyAndSet(fileGroups, setFileGroups);
    }

    const getFileData = () => {
        return new Stream(files)
            .map(fileData => (
                <tr key={fileData.fileName}>
                    <td>{fileData.fileName}</td>
                    <td>{formatFileSize(fileData.size)}</td>
                </tr>
            ))
            .toList();
    }

    return (
        <div className="notebook-new-files-group">
            <div className="notebook-new-files-group-files">
                <table className="formatted-table">
                    <tbody>
                        {getFileData()}
                    </tbody>
                </table>
            </div>

            <FileInput
                className="notebook-new-file-input"
                onchangeCallback={updateFile}
                multiple={true}
            />
        </div>
    );
}

export default FileGroup;