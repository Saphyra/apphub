import { Endpoint, RequestMethod } from "../../../../common/js/dao/dao";

export const ELITE_BASE_STAR_SYSTEMS_SEARCH = new Endpoint(RequestMethod.POST, "/api/elite-base/star-systems/search");
export const ELITE_BASE_GET_POWERS = new Endpoint(RequestMethod.GET, "/api/elite-base/powers");
export const ELITE_BASE_GET_POWERPLAY_STATES = new Endpoint(RequestMethod.GET, "/api/elite-base/powers/powerplay-states");
export const ELITE_BASE_NEAREST_MATERIAL_TRADERS = new Endpoint(RequestMethod.GET, "/api/elite-base/nearest/{starId}/material-traders/{materialType}/{page}");
export const ELITE_BASE_COMMODITY_TRADING_COMMODITIES = new Endpoint(RequestMethod.GET, "/api/elite-base/commodity-trading/commodities");
export const ELITE_BASE_COMMODITY_TRADING_COMMODITIES_AVERAGE_PRICE = new Endpoint(RequestMethod.GET, "/api/elite-base/commodity-trading/commodities/{commodityName}/average-price");
export const ELITE_BASE_COMMODITY_TRADING_TRADE = new Endpoint(RequestMethod.POST, "/api/elite-base/commodity-trading/trade");
export const ELITE_BASE_MATERIAL_TRADER_OVERRIDE_CREATE = new Endpoint(RequestMethod.PUT, "/api/elite-base/material-trader-override");
export const ELITE_BASE_IS_ADMIN = new Endpoint(RequestMethod.GET, "/api/elite-base/admin");
export const ELITE_BASE_MATERIAL_TRADER_OVERRIDE_VERIFY = new Endpoint(RequestMethod.POST, "/api/elite-base/material-trader-override/{stationId}");
export const ELITE_BASE_MATERIAL_TRADER_OVERRIDE_DELETE = new Endpoint(RequestMethod.DELETE, "/api/elite-base/material-trader-override/{stationId}");