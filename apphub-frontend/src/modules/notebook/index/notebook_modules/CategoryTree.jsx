import React, { useEffect, useState } from "react";
import "./category_tree/category_tree.css";
import Leaf from "./category_tree/Leaf";
import EventName from "../../../../common/js/event/EventName";
import { useUpdateEffect } from "react-use";
import useHasFocus from "../../../../common/hook/UseHasFocus";
import { NOTEBOOK_GET_CATEGORY_TREE } from "../../../../common/js/dao/endpoints/NotebookEndpoints";

const CategoryTree = ({ localizationHandler, setOpenedListItem, lastEvent, setLastEvent, userSettings, setDisplaySpinner }) => {
    const [tree, setTree] = useState([]);
    const [openedLeaves, setOpenedLeavesD] = useState(sessionStorage.openedLeaves ? JSON.parse(sessionStorage.openedLeaves) : [null]);

    useEffect(() => processEvent(), [lastEvent]);
    useEffect(() => loadTree(), []);

    const isInFocus = useHasFocus();
    useUpdateEffect(() => {
        if (isInFocus) {
            loadTree();
        }
    }, [isInFocus]);

    const setOpenedLeaves = (newOpenedLeaves) => {
        sessionStorage.openedLeaves = JSON.stringify(newOpenedLeaves);
        setOpenedLeavesD(newOpenedLeaves);
    }

    const processEvent = () => {
        if (lastEvent === null) {
            return;
        }

        switch (lastEvent.eventName) {
            case EventName.NOTEBOOK_LIST_ITEM_DELETED:
            case EventName.NOTEBOOK_LIST_ITEM_ARCHIVED:
            case EventName.NOTEBOOK_LIST_ITEM_CLONED:
            case EventName.NOTEBOOK_LIST_ITEM_MODIFIED:
                loadTree();
                break;
        }
    }

    const loadTree = () => {
        const fetch = async () => {
            const response = await NOTEBOOK_GET_CATEGORY_TREE.createRequest()
                .send(setDisplaySpinner);
            setTree(response);
        }
        fetch();
    }

    const createRoot = () => {
        return {
            archived: false,
            categoryId: null,
            children: tree,
            title: localizationHandler.get("root")
        }
    }

    return (
        <div id="notebook-category-tree">
            <Leaf
                category={createRoot()}
                openedLeaves={openedLeaves}
                setOpenedLeaves={setOpenedLeaves}
                setOpenedListItem={setOpenedListItem}
                setLastEvent={setLastEvent}
                userSettings={userSettings}
            />
        </div>
    );
}

export default CategoryTree;