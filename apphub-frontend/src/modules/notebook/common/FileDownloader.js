import Endpoints from "../../../common/js/dao/dao";

const downloadFile = (storedFileId) => {
    window.open(Endpoints.STORAGE_DOWNLOAD_FILE.assembleUrl({ storedFileId: storedFileId }));
}

export default downloadFile;