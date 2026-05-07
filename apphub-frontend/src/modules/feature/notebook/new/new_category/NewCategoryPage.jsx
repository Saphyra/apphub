import LocalizationHandler from "common/js/LocalizationHandler";
import localizationData from "./new_category_localization.json";
import { useParams } from "react-router";
import { useEffect, useState } from "react";
import NotificationService from "common/js/notification/NotificationService";
import validateListItemTitle from "../../common/validator/ListItemTitleValidator";
import Header from "common/component/Header";
import ListItemTitle from "../../common/list_item_title/ListItemTitle";
import ParentSelector from "../../common/parent_selector/ParentSelector";
import Footer from "common/component/Footer";
import Button from "common/component/input/Button";
import { ToastContainer } from "react-toastify";
import { NOTEBOOK_CREATE_CATEGORY, NOTEBOOK_NEW_PAGE, NOTEBOOK_PAGE } from "../../NotebookEndpoints";

const NewCategoryPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");

    const { parent } = useParams();
    const [parentId, setParentId] = useState(parent === "null" ? null : parent);

    const [listItemTitle, setListItemTitle] = useState("");

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

        window.location.href = NOTEBOOK_PAGE;
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
                        onclick={() => window.location.href = NOTEBOOK_NEW_PAGE.assembleUrl({ parent: parent })}
                    />,
                    <Button
                        key="home-button"
                        id="notebook-new-category-home-button"
                        label={localizationHandler.get("home")}
                        onclick={() => window.location.href = NOTEBOOK_PAGE}
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