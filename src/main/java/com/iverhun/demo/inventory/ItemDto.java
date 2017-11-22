package com.iverhun.demo.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ItemDto {
    // TODO: add validation constraints

    private String name;
    private String vendor;
    private LocalDate expirationDate;
    private long retailPrice;

    public Item toItem() {
        return Item.builder()
                .name(name)
                .retailPrice(retailPrice)
                .vendor(vendor)
                .expirationDate(expirationDate)
                .build();
    }
}
