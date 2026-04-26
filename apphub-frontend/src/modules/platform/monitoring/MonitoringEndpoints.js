import Endpoint from "common/js/dao/Endpoint";
import RequestMethod from "common/js/dao/RequestMethod";

export const MONITORING_PAGE = "/web/monitoring";

export const MONITORING_GET_FEATURES = new Endpoint(RequestMethod.GET, "/api/monitoring/features");
export const MONITORING_GET_FUNCTIONALITIES = new Endpoint(RequestMethod.GET, "/api/monitoring/features/{feature}/functionalities");
export const MONITORING_GET_SERVICES = new Endpoint(RequestMethod.GET, "/api/monitoring/{feature}/services");
export const MONITORING_GET_METRICS = new Endpoint(RequestMethod.GET, "/api/monitoring/metrics/{type}");