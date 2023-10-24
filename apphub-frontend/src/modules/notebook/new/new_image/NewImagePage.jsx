import React, { useEffect, useState } from "react";
import LocalizationHandler from "../../../../common/js/LocalizationHandler";
import localizationData from "./new_image_localization.json";
import { useParams } from "react-router";
import sessionChecker from "../../../../common/js/SessionChecker";
import NotificationService from "../../../../common/js/notification/NotificationService";
import Header from "../../../../common/component/Header";
import Footer from "../../../../common/component/Footer";
import Button from "../../../../common/component/input/Button";
import Constants from "../../../../common/js/Constants";
import { ToastContainer } from "react-toastify";
import ListItemTitle from "../../common/list_item_title/ListItemTitle";
import ParentSelector from "../../common/parent_selector/ParentSelector";
import FileInput from "../../../../common/component/input/FileInput";
import "./new_image.css";
import validateListItemTitle from "../../common/validator/ListItemTitleValidator";
import validateFile from "../../common/validator/FileValidator";
import Spinner from "../../../../common/component/Spinner";
import Endpoints from "../../../../common/js/dao/dao";
import Utils from "../../../../common/js/Utils";
import Response from "../../../../common/js/dao/Response";
import getDefaultErrorHandler from "../../../../common/js/dao/DefaultErrorHandler";

const NewImagePage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");

    const { parent } = useParams();
    const [parentId, setParentId] = useState(parent === "null" ? null : parent);
    const [listItemTitle, setListItemTitle] = useState("");
    const [image, setImage] = useState(null);
    const [preview, setPreview] = useState(null);
    const [displaySpinner, setDisplaySpinner] = useState(false);

    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);
    useEffect(() => displayPreview(), [image]);

    const displayPreview = () => {
        if (!image) {
            setPreview(null);
            return;
        }

        const objectUrl = URL.createObjectURL(image.file);
        setPreview(objectUrl)

        return () => URL.revokeObjectURL(objectUrl)
    }

    const create = async () => {
        const listItemTitleValidationResult = validateListItemTitle(listItemTitle);
        if (!listItemTitleValidationResult.valid) {
            NotificationService.showError(listItemTitleValidationResult.message);
            return;
        }

        const imageValidationResult = validateFile(image);
        if (!imageValidationResult.valid) {
            NotificationService.showError(imageValidationResult.message);
            return;
        }

        setDisplaySpinner(true);

        const payload = {
            title: listItemTitle,
            parent: parentId,
            metadata: {
                fileName: image.fileName,
                size: image.size
            }
        }

        const storedFileResponse = await Endpoints.NBOTEBOOK_CREATE_IMAGE.createRequest(payload)
            .send();

        const formData = new FormData();
        formData.append("file", image.e.target.files[0]);

        const options = {
            method: "PUT",
            body: formData,
            headers: {
                'Cache-Control': "no-cache",
                "BrowserLanguage": Utils.getBrowserLanguage(),
                "Request-Type": Constants.HEADER_REQUEST_TYPE_VALUE
            }
        }

        await fetch(Endpoints.STORAGE_UPLOAD_FILE.assembleUrl({ storedFileId: storedFileResponse.value }), options)
            .then(r => {
                setDisplaySpinner(false);

                if (!r.ok) {
                    r.text()
                        .then(body => {
                            const response = new Response(r.status, body);
                            getDefaultErrorHandler()
                                .handle(response);
                        });
                } else {
                    window.location.href = Constants.NOTEBOOK_PAGE;
                }
            });
    }

    return (
        <div id="notebook-new-image" className="main-page">
            <Header label={localizationHandler.get("page-title")} />

            <main id="notebook-new-image-main">
                <ListItemTitle
                    inputId="notebook-new-image-title"
                    placeholder={localizationHandler.get("image-title")}
                    setListItemTitle={setListItemTitle}
                    value={listItemTitle}
                />

                <ParentSelector
                    parentId={parentId}
                    setParentId={setParentId}
                />

                <div id="notebook-new-image-content-wrapper">
                    <FileInput
                        id="notebook-new-image-input"
                        onchangeCallback={setImage}
                        accept="image/png, image/gif, image/jpeg, image/jpg, image/bmp"
                    />

                    {preview &&
                        <div id="notebook-new-image-preview-wrapper">
                            <img
                                id="notebook-new-image-preview"
                                src={preview}
                            />
                        </div>
                    }
                </div>
            </main>

            <Footer
                rightButtons={[
                    <Button
                        key="back-button"
                        id="notebook-new-image-back-button"
                        label={localizationHandler.get("back")}
                        onclick={() => window.location.href = Constants.NOTEBOOK_NEW_PAGE + "/" + parent}
                    />,
                    <Button
                        key="home-button"
                        id="notebook-new-image-home-button"
                        label={localizationHandler.get("home")}
                        onclick={() => window.location.href = Constants.NOTEBOOK_PAGE}
                    />
                ]}

                centerButtons={[
                    <Button
                        key="create-button"
                        id="notebook-new-image-create-button"
                        label={localizationHandler.get("create")}
                        onclick={() => create()}
                    />
                ]}
            />

            <ToastContainer />

            {displaySpinner && <Spinner />}
        </div>
    );
}

export default NewImagePage;