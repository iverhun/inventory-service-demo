package com.iverhun.demo.inventory;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void testConnection() {
        // Given
        assertEquals(0, itemRepository.count());

        Item itemToPersist = Item.builder()
                .name("Credit Card 1")
                .vendor("Visa")
                .expirationDate(LocalDate.of(2017, 11, 30))
                .retailPrice(1000L)
                .build();

        // When
        itemRepository.save(itemToPersist);

        // Then
        assertEquals(1, itemRepository.count());

        Optional<Item> foundItem = itemRepository.findByName(itemToPersist.getName());

        assertTrue(foundItem.isPresent());
        assertEquals(itemToPersist.getName(), foundItem.get().getName());
    }

    @After
    public void cleanUp() {
        itemRepository.deleteAll();
    }
}