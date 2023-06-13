import React from "react";
import ListItemType from "../../common/ListItemType";
import Category from "./opened_item/category/Category";
import Utils from "../../../../common/js/Utils";
import Text from "./opened_item/text/Text";
import "./opened_item/opened_list_item.css";
import Checklist from "./opened_item/checklist/Checklist";
import Table from "./opened_item/table/Table";

const OpenedListItem = ({ localizationHandler, openedListItem, setOpenedListItem, lastEvent, setLastEvent }) => {
    const getContent = () => {
        switch (openedListItem.type) {
            case ListItemType.CATEGORY:
                return <Category
                    localizationHandler={localizationHandler}
                    openedListItem={openedListItem}
                    setOpenedListItem={setOpenedListItem}
                    setLastEvent={setLastEvent}
                    lastEvent={lastEvent}
                />
            case ListItemType.TEXT:
                return <Text
                    localizationHandler={localizationHandler}
                    openedListItem={openedListItem}
                    setOpenedListItem={setOpenedListItem}
                    setLastEvent={setLastEvent}
                />
            case ListItemType.CHECKLIST:
                return <Checklist
                    localizationHandler={localizationHandler}
                    openedListItem={openedListItem}
                    setOpenedListItem={setOpenedListItem}
                    setLastEvent={setLastEvent}
                />
            case ListItemType.TABLE:
                return <Table
                    localizationHandler={localizationHandler}
                    openedListItem={openedListItem}
                    setOpenedListItem={setOpenedListItem}
                    setLastEvent={setLastEvent}
                    checklist={false}
                />
            case ListItemType.CHECKLIST_TABLE:
                return <Table
                    localizationHandler={localizationHandler}
                    openedListItem={openedListItem}
                    setOpenedListItem={setOpenedListItem}
                    setLastEvent={setLastEvent}
                    checklist={true}
                />
            default:
                Utils.throwException("IllegalArgument", "Unhandled ListItemType: " + openedListItem.type);
        }
    }

    return getContent();
}

export default OpenedListItem;