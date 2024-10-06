import { STORAGE_DOWNLOAD_FILE } from "../../../common/js/dao/endpoints/StorageEndpoints";

const downloadFile = (storedFileId) => {
    window.open(STORAGE_DOWNLOAD_FILE.assembleUrl({ storedFileId: storedFileId }));
}

export default downloadFile;