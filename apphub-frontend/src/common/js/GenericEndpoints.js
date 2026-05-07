import Endpoint from "./dao/Endpoint";
import RequestMethod from "./dao/RequestMethod";

//Pages
export const INDEX_PAGE = "/web";
export const ERROR_PAGE = "/web/error";

//Platform
export const CHECK_SESSION = new Endpoint(RequestMethod.GET, "/api/session");
export const GET_OWN_USER_ID = new Endpoint(RequestMethod.GET, "/user/id");
export const USER_DATA_SEARCH_ACCOUNT = new Endpoint(RequestMethod.POST, "/api/user/accounts");
export const GET_WEB_SOCKET_PROTOCOL = new Endpoint(RequestMethod.GET, "/api/ws/protocol");
export const IS_ADMIN = new Endpoint(RequestMethod.GET, "/api/user/admin");

//Storage
export const STORAGE_UPLOAD_FILE = new Endpoint(RequestMethod.PUT, "/api/storage/{storedFileId}");
export const STORAGE_DOWNLOAD_FILE = new Endpoint(RequestMethod.GET, "/api/storage/{storedFileId}");
export const STORAGE_GET_METADATA = new Endpoint(RequestMethod.GET, "/api/storage/{storedFileId}/metadata");

//User settings
export const GET_USER_SETTINGS = new Endpoint(RequestMethod.GET, "/api/user/settings/{category}");
export const SET_USER_SETTINGS = new Endpoint(RequestMethod.POST, "/api/user/settings");