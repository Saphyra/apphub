import React, { useEffect, useState } from "react";
import FileInput from "../../../../../../common/component/input/FileInput";
import Button from "../../../../../../common/component/input/Button";

const Image = ({ data, updateData, selectType, localizationHandler }) => {
    const [preview, setPreview] = useState(null);

    useEffect(() => displayPreview(), [data]);

    const displayPreview = () => {
        if (!data) {
            setPreview(null);
            return;
        }

        const objectUrl = URL.createObjectURL(data.file);
        setPreview(objectUrl);

        return () => URL.revokeObjectURL(objectUrl)
    }

    return (
        <div>
            <FileInput
                onchangeCallback={updateData}
                accept="image/png, image/gif, image/jpeg, image/jpg, image/bmp"
            />

            <Button
                className="notebook-custom-table-change-column-type-button"
                onclick={selectType}
                title={localizationHandler.get("change-column-type")}
            />

            {preview &&
                <div className="notebook-new-custom-table-image-preview-wrapper">
                    <img
                        className="notebook-new-custom-table-image-preview"
                        src={preview}
                    />
                </div>
            }
        </div>
    );
}

export default Image;