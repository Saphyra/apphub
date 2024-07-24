import React, { useEffect, useState } from "react";
import localizationData from "./new_file_localization.json";
import LocalizationHandler from "../../../../common/js/LocalizationHandler";
import { useParams } from "react-router";
import sessionChecker from "../../../../common/js/SessionChecker";
import NotificationService from "../../../../common/js/notification/NotificationService";
import Header from "../../../../common/component/Header";
import ListItemTitle from "../../common/list_item_title/ListItemTitle";
import ParentSelector from "../../common/parent_selector/ParentSelector";
import FileInput from "../../../../common/component/input/FileInput";
import Footer from "../../../../common/component/Footer";
import Button from "../../../../common/component/input/Button";
import Constants from "../../../../common/js/Constants";
import { ToastContainer } from "react-toastify";
import Spinner from "../../../../common/component/Spinner";
import "./new_file.css";
import create from "./NewFileSaver";

const NewFilePage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");

    const { parent } = useParams();
    const [parentId, setParentId] = useState(parent === "null" ? null : parent);
    const [listItemTitle, setListItemTitle] = useState("");
    const [file, setFile] = useState(null);
    const [displaySpinner, setDisplaySpinner] = useState(false);

    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);

    const save = async () => {
        const fileUploadSuccessful = await create(listItemTitle, file, parentId, setDisplaySpinner);
        if (fileUploadSuccessful) {
            window.location.href = Constants.NOTEBOOK_PAGE;
        };
    }

    return (
        <div id="notebook-new-file" className="main-page">
            <Header label={localizationHandler.get("page-title")} />

            <main id="notebook-new-file-main">
                <ListItemTitle
                    inputId="notebook-new-file-title"
                    placeholder={localizationHandler.get("file-title")}
                    setListItemTitle={setListItemTitle}
                    value={listItemTitle}
                />

                <ParentSelector
                    parentId={parentId}
                    setParentId={setParentId}
                />

                <div id="notebook-new-file-content-wrapper">
                    <FileInput
                        id="notebook-new-file-input"
                        onchangeCallback={setFile}
                    />
                </div>
            </main>

            <Footer
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
                        onclick={() => save()}
                    />
                ]}
            />

            <ToastContainer />

            {displaySpinner && <Spinner />}
        </div>
    );
}

export default NewFilePage;