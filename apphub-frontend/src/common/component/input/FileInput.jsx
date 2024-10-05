import React from "react";

const FileInput = ({ id, className, onchangeCallback, accept, multiple = false }) => {
    const onchange = (e) => {
        if (!e.target.files || e.target.files.length === 0) {
            onchangeCallback(null);
            return;
        }

        if (multiple) {
            const result = [];

            for (let i = 0; i < e.target.files.length; i++) {
                const file = e.target.files[i];
                const fileData = {
                    fileName: file.name,
                    size: file.size,
                    file: file,
                    e: e
                }

                result.push(fileData);
            }

            onchangeCallback(result);
        } else {
            const file = e.target.files[0];
            const fileData = {
                fileName: file.name,
                size: file.size,
                file: file,
                e: e
            }
            onchangeCallback(fileData);
        }
    }

    return (
        <input
            id={id}
            className={className}
            type="file"
            onChange={onchange}
            accept={accept}
            multiple={multiple}
        />
    );
}

export default FileInput;