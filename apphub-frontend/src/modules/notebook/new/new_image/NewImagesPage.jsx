import React, { useEffect, useState } from "react";
import LocalizationHandler from "../../../../common/js/LocalizationHandler";
import localizationData from "./new_image_localization.json";
import { useParams } from "react-router";
import sessionChecker from "../../../../common/js/SessionChecker";
import NotificationService from "../../../../common/js/notification/NotificationService";
import create from "./NewImageSaver";
import Constants from "../../../../common/js/Constants";
import Header from "../../../../common/component/Header";
import ParentSelector from "../../common/parent_selector/ParentSelector";
import ImageGroupData from "./group/ImageGroupData";
import ImageGroup from "./group/ImageGroup";
import Footer from "../../../../common/component/Footer";
import Button from "../../../../common/component/input/Button";
import { ToastContainer } from "react-toastify";
import Spinner from "../../../../common/component/Spinner";
import Stream from "../../../../common/js/collection/Stream";
import { addAndSet } from "../../../../common/js/Utils";

const NewImagesPage = ()  => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");

    const { parent } = useParams();
    const [parentId, setParentId] = useState(parent === "null" ? null : parent);
    const [imageGroups, setImageGroups] = useState([new ImageGroupData()]);
    const [displaySpinner, setDisplaySpinner] = useState(false);

    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);

    const getImageGroups = () => {
        return new Stream(imageGroups)
            .map(imageGroup => <ImageGroup
                key={imageGroup.id}
                imageGroup={imageGroup}
                imageGroups={imageGroups}
                setImageGroups={setImageGroups}
            />)
            .toList();
    }

    const uploadImages = async () => {
        const files = new Stream(imageGroups)
            .flatMap(fileGroup => new Stream(fileGroup.files))
            .toList();

        const allUploadSuccessful = true;
        for (let i = 0; i < files.length; i++) {
            if (!await uploadFile(files[i])) {
                allUploadSuccessful = false;
            }
        }

        if (allUploadSuccessful === true) {
            window.location.href = Constants.NOTEBOOK_PAGE;
        } else {
            NotificationService.showError(localizationHandler.get("file-upload-failed"));
        }
    }

    const uploadFile = async (file) => {
        return await create(file.fileName, file, parentId, setDisplaySpinner);
    }

    return (
        <div id="notebook-new-file" className="main-page">
            <Header label={localizationHandler.get("page-title")} />

            <main id="notebook-new-file-main">
                <ParentSelector
                    parentId={parentId}
                    setParentId={setParentId}
                />

                <div id="notebook-new-files-content-wrapper">
                    {getImageGroups()}
                </div>
            </main>

            <Footer
                leftButtons={[
                    <Button
                        key="add-group"
                        id="notebook-new-files"
                        label={localizationHandler.get("add-group")}
                        onclick={() => addAndSet(imageGroups, new ImageGroupData(), setImageGroups)}
                    />
                ]}

                rightButtons={[
                    <Button
                        key="back-button"
                        id="notebook-new-file-back-button"
                        label={localizationHandler.get("back")}
                        onclick={() => window.location.href = Constants.NOTEBOOK_NEW_PAGE + "/" + parent}
                    />,
                    <Button
                        key="home-button"
                        id="notebook-new-file-home-button"
                        label={localizationHandler.get("home")}
                        onclick={() => window.location.href = Constants.NOTEBOOK_PAGE}
                    />
                ]}

                centerButtons={[
                    <Button
                        key="create-button"
                        id="notebook-new-file-create-button"
                        label={localizationHandler.get("create")}
                        onclick={() => uploadImages()}
                    />
                ]}
            />

            <ToastContainer />

            {displaySpinner && <Spinner />}
        </div>
    );
}

export default NewImagesPage;