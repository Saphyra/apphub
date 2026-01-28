package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.dao;

import com.github.saphyra.apphub.api.custom.elite_base.model.commodity_trading.CommodityTradingRequest;
import com.github.saphyra.apphub.api.custom.elite_base.model.commodity_trading.TradeMode;
import com.github.saphyra.apphub.lib.common_util.DateTimeConverter;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.sql_builder.BetweenCondition;
import com.github.saphyra.apphub.lib.sql_builder.Column;
import com.github.saphyra.apphub.lib.sql_builder.Equation;
import com.github.saphyra.apphub.lib.sql_builder.NamedColumn;
import com.github.saphyra.apphub.lib.sql_builder.Operation;
import com.github.saphyra.apphub.lib.sql_builder.OperationCondition;
import com.github.saphyra.apphub.lib.sql_builder.OrderType;
import com.github.saphyra.apphub.lib.sql_builder.PowerSegment;
import com.github.saphyra.apphub.lib.sql_builder.QualifiedColumn;
import com.github.saphyra.apphub.lib.sql_builder.QualifiedTable;
import com.github.saphyra.apphub.lib.sql_builder.SelectQuery;
import com.github.saphyra.apphub.lib.sql_builder.SqlBuilder;
import com.github.saphyra.apphub.lib.sql_builder.SquareRootSegment;
import com.github.saphyra.apphub.lib.sql_builder.SubtractSegment;
import com.github.saphyra.apphub.lib.sql_builder.SumSegment;
import com.github.saphyra.apphub.lib.sql_builder.WrappedValue;
import com.github.saphyra.apphub.service.custom.elite_base.common.EliteBaseProperties;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemLocationType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.Offer;
import com.github.saphyra.apphub.service.custom.elite_base.util.ConversionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_BUY_PRICE;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_DEMAND;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_DISTANCE_FROM_REFERENCE;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_EXTERNAL_REFERENCE;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_ID;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_ITEM_NAME;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_LAST_UPDATE;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_LOCATION_TYPE;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_SELL_PRICE;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_STAR_NAME;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_STAR_SYSTEM_ID;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_STOCK;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_TYPE;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_X_POS;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_Y_POS;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_Z_POS;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.TABLE_ITEM_COMMODITY;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.TABLE_LAST_UPDATE;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.TABLE_STAR_SYSTEM;

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
            request.getItemName()
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

    private String getSql(int offset, OrderType orderType, TradeMode tradeMode, StarSystem referenceSystem, int minTradeAmount, int minPrice, int maxPrice, Duration maxAge, String itemName) {
        SelectQuery query = SqlBuilder.select()
            .from(new QualifiedTable(SCHEMA, TABLE_ITEM_COMMODITY));
        addColumns(query, tradeMode, referenceSystem);
        addJoins(query);
        addConditions(query, tradeMode, minTradeAmount, minPrice, maxPrice, maxAge, itemName);
        query.orderBy(getOrderByColumn(tradeMode), orderType);
        query.offset(offset);
        query.limit(eliteBaseProperties.getSearchPageSize());

        String sql = query.build();
        log.debug("Get offers SQL: {}", sql);

        return sql;
    }

    private void addConditions(SelectQuery query, TradeMode tradeMode, int minTradeAmount, int minPrice, int maxPrice, Duration maxAge, String itemName) {
        query.condition(new OperationCondition(
                new QualifiedColumn(TABLE_ITEM_COMMODITY, getAmountColumn(tradeMode)),
                Operation.GREATER_OR_EQUAL,
                new WrappedValue(minTradeAmount)
            ))
            .and()
            .condition(new BetweenCondition(
                new QualifiedColumn(TABLE_ITEM_COMMODITY, getPriceColumn(tradeMode)),
                new WrappedValue(minPrice),
                new WrappedValue(maxPrice)
            ))
            .and()
            .condition(new OperationCondition(
                new QualifiedColumn(TABLE_LAST_UPDATE, COLUMN_LAST_UPDATE),
                Operation.GREATER_OR_EQUAL,
                new WrappedValue(dateTimeConverter.convertDomain(dateTimeUtil.getCurrentDateTime().minus(maxAge)))
            ))
            .and()
            .condition(new Equation(
                new QualifiedColumn(TABLE_ITEM_COMMODITY, COLUMN_ITEM_NAME),
                new WrappedValue(itemName)
            ));
    }

    private void addJoins(SelectQuery query) {
        query.innerJoin(
            new QualifiedTable(SCHEMA, TABLE_LAST_UPDATE),
            new Equation(
                new QualifiedColumn(TABLE_ITEM_COMMODITY, COLUMN_EXTERNAL_REFERENCE),
                new QualifiedColumn(TABLE_LAST_UPDATE, COLUMN_EXTERNAL_REFERENCE)
            ),
            new Equation(
                new QualifiedColumn(TABLE_LAST_UPDATE, COLUMN_TYPE),
                new WrappedValue(ItemType.COMMODITY.name())
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

        query.column(new QualifiedColumn(TABLE_LAST_UPDATE, COLUMN_LAST_UPDATE));
        query.column(new QualifiedColumn(TABLE_STAR_SYSTEM, COLUMN_STAR_NAME));
        query.column(new NamedColumn(
            new SquareRootSegment(
                new SumSegment(
                    new PowerSegment(
                        new SubtractSegment(
                            new QualifiedColumn(TABLE_STAR_SYSTEM, COLUMN_X_POS),
                            new WrappedValue(referenceSystem.getPosition().getX())
                        ),
                        2
                    ),
                    new PowerSegment(
                        new SubtractSegment(
                            new QualifiedColumn(TABLE_STAR_SYSTEM, COLUMN_Y_POS),
                            new WrappedValue(referenceSystem.getPosition().getY())
                        ),
                        2
                    ),
                    new PowerSegment(
                        new SubtractSegment(
                            new QualifiedColumn(TABLE_STAR_SYSTEM, COLUMN_Z_POS),
                            new WrappedValue(referenceSystem.getPosition().getZ())
                        ),
                        2
                    )
                )
            ),
            COLUMN_DISTANCE_FROM_REFERENCE
        ));
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
