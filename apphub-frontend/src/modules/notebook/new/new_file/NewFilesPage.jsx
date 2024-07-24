import React, { useEffect, useState } from "react";
import localizationData from "./new_file_localization.json";
import LocalizationHandler from "../../../../common/js/LocalizationHandler";
import { useParams } from "react-router";
import sessionChecker from "../../../../common/js/SessionChecker";
import NotificationService from "../../../../common/js/notification/NotificationService";
import Header from "../../../../common/component/Header";
import ParentSelector from "../../common/parent_selector/ParentSelector";
import Footer from "../../../../common/component/Footer";
import Button from "../../../../common/component/input/Button";
import Constants from "../../../../common/js/Constants";
import { ToastContainer } from "react-toastify";
import Spinner from "../../../../common/component/Spinner";
import "./new_file.css";
import Stream from "../../../../common/js/collection/Stream";
import FileGroup from "./group/FileGroup";
import FileGroupData from "./group/FileGroupData";
import Utils from "../../../../common/js/Utils";
import create from "./NewFileSaver";

const NewFilesPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");

    const { parent } = useParams();
    const [parentId, setParentId] = useState(parent === "null" ? null : parent);
    const [fileGroups, setFileGroups] = useState([new FileGroupData()]);
    const [displaySpinner, setDisplaySpinner] = useState(false);

    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);

    const getFileGroups = () => {
        return new Stream(fileGroups)
            .map(fileGroup => <FileGroup
                key={fileGroup.id}
                fileGroup={fileGroup}
                fileGroups={fileGroups}
                setFileGroups={setFileGroups}
            />)
            .toList();
    }

    const uploadFiles = async () => {
        const files = new Stream(fileGroups)
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
                    {getFileGroups()}
                </div>
            </main>

            <Footer
                leftButtons={[
                    <Button
                        key="add-group"
                        id="notebook-new-files"
                        label={localizationHandler.get("add-group")}
                        onclick={() => Utils.addAndSet(fileGroups, new FileGroupData(), setFileGroups)}
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
                        onclick={() => uploadFiles()}
                    />
                ]}
            />

            <ToastContainer />

            {displaySpinner && <Spinner />}
        </div>
    );
}

export default NewFilesPage;