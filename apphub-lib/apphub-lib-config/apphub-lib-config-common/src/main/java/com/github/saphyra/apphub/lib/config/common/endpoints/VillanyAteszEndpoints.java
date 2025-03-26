package com.github.saphyra.apphub.lib.config.common.endpoints;

public class VillanyAteszEndpoints {
    //Contact
    public static final String VILLANY_ATESZ_CREATE_CONTACT = "/api/villany-atesz/contact";
    public static final String VILLANY_ATESZ_EDIT_CONTACT = "/api/villany-atesz/contact/{contactId}";
    public static final String VILLANY_ATESZ_DELETE_CONTACT = "/api/villany-atesz/contact/{contactId}";
    public static final String VILLANY_ATESZ_GET_CONTACTS = "/api/villany-atesz/contact";

    //Stock Category
    public static final String VILLANY_ATESZ_CREATE_STOCK_CATEGORY = "/api/villany-atesz/stock/category";
    public static final String VILLANY_ATESZ_EDIT_STOCK_CATEGORY = "/api/villany-atesz/stock/category/{stockCategoryId}";
    public static final String VILLANY_ATESZ_DELETE_STOCK_CATEGORY = "/api/villany-atesz/stock/category/{stockCategoryId}";
    public static final String VILLANY_ATESZ_GET_STOCK_CATEGORIES = "/api/villany-atesz/stock/category";

    //Stock Item
    public static final String VILLANY_ATESZ_CREATE_STOCK_ITEM = "/api/villany-atesz/stock/item";
    public static final String VILLANY_ATESZ_DELETE_STOCK_ITEM = "/api/villany-atesz/stock/item/{stockItemId}";
    public static final String VILLANY_ATESZ_GET_STOCK_ITEMS = "/api/villany-atesz/stock/item";
    public static final String VILLANY_ATESZ_GET_STOCK_ITEMS_FOR_CATEGORY = "/api/villany-atesz/stock/category/{stockCategoryId}";
    public static final String VILLANY_ATESZ_GET_STOCK_ITEM = "/api/villany-atesz/stock/item/{stockItemId}";
    public static final String VILLANY_ATESZ_FIND_STOCK_ITEM_BY_BAR_CODE = "/api/villany-atesz/stock/item/bar-code";
    public static final String VILLANY_ATESZ_FIND_BAR_CODE_BY_STOCK_ITEM_ID = "/api/villany-atesz/stock/item/{stockItemId}/bar-code";
    public static final String VILLANY_ATESZ_INDEX_TOTAL_STOCK_VALUE = "/api/villany-atesz/index/total-value/stock";

    //Cart
    public static final String VILLANY_ATESZ_CREATE_CART = "/api/villany-atesz/cart";
    public static final String VILLANY_ATESZ_GET_CARTS = "/api/villany-atesz/cart";
    public static final String VILLANY_ATESZ_GET_CART = "/api/villany-atesz/cart/{cartId}";
    public static final String VILLANY_ATESZ_ADD_TO_CART = "/api/villany-atesz/cart";
    public static final String VILLANY_ATESZ_FINALIZE_CART = "/api/villany-atesz/cart/{cartId}";
    public static final String VILLANY_ATESZ_DELETE_CART = "/api/villany-atesz/cart/{cartId}";
    public static final String VILLANY_ATESZ_REMOVE_FROM_CART = "/api/villany-atesz/cart/{cartId}/item/{stockItemId}";
    public static final String VILLANY_ATESZ_CART_EDIT_MARGIN = "/api/villany-atesz/cart/{cartId}/margin";

    //Acquisition
    public static final String VILLANY_ATESZ_STOCK_ACQUIRE = "/api/villany-atesz/stock/acquire";
    public static final String VILLANY_ATESZ_INDEX_GET_STOCK_ITEMS_MARKED_FOR_ACQUISITION = "/api/villany-atesz/index/stock-item/marked-for-acquisition";
    public static final String VILLANY_ATESZ_GET_ACQUISITION_DATES = "/api/villany-atesz/acquisition";
    public static final String VILLANY_ATESZ_GET_ACQUISITIONS = "/api/villany-atesz/acquisition/{acquiredAt}";

