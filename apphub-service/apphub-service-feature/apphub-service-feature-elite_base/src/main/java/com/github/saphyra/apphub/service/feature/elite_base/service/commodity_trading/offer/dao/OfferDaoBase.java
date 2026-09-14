package com.github.saphyra.apphub.service.feature.elite_base.service.commodity_trading.offer.dao;

import com.github.saphyra.apphub.api.feature.elite_base.model.commodity_trading.CommodityTradingRequest;
import com.github.saphyra.apphub.api.feature.elite_base.model.commodity_trading.TradeMode;
import com.github.saphyra.apphub.lib.common_util.DateTimeConverter;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.sql_builder.condition.BetweenCondition;
import com.github.saphyra.apphub.lib.sql_builder.column.Column;
import com.github.saphyra.apphub.lib.sql_builder.operation.Equation;
import com.github.saphyra.apphub.lib.sql_builder.column.NamedColumn;
import com.github.saphyra.apphub.lib.sql_builder.value.NumberValue;
import com.github.saphyra.apphub.lib.sql_builder.operation.Operation;
import com.github.saphyra.apphub.lib.sql_builder.condition.OperationCondition;
import com.github.saphyra.apphub.lib.sql_builder.keyword.OrderType;
import com.github.saphyra.apphub.lib.sql_builder.keyword.PowerSegment;
import com.github.saphyra.apphub.lib.sql_builder.column.QualifiedColumn;
import com.github.saphyra.apphub.lib.sql_builder.table.QualifiedTable;
import com.github.saphyra.apphub.lib.sql_builder.query.SelectQuery;
import com.github.saphyra.apphub.lib.sql_builder.SqlBuilder;
import com.github.saphyra.apphub.lib.sql_builder.operation.SquareRootSegment;
import com.github.saphyra.apphub.lib.sql_builder.operation.SubtractSegment;
import com.github.saphyra.apphub.lib.sql_builder.operation.SumSegment;
import com.github.saphyra.apphub.lib.sql_builder.value.WrappedValue;
import com.github.saphyra.apphub.service.feature.elite_base.common.EliteBaseProperties;
import com.github.saphyra.apphub.service.feature.elite_base.dao.item.ItemLocationType;
import com.github.saphyra.apphub.service.feature.elite_base.dao.ObjectType;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.feature.elite_base.service.commodity_trading.offer.Offer;
import com.github.saphyra.apphub.service.feature.elite_base.util.ConversionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_BUY_PRICE;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_DEMAND;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_DISTANCE_FROM_REFERENCE;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_EXTERNAL_REFERENCE;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_ID;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_ITEM_NAME;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_LAST_UPDATE;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_LOCATION_TYPE;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_SELL_PRICE;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_STAR_NAME;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_STAR_SYSTEM_ID;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_STOCK;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_OBJECT_TYPE;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_X_POS;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_Y_POS;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_Z_POS;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.TABLE_ITEM_COMMODITY;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.TABLE_LAST_UPDATE_V2;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.TABLE_STAR_SYSTEM;

@Slf4j
abstract class OfferDaoBase implements OfferDao {
    private final JdbcTemplate jdbcTemplate;
    private final DateTimeUtil dateTimeUtil;
    private final DateTimeConverter dateTimeConverter;
    private final UuidConverter uuidConverter;
    private final EliteBaseProperties eliteBaseProperties;

    OfferDaoBase(OfferDaoContext context) {
        this.jdbcTemplate = context.getJdbcTemplate();
        this.dateTimeUtil = context.getDateTimeUtil();
        this.uuidConverter = context.getUuidConverter();
        this.dateTimeConverter = context.getDateTimeConverter();
        this.eliteBaseProperties = context.getEliteBaseProperties();
    }

    @Override
    public List<Offer> getOffers(int offset, CommodityTradingRequest request, StarSystem referenceSystem) {
        String sql = getSql(
            offset,
            ConversionUtils.toOrderType(request.getOrder()),
            request.getTradeMode(),
            referenceSystem,
            request.getMinTradeAmount(),
            request.getMinPrice(),
            request.getMaxPrice(),
            request.getMaxTimeSinceLastUpdated(),
            request.getItemName(),
            request.getMaxStarSystemDistance()
        );

        return query(sql, request.getTradeMode());
    }

