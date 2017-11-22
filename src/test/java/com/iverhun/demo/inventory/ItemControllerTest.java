package com.iverhun.demo.inventory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import lombok.SneakyThrows;
import org.hamcrest.CoreMatchers;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.text.IsEmptyString.isEmptyString;
import static org.junit.Assert.assertEquals;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.halLinks;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ItemControllerTest extends BaseControllerTest {
    @Autowired
    private MappingJackson2HttpMessageConverter messageConverter;


    // TODO: test required fields
    // TODO: test duplicate name
    // TODO: cover error handling (400, 401, 404, 409, 422)

    @Test
    public void testGetItems_emptyDatabase() throws Exception {
        mockMvc.perform(get("/items"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(HAL_JSON_CHARSET_UTF_8))
                .andExpect(content().json("{ }"));
    }

    @Test
    public void testGetItems_found() throws Exception {
        Item item1 = itemRepository.save(Item.builder()
                .name("i1")
                .vendor("v1")
                .expirationDate(LocalDate.now())
                .retailPrice(100)
                .build());
        Item item2 = itemRepository.save(Item.builder()
                .name("i2")
                .vendor("v2")
                .expirationDate(LocalDate.now())
                .retailPrice(200)
                .build());

        // When
        ResultActions action = mockMvc.perform(get("/items"))
                .andDo(MockMvcResultHandlers.print());
        // Then
        action.andExpect(status().isOk())
                .andExpect(content().contentType(HAL_JSON_CHARSET_UTF_8))
                .andExpect(jsonPath("$._embedded.items.length()", CoreMatchers.is(2)));

        action.andDo(document("{class-name}/{method-name}",
                responseFields(
                        fieldWithPath("_embedded").description("'item' array with item resources."),
                        fieldWithPath("_embedded.items").description("Array with returned Item resources."),
                        fieldWithPath("_embedded.items[].name").description("Item's name."),
                        fieldWithPath("_embedded.items[].vendor").description("Item's vendor."),
                        fieldWithPath("_embedded.items[].expirationDate").description("Item's expiration date."),
                        fieldWithPath("_embedded.items[].retailPrice").description("Item's price."),
                        fieldWithPath("_embedded.items[]._links").description("Links section."),
                        fieldWithPath("_embedded.items[]._links.self.href").description("Self reference."))));
    }

    @Test
    public void testGetItemByName_found() throws Exception {
        Item item1 = itemRepository.save(Item.builder()
                .name("i1")
                .vendor("v1")
                .expirationDate(LocalDate.now())
                .retailPrice(100)
                .build());
        Item noiseItem = itemRepository.save(Item.builder()
                .name("noiseItem")
                .vendor("v2")
                .expirationDate(LocalDate.now())
                .retailPrice(200)
                .build());

        // When
        ResultActions action = mockMvc.perform(get("/items/{name}", item1.getName()))
                .andDo(MockMvcResultHandlers.print());

        // Then
        action.andExpect(status().isOk())
                .andExpect(content().contentType(HAL_JSON_CHARSET_UTF_8))
                .andExpect(jsonPath("$.name", CoreMatchers.is(item1.getName())))
                .andExpect(jsonPath("$.vendor", CoreMatchers.is(item1.getVendor())))
                .andExpect(jsonPath("$.expirationDate", CoreMatchers.equalTo(item1.getExpirationDate().toString())))
                .andExpect(jsonPath("$.retailPrice", CoreMatchers.equalTo((int)item1.getRetailPrice())));

        action.andDo(document("{class-name}/{method-name}",
                responseFields(
                        fieldWithPath("name").description("Item's name."),
                        fieldWithPath("vendor").description("Item's vendor."),
                        fieldWithPath("expirationDate").description("Item's expiration date."),
                        fieldWithPath("retailPrice").description("Item's price."),
                        fieldWithPath("_links").description("Links section."),
                        fieldWithPath("_links.self.href").description("Self reference."))));
    }


    @Test
    @Ignore("Fails because the error handling is not implemented yet")
    public void testGetItemByName_notFound() throws Exception {
        Item item1 = itemRepository.save(Item.builder()
                .name("i1")
                .vendor("v1")
                .expirationDate(LocalDate.now())
                .retailPrice(100)
                .build());

        // When
        ResultActions action = mockMvc.perform(get("/items/{name}", "non-existing item"))
                .andDo(MockMvcResultHandlers.print());

        // Then
        // TODO: error handling
        action.andExpect(status().isNotFound())
                .andExpect(content().contentType(HAL_JSON_CHARSET_UTF_8))
                .andExpect(jsonPath("$.name", CoreMatchers.is(item1.getName())))
                .andExpect(jsonPath("$.vendor", CoreMatchers.is(item1.getVendor())))
                .andExpect(jsonPath("$.expirationDate", CoreMatchers.equalTo(item1.getExpirationDate().toString())))
                .andExpect(jsonPath("$.retailPrice", CoreMatchers.equalTo((int)item1.getRetailPrice())));

        action.andDo(document("{class-name}/{method-name}",
                responseFields(
                        fieldWithPath("name").description("Item's name."),
                        fieldWithPath("vendor").description("Item's vendor."),
                        fieldWithPath("expirationDate").description("Item's expiration date."),
                        fieldWithPath("retailPrice").description("Item's price."),
                        fieldWithPath("_links").description("Links section."),
                        fieldWithPath("_links.self.href").description("Self reference.")),
                links(halLinks(),
                        linkWithRel("self").description("Link to self section."))));
    }


    @Test
    public void testDeleteItem_deleted() throws Exception {
        // Given
        Item itemToDelete = itemRepository.save(Item.builder()
                .name("itemToDelete")
                .vendor("v1")
                .expirationDate(LocalDate.now())
                .retailPrice(100)
                .build());

        Item itemToRetain = itemRepository.save(Item.builder()
                .name("itemToRetain")
                .vendor("v2")
                .expirationDate(LocalDate.now())
                .retailPrice(200)
                .build());

        // When
        ResultActions action = mockMvc.perform(delete("/items/{name}", itemToDelete.getName()))
                .andDo(MockMvcResultHandlers.print());

        // Then
        assertEquals(1, itemRepository.count());
        Item remainingItem = itemRepository.findByName(itemToRetain.getName()).get();
        assertEquals(itemToRetain.getName(), remainingItem.getName());

        action.andExpect(status().isNoContent())
                .andExpect(content().string(isEmptyString()));

        action.andDo(document("{class-name}/{method-name}"));
    }


    @Test
    public void testCreateItem_created() throws Exception {
        // Given
        ItemDto itemDto = ItemDto.builder()
                .name("newItem")
                .vendor("v1")
                .expirationDate(LocalDate.now())
                .retailPrice(100)
                .build();

        String requestBody = asJsonString(itemDto);

        // When
        ResultActions action = mockMvc.perform(post("/items")
                .content(requestBody))
                .andDo(MockMvcResultHandlers.print());

        // Then
        assertEquals(1, itemRepository.count());
        Item savedItem = itemRepository.findByName("newItem").get();

        action.andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/items/" + savedItem.getName())));

        action.andDo(document("{class-name}/{method-name}",
                responseHeaders(
                        headerWithName("Location").description(
                                "URI path to created resource."))));
    }

    @Test
    public void testUpdateItemPrice_updated() throws Exception {
        // Given
        Item itemToUpdate = itemRepository.save(Item.builder()
                .name("itemToUpdate")
                .vendor("v1")
                .expirationDate(LocalDate.now())
                .retailPrice(100)
                .build());

        ItemPriceDto itemPriceDto = new ItemPriceDto(123);

        String requestBody = asJsonString(itemPriceDto);

        // When
        ResultActions action = mockMvc.perform(put("/items/{name}/price", itemToUpdate.getName())
                .content(requestBody))
                .andDo(MockMvcResultHandlers.print());

        // Then
        assertEquals(1, itemRepository.count());
        Item savedItem = itemRepository.findByName("itemToUpdate").get();

        assertEquals(savedItem.getRetailPrice(), itemPriceDto.getRetailPrice());

        action.andExpect(status().isOk())
                .andExpect(content().contentType(HAL_JSON_CHARSET_UTF_8))
                .andExpect(jsonPath("$.name", CoreMatchers.is(itemToUpdate.getName())))
                .andExpect(jsonPath("$.vendor", CoreMatchers.is(itemToUpdate.getVendor())))
                .andExpect(jsonPath("$.expirationDate", CoreMatchers.equalTo(itemToUpdate.getExpirationDate().toString())))
                .andExpect(jsonPath("$.retailPrice", CoreMatchers.equalTo((int)itemPriceDto.getRetailPrice())));

        action.andDo(document("{class-name}/{method-name}",
                responseFields(
                        fieldWithPath("name").description("Item's name."),
                        fieldWithPath("vendor").description("Item's vendor."),
                        fieldWithPath("expirationDate").description("Item's expiration date."),
                        fieldWithPath("retailPrice").description("Item's price."),
                        fieldWithPath("_links").description("Links section."),
                        fieldWithPath("_links.self.href").description("Self reference.")),
                links(halLinks(),
                        linkWithRel("self").description("Link to self section."))));

        action.andDo(document("{class-name}/{method-name}"));
    }


    @SneakyThrows
    public String asJsonString(Object o) {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        ObjectMapper objectMapper = messageConverter.getObjectMapper();
        objectMapper.registerModule(new JSR310Module());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        messageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);

        return mockHttpOutputMessage.getBodyAsString();
    }

}