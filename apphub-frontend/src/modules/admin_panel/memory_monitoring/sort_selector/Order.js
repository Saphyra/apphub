export const ORDER_BY_SERVICE_NAME = "ORDER_BY_SERVICE_NAME";
export const ORDER_BY_AVAILABLE_MEMORY = "ORDER_BY_AVAILABLE_MEMORY";
export const ORDER_BY_ALLOCATED_MEMORY = "ORDER_BY_ALLOCATED_MEMORY";
export const ORDER_BY_USED_MEMORY = "ORDER_BY_USED_MEMORY";
export const ORDER_BY_MAX_USED_MEMORY = "ORDER_BY_MAX_USED_MEMORY";

export const OrderBy = {};
OrderBy[ORDER_BY_SERVICE_NAME] = (a, b) => a.serviceName.localeCompare(b.serviceName);
OrderBy[ORDER_BY_AVAILABLE_MEMORY] = (a, b) => a.availableMemory - b.availableMemory;
OrderBy[ORDER_BY_ALLOCATED_MEMORY] = (a, b) => a.allocatedMemory - b.allocatedMemory;
OrderBy[ORDER_BY_USED_MEMORY] = (a, b) => a.latestUsedMemory - b.latestUsedMemory;
OrderBy[ORDER_BY_MAX_USED_MEMORY] = (a, b) => a.maxUsedMemory - b.maxUsedMemory;

export const ORDER_ASCENDING = "ORDER_ASCENDING";
export const ORDER_DESCENDING = "ORDER_DESCENDING";

export const Order = {};
Order[ORDER_ASCENDING] = order => order;
Order[ORDER_DESCENDING] = order => -1 * order;