    private List<Offer> query(String sql, TradeMode tradeMode) {
        return jdbcTemplate.query(
            sql,
            rs -> {
                List<Offer> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(mapRow(rs, tradeMode));
                }
                return result;
            });
    }

    private Offer mapRow(ResultSet rs, TradeMode tradeMode) throws SQLException {
        return Offer.builder()
            .externalReference(uuidConverter.convertEntity(rs.getString(COLUMN_EXTERNAL_REFERENCE)))
            .locationType(ItemLocationType.valueOf(rs.getString(COLUMN_LOCATION_TYPE)))
            .price(rs.getInt(getPriceColumn(tradeMode)))
            .amount(rs.getInt(getAmountColumn(tradeMode)))
            .starSystemId(uuidConverter.convertEntity(rs.getString(COLUMN_STAR_SYSTEM_ID)))
            .starName(rs.getString(COLUMN_STAR_NAME))
            .lastUpdate(dateTimeConverter.convertToLocalDateTime(rs.getString(COLUMN_LAST_UPDATE)))
            .distanceFromReferenceSystem(rs.getDouble(COLUMN_DISTANCE_FROM_REFERENCE))
            .build();
    }

    private String getSql(int offset, OrderType orderType, TradeMode tradeMode, StarSystem referenceSystem, int minTradeAmount, int minPrice, int maxPrice, Duration maxAge, String itemName, int maxDistance) {
        SelectQuery query = SqlBuilder.select()
            .from(new QualifiedTable(SCHEMA, TABLE_ITEM_COMMODITY));
        addColumns(query, tradeMode, referenceSystem);
        addJoins(query);
        addConditions(query, tradeMode, minTradeAmount, minPrice, maxPrice, maxAge, itemName, maxDistance, referenceSystem);
        query.orderBy(getOrderByColumn(tradeMode), orderType);
        query.offset(offset);
        query.limit(eliteBaseProperties.getSearchPageSize());

        String sql = query.build();
        log.info("Get offers SQL: {}", sql);

        return sql;
    }

    private void addConditions(SelectQuery query, TradeMode tradeMode, int minTradeAmount, int minPrice, int maxPrice, Duration maxAge, String itemName, int maxDistance, StarSystem referenceSystem) {
        query
            .condition(new Equation(
                new QualifiedColumn(TABLE_ITEM_COMMODITY, COLUMN_ITEM_NAME),
                new WrappedValue(itemName)
            ))
            .and()
            .condition(new OperationCondition(
                new QualifiedColumn(TABLE_ITEM_COMMODITY, getAmountColumn(tradeMode)),
                Operation.GREATER_OR_EQUAL,
                new NumberValue(minTradeAmount)
            ))
            .and()
            .condition(new BetweenCondition(
                new QualifiedColumn(TABLE_ITEM_COMMODITY, getPriceColumn(tradeMode)),
                new NumberValue(minPrice),
                new NumberValue(maxPrice)
            ))
            .and()
            .condition(new OperationCondition(
                new QualifiedColumn(TABLE_LAST_UPDATE_V2, COLUMN_LAST_UPDATE),
                Operation.GREATER_OR_EQUAL,
                new WrappedValue(dateTimeConverter.convertDomain(dateTimeUtil.getCurrentDateTime().minus(maxAge)))
            ))
            .and()
            .condition(new OperationCondition(
                distanceSumSegment(referenceSystem),
                Operation.LOWER_OR_EQUAL,
                new NumberValue(Math.pow(maxDistance, 2))
            ));
    }

    private void addJoins(SelectQuery query) {
        query.innerJoin(
            new QualifiedTable(SCHEMA, TABLE_LAST_UPDATE_V2),
            new Equation(
                new QualifiedColumn(TABLE_ITEM_COMMODITY, COLUMN_EXTERNAL_REFERENCE),
                new QualifiedColumn(TABLE_LAST_UPDATE_V2, COLUMN_EXTERNAL_REFERENCE)
            ),
            new Equation(
                new QualifiedColumn(TABLE_LAST_UPDATE_V2, COLUMN_OBJECT_TYPE),
                new WrappedValue(ObjectType.COMMODITY.name())
            )
        );
        query.innerJoin(
            new QualifiedTable(SCHEMA, TABLE_STAR_SYSTEM),
            new QualifiedColumn(TABLE_ITEM_COMMODITY, COLUMN_STAR_SYSTEM_ID),
            new QualifiedColumn(TABLE_STAR_SYSTEM, COLUMN_ID)
        );
    }

    private void addColumns(SelectQuery query, TradeMode tradeMode, StarSystem referenceSystem) {
        Stream.of(
                COLUMN_EXTERNAL_REFERENCE,
                COLUMN_LOCATION_TYPE,
                getPriceColumn(tradeMode),
                getAmountColumn(tradeMode),
                COLUMN_STAR_SYSTEM_ID
            )
            .forEach(column -> query.column(new QualifiedColumn(TABLE_ITEM_COMMODITY, column)));

        query.column(new QualifiedColumn(TABLE_LAST_UPDATE_V2, COLUMN_LAST_UPDATE));
        query.column(new QualifiedColumn(TABLE_STAR_SYSTEM, COLUMN_STAR_NAME));
        query.column(new NamedColumn(
            new SquareRootSegment(
                distanceSumSegment(referenceSystem)
            ),
            COLUMN_DISTANCE_FROM_REFERENCE
        ));
    }

    private static SumSegment distanceSumSegment(StarSystem referenceSystem) {
        return new SumSegment(
            new PowerSegment(
                new SubtractSegment(
                    new QualifiedColumn(TABLE_STAR_SYSTEM, COLUMN_X_POS),
                    new NumberValue(referenceSystem.getPosition().getX())
                ),
                2
            ),
            new PowerSegment(
                new SubtractSegment(
                    new QualifiedColumn(TABLE_STAR_SYSTEM, COLUMN_Y_POS),
                    new NumberValue(referenceSystem.getPosition().getY())
                ),
                2
            ),
            new PowerSegment(
                new SubtractSegment(
                    new QualifiedColumn(TABLE_STAR_SYSTEM, COLUMN_Z_POS),
                    new NumberValue(referenceSystem.getPosition().getZ())
                ),
                2
            )
        );
    }

    private String getPriceColumn(TradeMode tradeMode) {
        return switch (tradeMode) {
            case BUY -> COLUMN_SELL_PRICE;
            case SELL -> COLUMN_BUY_PRICE;
        };
    }

    private String getAmountColumn(TradeMode tradeMode) {
        return switch (tradeMode) {
            case BUY -> COLUMN_STOCK;
            case SELL -> COLUMN_DEMAND;
        };
    }

    protected abstract Column getOrderByColumn(TradeMode tradeMode);
}
