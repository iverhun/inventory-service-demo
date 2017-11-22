package com.iverhun.demo.inventory;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.core.Relation;

import java.time.LocalDate;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Relation(value = "item", collectionRelation = "items")
public class ItemResource extends ResourceSupport {
    private String name;
    private String vendor;

    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate expirationDate;
    private long retailPrice;

    public ItemResource(Item item) {
        name = item.getName();
        vendor = item.getVendor();
        expirationDate = item.getExpirationDate();
        retailPrice = item.getRetailPrice();
        add(linkTo(methodOn(ItemController.class).getItem(item.getName())).withSelfRel());
    }
}
