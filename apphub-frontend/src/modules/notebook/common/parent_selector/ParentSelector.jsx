import React, { useEffect, useState } from "react";
import localizationData from "./parent_selector_localization.json";
import LocalizationHandler from "../../../../common/js/LocalizationHandler";
import "./parent_selector.css";
import Button from "../../../../common/component/input/Button";
import Stream from "../../../../common/js/collection/Stream";
import Child from "./Child";
import { NOTEBOOK_GET_CHILDREN_OF_CATEGORY } from "../../../../common/js/dao/endpoints/NotebookEndpoints";

const ParentSelector = ({ parentId, setParentId, listItemId = null }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [currentCategoryData, setCurrentCategoryData] = useState({ children: [] });

    useEffect(() => loadCategory(), [parentId]);

    const loadCategory = () => {
        const fetch = async () => {
            const queryParams = {
                type: "CATEGORY"
            }

            if (parentId !== null) {
                queryParams.categoryId = parentId;
            }

            if (listItemId !== null) {
                queryParams.exclude = listItemId;
            }

            const response = await NOTEBOOK_GET_CHILDREN_OF_CATEGORY.createRequest(null, null, queryParams)
                .send();

            if (response.listItemType !== "CATEGORY") {
                setParentId(response.parent);
            }

            setCurrentCategoryData(response);
        }
        fetch();
    }

    const getCategories = () => {
        if (currentCategoryData.children.length === 0) {
            return (
                <div className="notebook-parent-selector-no-child">
                    {localizationHandler.get("no-child")}
                </div>
            );
        }

        return new Stream(currentCategoryData.children)
            .sorted((a, b) => a.title.toLowerCase().localeCompare(b.title.toLowerCase()))
            .map(child =>
                <Child
                    key={child.id}
                    data={child}
                    setParentId={setParentId}
                />
            )
            .toList()
    }

    return (
        <div className="notebook-parent-selector">
            <div className="notebook-parent-selector-selected-parent">
                <span>{localizationHandler.get("selected-parent")}:</span>
                <span> </span>
                <span id="notebook-parent-selector-selected-parent-title">{currentCategoryData.title === null ? localizationHandler.get("root") : currentCategoryData.title}</span>
            </div>

            <div className="notebook-parent-selector-up-button-wrapper">
                <Button
                    className="notebook-parent-selector-up-button"
                    onclick={() => setParentId(currentCategoryData.parent)}
                    disabled={parentId === null}
                    label={localizationHandler.get("up")}
                />
            </div>

            <div className="notebook-parent-selector-children">
                {getCategories()}
            </div>
        </div>
    );
}

export default ParentSelector;