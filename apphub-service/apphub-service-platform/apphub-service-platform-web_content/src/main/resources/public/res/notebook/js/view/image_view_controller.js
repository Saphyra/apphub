(function ImageViewController(){
    window.imageViewController = new function(){
        this.viewImage = viewImage;
    }

    function viewImage(listItemId, title, fileId){
        document.getElementById(ids.viewImageTitle).innerText = title;
        document.getElementById(ids.viewImageImg).src = Mapping.getEndpoint("STORAGE_DOWNLOAD_FILE", {storedFileId: fileId}).getUrl();

        switchTab("main-page", "view-image");
        switchTab("button-wrapper", "view-image-buttons");
    }
})();