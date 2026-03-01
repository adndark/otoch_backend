package com.otoch.service;

import com.otoch.model.Item;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ItemService {

    private final Map<Long, Item> items = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public ItemService() {
        Item sample1 = new Item(idGenerator.getAndIncrement(), "Sample Item 1", "A sample item for testing", 19.99);
        Item sample2 = new Item(idGenerator.getAndIncrement(), "Sample Item 2", "Another sample item", 29.99);
        Item sample3 = new Item(idGenerator.getAndIncrement(), "Sample Item 2", "Another THIRD sample item", 29.99);
        items.put(sample1.getId(), sample1);
        items.put(sample2.getId(), sample2);
        items.put(sample2.getId(), sample3);
    }

    public List<Item> getAllItems() {
        return new ArrayList<>(items.values());
    }

    public Optional<Item> getItemById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    public Item createItem(Item item) {
        item.setId(idGenerator.getAndIncrement());
        items.put(item.getId(), item);
        return item;
    }

    public Optional<Item> updateItem(Long id, Item updatedItem) {
        if (!items.containsKey(id)) {
            return Optional.empty();
        }
        updatedItem.setId(id);
        items.put(id, updatedItem);
        return Optional.of(updatedItem);
    }

    public boolean deleteItem(Long id) {
        return items.remove(id) != null;
    }
}
