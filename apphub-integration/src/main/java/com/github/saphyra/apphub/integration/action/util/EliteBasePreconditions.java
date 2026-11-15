package com.github.saphyra.apphub.integration.action.util;

import com.github.saphyra.apphub.integration.framework.db.DatabaseUtil;
import org.testng.SkipException;

public class EliteBasePreconditions {
    public static String checkMaterialTraderPreconditions() {
        if (!DatabaseUtil.materialTraderExists()) {
            throw new SkipException("Material trader does not exist in the database. Skipping test.");
        }

        return DatabaseUtil.anyStarSystemName();
    }

    public static String checkBuyCommodityPreconditions(String commodity) {
        return DatabaseUtil.starSystemSellingCommodity(commodity)
            .orElseThrow(() -> new SkipException("No star system found selling commodity: " + commodity));
    }

    public static String checkSellCommodityPreconditions(String commodity) {
        return DatabaseUtil.starSystemBuyingCommodity(commodity)
            .orElseThrow(() -> new SkipException("No star system found buying commodity: " + commodity));
    }
}
