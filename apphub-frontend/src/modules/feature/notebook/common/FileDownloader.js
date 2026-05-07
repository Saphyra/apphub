import { STORAGE_DOWNLOAD_FILE } from "common/js/GenericEndpoints";

const downloadFile = (storedFileId) => {
    window.open(STORAGE_DOWNLOAD_FILE.assembleUrl({ storedFileId: storedFileId }));
}

export default downloadFile;