    //Stock Inventory
    public static final String VILLANY_ATESZ_STOCK_INVENTORY_GET_ITEMS = "/api/villany-atesz/stock/inventory";
    public static final String VILLANY_ATESZ_STOCK_INVENTORY_EDIT_NAME = "/api/villany-atesz/stock/inventory/{stockItemId}/name";
    public static final String VILLANY_ATESZ_STOCK_INVENTORY_EDIT_CATEGORY = "/api/villany-atesz/stock/inventory/{stockItemId}/category";
    public static final String VILLANY_ATESZ_STOCK_INVENTORY_EDIT_SERIAL_NUMBER = "/api/villany-atesz/stock/inventory/{stockItemId}/serial-number";
    public static final String VILLANY_ATESZ_STOCK_INVENTORY_EDIT_BAR_CODE = "/api/villany-atesz/stock/inventory/{stockItemId}/bar-code";
    public static final String VILLANY_ATESZ_STOCK_INVENTORY_EDIT_IN_CAR = "/api/villany-atesz/stock/inventory/{stockItemId}/in-car";
    public static final String VILLANY_ATESZ_STOCK_INVENTORY_EDIT_IN_STORAGE = "/api/villany-atesz/stock/inventory/{stockItemId}/in-storage";
    public static final String VILLANY_ATESZ_STOCK_INVENTORY_EDIT_INVENTORIED = "/api/villany-atesz/stock/inventory/{stockItemId}/inventoried";
    public static final String VILLANY_ATESZ_STOCK_INVENTORY_EDIT_MARKED_FOR_ACQUISITION = "/api/villany-atesz/stock/inventory/{stockItemId}/marked-for-acquisition";
    public static final String VILLANY_ATESZ_MOVE_STOCK_TO_CAR = "/api/villany-atesz/stock/item/{stockItemId}/to-car";
    public static final String VILLANY_ATESZ_MOVE_STOCK_TO_STORAGE = "/api/villany-atesz/stock/item/{stockItemId}/to-storage";
    public static final String VILLANY_ATESZ_RESET_INVENTORIED = "/api/villany-atesz/stock/inventory/reset-inventoried";

    //Toolbox
    public static final String VILLANY_ATESZ_INDEX_TOTAL_TOOLBOX_VALUE = "/api/villany-atesz/index/total-value/toolbox";
    public static final String VILLANY_ATESZ_GET_TOOLS = "/api/villany-atesz/tool";
    public static final String VILLANY_ATESZ_CREATE_TOOL = "/api/villany-atesz/tool";
    public static final String VILLANY_ATESZ_SET_TOOL_STATUS = "/api/villany-atesz/tool/{toolId}";
    public static final String VILLANY_ATESZ_DELETE_TOOL = "/api/villany-atesz/tool/{toolId}";

    //Toolbox inventory
    public static final String VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_STORAGE_BOX = "/api/villany-atesz/tool/inventory/{toolId}/storage-box";
    public static final String VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_TOOL_TYPE = "/api/villany-atesz/tool/inventory/{toolId}/tool-type";
    public static final String VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_BRAND = "/api/villany-atesz/tool/inventory/{toolId}/brand";
    public static final String VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_NAME = "/api/villany-atesz/tool/inventory/{toolId}/name";
    public static final String VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_COST = "/api/villany-atesz/tool/inventory/{toolId}/cost";
    public static final String VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_ACQUIRED_AT = "/api/villany-atesz/tool/inventory/{toolId}/acquired-at";
    public static final String VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_WARRANTY_EXPIRES_AT = "/api/villany-atesz/tool/inventory/{toolId}/warranty-expires-at";
    public static final String VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_STATUS = "/api/villany-atesz/tool/inventory/{toolId}/status";
    public static final String VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_SCRAPPED_AT = "/api/villany-atesz/tool/inventory/{toolId}/scrapped-at";
    public static final String VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_INVENTORIED = "/api/villany-atesz/tool/inventory/{toolId}/inventoried";
    public static final String VILLANY_ATESZ_TOOLBOX_INVENTORY_RESET_INVENTORIED = "/api/villany-atesz/tool/inventory/reset-inventoried";

    //Tool Type
    public static final String VILLANY_ATESZ_GET_TOOL_TYPES = "/api/villany-atesz/tool/type";
    public static final String VILLANY_ATESZ_EDIT_TOOL_TYPE = "/api/villany-atesz/tool/tool-type/{toolTypeId}";
    public static final String VILLANY_ATESZ_DELETE_TOOL_TYPE = "/api/villany-atesz/tool/tool-type/{toolTypeId}";

    //Storage Box
    public static final String VILLANY_ATESZ_GET_STORAGE_BOXES = "/api/villany-atesz/toolbox/storage-box";
    public static final String VILLANY_ATESZ_EDIT_STORAGE_BOX = "/api/villany-atesz/tool/storage-box/{storageBoxId}";
    public static final String VILLANY_ATESZ_DELETE_STORAGE_BOX = "/api/villany-atesz/tool/storage-box/{storageBoxId}";

    //Commission
    public static final String VILLANY_ATESZ_COMMISSION_CREATE_OR_UPDATE = "/api/villany-atesz/commissions";
    public static final String VILLANY_ATESZ_COMMISSION_DELETE = "/api/villany-atesz/commissions/{commissionId}";
    public static final String VILLANY_ATESZ_COMMISSIONS_GET = "/api/villany-atesz/commissions";
    public static final String VILLANY_ATESZ_COMMISSION_GET = "/api/villany-atesz/commissions/{commissionId}";
    public static final String VILLANY_ATESZ_COMMISSION_GET_CART = "/api/villany-atesz/commissions/{cartId}/cart";
}
