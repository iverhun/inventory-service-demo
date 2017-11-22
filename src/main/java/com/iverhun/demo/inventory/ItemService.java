package com.iverhun.demo.inventory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {
    private static final Logger log = LoggerFactory.getLogger(ItemService.class);

    @Autowired
    private ItemRepository itemRepository;

    public List<Item> getItems() {
        return itemRepository.findAll();
    }

    public Item getItem(String name) {
        return findOrThrow(name);
    }

    public void delete(String name) {
        Item item = findOrThrow(name);
        itemRepository.delete(item);
        log.info("Deleted item {}", name);
    }

    public Item create(Item item) {
        Item createdItem = itemRepository.save(item);
        log.info("Created item {}#{}", createdItem.getName(), createdItem.getId());
        return createdItem;
    }

    public Item updatePrice(String name, long price) {
        Item existingItem = findOrThrow(name);
        existingItem.setRetailPrice(price);
        Item updatedItem = itemRepository.save(existingItem);
        log.info("Updated item {}#{}. Price set to {}", updatedItem.getName(), updatedItem.getId(), updatedItem.getRetailPrice());
        return updatedItem;
    }

    private Item findOrThrow(String name) {
        return itemRepository.findByName(name).orElseThrow(() -> new RuntimeException("Item " + name + " doesn't exist"));
    }
}
