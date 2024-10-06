import OpenedPageType from "../../../../common/OpenedPageType";

const compareListItems = (a, b) => {
    if (a.archived && !b.archived) {
        return 1;
    }

    if (!a.archived && b.archived) {
        return -1;
    }

    if (a.type === b.type) {
        return compareByTitle(a, b);
    }

    if (a.type === OpenedPageType.CATEGORY) {
        return -1;
    }

    if (b.type === OpenedPageType.CATEGORY) {
        return 1;
    }

    return compareByTitle(a, b);
}

const compareByTitle = (a, b) => {
    return a.title.localeCompare(b.title);
}

export default compareListItems;