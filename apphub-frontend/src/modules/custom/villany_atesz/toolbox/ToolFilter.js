import Optional from "../../../../common/js/collection/Optional";

const filterTool = (tool, search)  => {
    const lcSearch = search.toLowerCase();

    return search.length === 0
    || new Optional(tool.toolType).filter(toolType => toolType.name.toLowerCase().indexOf(lcSearch) >= 0).isPresent()
    || new Optional(tool.storageBox).filter(storageBox => storageBox.name.toLowerCase().indexOf(lcSearch) >= 0).isPresent()
    || tool.brand.toLowerCase().indexOf(lcSearch) >= 0
    || tool.name.toLowerCase().indexOf(lcSearch) >= 0
    || ("" + tool.cost).indexOf(lcSearch) >= 0
    || tool.acquiredAt.indexOf(lcSearch) >= 0
    || new Optional(tool.warrantyExpiresAt).filter(warrantyExpiresAt => warrantyExpiresAt.indexOf(lcSearch) >= 0).isPresent()
}

export default filterTool;