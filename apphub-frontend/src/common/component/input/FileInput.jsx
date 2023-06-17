import React from "react";

const FileInput = ({ id, className, onchangeCallback, accept }) => {
    const onchange = (e) => {
        if (!e.target.files || e.target.files.length === 0) {
            onchangeCallback(null);
            return;
        }

        const file = e.target.files[0];
        const fileData = {
            fileName: file.name,
            size: file.size,
            file: file,
            e: e
        }
        onchangeCallback(fileData);
    }

    return (
        <input
            id={id}
            className={className}
            type="file"
            onChange={onchange}
            accept={accept}
        />
    );
}

export default FileInput;