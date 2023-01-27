(function FileViewController(){
    let openedFileId = null;

    window.fileViewController = new function(){
        this.viewFile = viewFile;
        this.downloadImage = function(){
            window.open(Mapping.getEndpoint("STORAGE_DOWNLOAD_FILE", {storedFileId: openedFileId}).getUrl())
        }
    }

    function viewFile(listItemId, title, fileId){
        openedFileId = fileId;

        const request = new Request(Mapping.getEndpoint("STORAGE_GET_METADATA", {storedFileId: openedFileId}));
            request.convertResponse = jsonConverter;
            request.processValidResponse = function(metadata){
                document.getElementById(ids.viewFileTitle).innerText = title;
                document.getElementById(ids.viewFileName).innerText = metadata.fileName;
                document.getElementById(ids.viewFileSize).innerText = formatFileSize(metadata.size);
                document.getElementById(ids.viewFileCreatedAt).innerText = LocalDateTime.fromEpochSeconds(metadata.createdAt);

                switchTab("main-page", "view-file");
                switchTab("button-wrapper", "view-file-buttons");
            }
        dao.sendRequestAsync(request);
    }
})();