package com.github.saphyra.apphub.service.custom.elite_base.dao.item.type;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.dao.AbstractDao;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.custom.elite_base.common.EliteBaseProperties;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemType;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@Slf4j
//TODO unit test
public class ItemTypeDao extends AbstractDao<ItemTypeEntity, ItemTypeDto, String, ItemTypeRepository> {
    private final CountDownLatch latch = new CountDownLatch(1);
    private final Set<ItemTypeDto> cache = new CopyOnWriteArraySet<>();
    private final ExecutorServiceBean executorServiceBean;
    private final EliteBaseProperties eliteBaseProperties;

    ItemTypeDao(ItemTypeConverter converter, ItemTypeRepository repository, ExecutorServiceBean executorServiceBean, EliteBaseProperties eliteBaseProperties) {
        super(converter, repository);
        this.executorServiceBean = executorServiceBean;
        this.eliteBaseProperties = eliteBaseProperties;
    }

    @Override
    public void delete(ItemTypeDto itemTypeDto) {
        throw new UnsupportedOperationException("ItemType deletion is not supported.");
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException("ItemType deletion is not supported.");
    }

    @Override
    public void deleteAll(List<ItemTypeDto> itemTypeDtos) {
        throw new UnsupportedOperationException("ItemType deletion is not supported.");
    }

    @Override
    public void deleteById(String s) {
        throw new UnsupportedOperationException("ItemType deletion is not supported.");
    }

    @Override
    public List<ItemTypeDto> findAll() {
        waitForCacheLoaded();

        return new ArrayList<>(cache);
    }

    @Override
    public List<ItemTypeDto> findAllById(Iterable<String> itemNames) {
        waitForCacheLoaded();

        Set<String> itemNamesSet = StreamSupport.stream(itemNames.spliterator(), false)
            .collect(Collectors.toSet());

        return cache.stream()
            .filter(itemTypeDto -> itemNamesSet.contains(itemTypeDto.getItemName()))
            .toList();
    }

    public ItemTypeDto findByIdValidated(String commodityName) {
        return findById(commodityName)
            .orElseThrow(() -> ExceptionFactory.notFound("ItemType not found for commodityName " + commodityName));
    }

    @Override
    public Optional<ItemTypeDto> findById(String itemName) {
        waitForCacheLoaded();

        return cache.stream()
            .filter(itemTypeDto -> itemTypeDto.getItemName().equals(itemName))
            .findFirst();
    }

    @Override
    public void save(ItemTypeDto itemTypeDto) {
        if (cache.add(itemTypeDto)) {
            super.save(itemTypeDto);
        }
    }

    @Override
    public void saveAll(Collection<ItemTypeDto> itemTypeDtos) {
        List<ItemTypeDto> toBeSaved = itemTypeDtos.stream()
            .filter(itemTypeDto -> !cache.contains(itemTypeDto))
            .toList();

        if (!toBeSaved.isEmpty()) {
            cache.addAll(toBeSaved);
            super.saveAll(toBeSaved);
        }
    }

    @SneakyThrows
    private void waitForCacheLoaded() {
        if (!latch.await(eliteBaseProperties.getCache().getCacheReadTimeout().toSeconds(), TimeUnit.SECONDS)) {
            throw ExceptionFactory.loggedException(HttpStatus.REQUEST_TIMEOUT, ErrorCode.TEMPORARILY_NOT_AVAILABLE, "ItemTypeCache is not loaded yet.");
        }
    }

    @EventListener
    public void init(ApplicationReadyEvent event) {
        executorServiceBean.execute(this::load);
    }

    private void load() {
        StopWatch stopWatch = StopWatch.createStarted();
        log.info("ItemType loading started...");

        cache.addAll(converter.convertEntity(StreamSupport.stream(repository.findAll().spliterator(), false).toList()));
        latch.countDown();

        stopWatch.stop();
        log.info("{} ItemType were loaded in {} ms.", cache.size(), stopWatch.getTime(TimeUnit.MILLISECONDS));
    }

    /**
     * Gets all the known item names for the given types
     *
     * @param types to filter for
     * @return all the known item names
     */
    public List<String> getItemNames(List<ItemType> types) {
        waitForCacheLoaded();

        return cache.stream()
            .filter(itemTypeDto -> types.contains(itemTypeDto.getType()))
            .map(ItemTypeDto::getItemName)
            .toList();
    }

    public void saveAll(ItemType type, List<String> commodityNames) {
        List<ItemTypeDto> dtos = commodityNames.stream()
            .map(commodityName -> ItemTypeDto.builder()
                .itemName(commodityName)
                .type(type)
                .build())
            .toList();
        saveAll(dtos);
    }
}
