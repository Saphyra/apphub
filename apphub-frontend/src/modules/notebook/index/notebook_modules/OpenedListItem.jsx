import React from "react";
import OpenedPageType from "../../common/OpenedPageType";
import Category from "./opened_item/category/Category";
import Utils from "../../../../common/js/Utils";
import Text from "./opened_item/text/Text";
import "./opened_item/opened_list_item.css";
import Checklist from "./opened_item/checklist/Checklist";
import Table from "./opened_item/table/Table";
import Image from "./opened_item/image/Image";
import File from "./opened_item/file/File";
import Search from "./opened_item/category/Search";
import PinGroupManager from "./opened_item/manage_pin_groups/PinGroupManager";

const OpenedListItem = ({
    localizationHandler,
    openedListItem,
    setOpenedListItem,
    lastEvent,
    setLastEvent,
    userSettings,
    changeUserSettings,
    setConfirmationDialogData,
    setDisplaySpinner
}) => {
    console.log("Opened ListItem", openedListItem);

    const getContent = () => {
        switch (openedListItem.type) {
            case OpenedPageType.SEARCH:
                return <Search
                    localizationHandler={localizationHandler}
                    openedListItem={openedListItem}
                    setOpenedListItem={setOpenedListItem}
                    setLastEvent={setLastEvent}
                    lastEvent={lastEvent}
                    userSettings={userSettings}
                    changeUserSettings={changeUserSettings}
                    setConfirmationDialogData={setConfirmationDialogData}
                />
            case OpenedPageType.CATEGORY:
                return <Category
                    localizationHandler={localizationHandler}
                    openedListItem={openedListItem}
                    setOpenedListItem={setOpenedListItem}
                    setLastEvent={setLastEvent}
                    lastEvent={lastEvent}
                    userSettings={userSettings}
                    changeUserSettings={changeUserSettings}
                    setConfirmationDialogData={setConfirmationDialogData}
                />
            case OpenedPageType.TEXT:
                return <Text
                    localizationHandler={localizationHandler}
                    openedListItem={openedListItem}
                    setOpenedListItem={setOpenedListItem}
                    setLastEvent={setLastEvent}
                    setConfirmationDialogData={setConfirmationDialogData}
                />
            case OpenedPageType.CHECKLIST:
                return <Checklist
                    localizationHandler={localizationHandler}
                    openedListItem={openedListItem}
                    setOpenedListItem={setOpenedListItem}
                    setLastEvent={setLastEvent}
                    setConfirmationDialogData={setConfirmationDialogData}
                />
            case OpenedPageType.TABLE:
                return <Table
                    localizationHandler={localizationHandler}
                    openedListItem={openedListItem}
                    setOpenedListItem={setOpenedListItem}
                    setLastEvent={setLastEvent}
                    checklist={false}
                    setConfirmationDialogData={setConfirmationDialogData}
                />
            case OpenedPageType.CHECKLIST_TABLE:
                return <Table
                    localizationHandler={localizationHandler}
                    openedListItem={openedListItem}
                    setOpenedListItem={setOpenedListItem}
                    setLastEvent={setLastEvent}
                    checklist={true}
                    setConfirmationDialogData={setConfirmationDialogData}
                />
            case OpenedPageType.IMAGE:
                return <Image
                    localizationHandler={localizationHandler}
                    openedListItem={openedListItem}
                    setOpenedListItem={setOpenedListItem}
                />
            case OpenedPageType.FILE:
                return <File
                    localizationHandler={localizationHandler}
                    openedListItem={openedListItem}
                    setOpenedListItem={setOpenedListItem}
                />
            case OpenedPageType.CUSTOM_TABLE:
                return <Table
                    localizationHandler={localizationHandler}
                    openedListItem={openedListItem}
                    setOpenedListItem={setOpenedListItem}
                    setLastEvent={setLastEvent}
                    checklist={false}
                    setConfirmationDialogData={setConfirmationDialogData}
                    custom={true}
                    setDisplaySpinner={setDisplaySpinner}
                />
            case OpenedPageType.MANAGE_PIN_GROUPS:
                return <PinGroupManager
                    localizationHandler={localizationHandler}
                    openedListItem={openedListItem}
                    setOpenedListItem={setOpenedListItem}
                    setLastEvent={setLastEvent}
                    setConfirmationDialogData={setConfirmationDialogData}
                    lastEvent={lastEvent}
                />
            default:
                Utils.throwException("IllegalArgument", "Unhandled ListItemType in OpenedListItem: " + openedListItem.type);
        }
    }

    return getContent();
}

export default OpenedListItem;