(function ImageViewController(){
    let openedFileId = null;

    window.imageViewController = new function(){
        this.viewImage = viewImage;
        this.downloadImage = function(){
            window.open(Mapping.getEndpoint("STORAGE_DOWNLOAD_FILE", {storedFileId: openedFileId}).getUrl())
        }
    }

    function viewImage(listItemId, title, fileId){
        openedFileId = fileId;
        document.getElementById(ids.viewImageTitle).innerText = title;
        document.getElementById(ids.viewImageImg).src = Mapping.getEndpoint("STORAGE_DOWNLOAD_FILE", {storedFileId: fileId}).getUrl();

        switchTab("main-page", "view-image");
        switchTab("button-wrapper", "view-image-buttons");
    }
})();