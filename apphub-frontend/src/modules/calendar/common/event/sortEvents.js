function sortEvents(a, b) {
    if (Boolean(a.archived) === Boolean(b.archived)) {
        return a.title.localeCompare(b.title);
    }

    return a.archived ? 1 : -1;
}

export default sortEvents;