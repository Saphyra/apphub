import { Endpoint, RequestMethod } from "../dao";

export const STORAGE_UPLOAD_FILE = new Endpoint(RequestMethod.PUT, "/api/storage/{storedFileId}");
export const STORAGE_DOWNLOAD_FILE = new Endpoint(RequestMethod.GET, "/api/storage/{storedFileId}");
export const STORAGE_GET_METADATA = new Endpoint(RequestMethod.GET, "/api/storage/{storedFileId}/metadata");