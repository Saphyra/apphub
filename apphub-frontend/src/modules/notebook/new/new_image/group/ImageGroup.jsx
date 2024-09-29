import React, { useEffect, useState } from "react";
import Stream from "../../../../../common/js/collection/Stream";
import FileInput from "../../../../../common/component/input/FileInput";
import "./image_group.css";
import { copyAndSet } from "../../../../../common/js/Utils";

const ImageGroup = ({ imageGroup, imageGroups, setImageGroups }) => {
    const [files, setFiles] = useState([]);
    const [previews, setPreviews] = useState([]);

    useEffect(() => displayPreview(), [files]);

    const displayPreview = () => {
        const objectUrls = new Stream(files)
            .map(image => URL.createObjectURL(image.file))
            .toList();

        setPreviews(objectUrls);

        return () => new Stream(objectUrls)
            .forEach(objectUrl => URL.revokeObjectURL(objectUrl));
    }

    const updateFile = (file) => {
        setFiles(file);

        imageGroup.files = file;

        copyAndSet(imageGroups, setImageGroups);
    }

    const getFileData = () => {
        return new Stream(previews)
            .map((preview, index) => (
                <div
                    key={index}
                    className="notebook-new-images-group-image-wrapper"
                >
                    <img src={preview} />
                </div>
            ))
            .toList();
    }

    return (
        <div className="notebook-new-images-group">
            <div className="notebook-new-images-group-images">
                {getFileData()}
            </div>

            <FileInput
                className="notebook-new-image-input"
                onchangeCallback={updateFile}
                multiple={true}
                accept="image/png, image/gif, image/jpeg, image/jpg, image/bmp"
            />
        </div>
    );
}

export default ImageGroup;