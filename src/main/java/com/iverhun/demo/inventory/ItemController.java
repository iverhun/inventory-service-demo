package com.iverhun.demo.inventory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @RequestMapping(method = GET)
    public Resources<ItemResource> getItems() {
        List<ItemResource> itemResources = itemService.getItems()
                .stream()
                .map(ItemResource::new)
                .collect(toList());
        return new Resources<>(itemResources);
    }

    @RequestMapping(method = GET, path = "/{name}")
    public ResponseEntity<ItemResource> getItem(@PathVariable String name) {
        ItemResource itemResource = new ItemResource(itemService.getItem(name));
        return ResponseEntity.ok(itemResource);
    }

    @PostMapping
    public ResponseEntity<ItemResource> createItem(@Validated @RequestBody ItemDto itemDto) {
        Item item = itemService.create(itemDto.toItem());
        Link linkToItem = new ItemResource(item).getLink(Link.REL_SELF);
        return ResponseEntity.created(URI.create(linkToItem.getHref())).build();
    }
    
    @PutMapping("/{name}/price")
    public ResponseEntity<ItemResource> updateItemPrice(@PathVariable String name,
                                                           @Validated @RequestBody ItemPriceDto itemPriceDto) {
        Item updated = itemService.updatePrice(name, itemPriceDto.getRetailPrice());
        ItemResource resource = new ItemResource(updated);
        return ResponseEntity.ok(resource);
    }
    
    @DeleteMapping(path = "/{name}")
    public ResponseEntity<?> deleteItem(@PathVariable String name) {
        itemService.delete(name);
        return ResponseEntity.noContent().build();
    }
}
