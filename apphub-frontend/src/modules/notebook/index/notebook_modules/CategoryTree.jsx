import React, { useEffect, useState } from "react";
import Endpoints from "../../../../common/js/dao/dao";
import "./category_tree/category_tree.css";
import Leaf from "./category_tree/Leaf";
import EventName from "../../../../common/js/event/EventName";
import useHasFocus from "../../../../common/js/UseHasFocus";
import { useUpdateEffect } from "react-use";

const CategoryTree = ({ localizationHandler, setOpenedListItem, lastEvent, setLastEvent, userSettings }) => {
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
            const response = await Endpoints.NOTEBOOK_GET_CATEGORY_TREE.createRequest()
                .send();
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