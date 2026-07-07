export const replaceTabsWithSpaces = (e) => {
    if (e.key === "Tab") {
        e.preventDefault();

        const start = e.target.selectionStart;
        const end = e.target.selectionEnd;
        e.target.value = e.target.value.substring(0, start) + "    " + e.target.value.substring(end);
        e.target.selectionStart = e.target.selectionEnd = start + 4;
    }
}