package com.iverhun.demo.inventory;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ItemPriceDto {

    @NotNull
    private final long retailPrice;
}
