import React, { useEffect, useState } from "react";
import LocalizationHandler from "../../../../common/js/LocalizationHandler";
import localizationData from "./new_category_localization.json";
import { useParams } from "react-router";
import sessionChecker from "../../../../common/js/SessionChecker";
import NotificationService from "../../../../common/js/notification/NotificationService";
import Header from "../../../../common/component/Header";
import Footer from "../../../../common/component/Footer";
import Button from "../../../../common/component/input/Button";
import Constants from "../../../../common/js/Constants";
import ListItemTitle from "../../common/list_item_title/ListItemTitle";
import ParentSelector from "../../common/parent_selector/ParentSelector";
import { ToastContainer } from "react-toastify";
import validateListItemTitle from "../../common/validator/ListItemTitleValidator";
import { NOTEBOOK_CREATE_CATEGORY } from "../../../../common/js/dao/endpoints/NotebookEndpoints";

const NewCategoryPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");

    const { parent } = useParams();
    const [parentId, setParentId] = useState(parent === "null" ? null : parent);

    const [listItemTitle, setListItemTitle] = useState("");

    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);

    const create = async () => {
        const result = validateListItemTitle(listItemTitle);
        if (!result.valid) {
            NotificationService.showError(result.message);
            return;
        }

        const payload = {
            parent: parentId,
            title: listItemTitle
        }
        await NOTEBOOK_CREATE_CATEGORY.createRequest(payload)
            .send();

        window.location.href = Constants.NOTEBOOK_PAGE;
    }

    return (
        <div id="notebook-new-category" className="main-page">
            <Header label={localizationHandler.get("page-title")} />

            <main id="notebook-new-category-main">
                <ListItemTitle
                    inputId="notebook-new-category-title"
                    placeholder={localizationHandler.get("category-title")}
                    setListItemTitle={setListItemTitle}
                    value={listItemTitle}
                />

                <ParentSelector
                    parentId={parentId}
                    setParentId={setParentId}
                />
            </main>

            <Footer
                rightButtons={[
                    <Button
                        key="back-button"
                        id="notebook-new-category-back-button"
                        label={localizationHandler.get("back")}
                        onclick={() => window.location.href = Constants.NOTEBOOK_NEW_PAGE + "/" + parent}
                    />,
                    <Button
                        key="home-button"
                        id="notebook-new-category-home-button"
                        label={localizationHandler.get("home")}
                        onclick={() => window.location.href = Constants.NOTEBOOK_PAGE}
                    />
                ]}

                centerButtons={[
                    <Button
                        key="create-button"
                        id="notebook-new-category-create-button"
                        label={localizationHandler.get("create")}
                        onclick={() => create()}
                    />
                ]}
            />

            <ToastContainer />
        </div>
    );
}

export default NewCategoryPage;