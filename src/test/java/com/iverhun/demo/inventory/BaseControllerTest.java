package com.iverhun.demo.inventory;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.options;

public class BaseControllerTest {
    public static final String HAL_JSON_CHARSET_UTF_8 = "application/hal+json;charset=UTF-8";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    protected ItemRepository itemRepository;

    protected MockMvc mockMvc;

    @Rule
    public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("build/generated-snippets");

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .apply(MockMvcRestDocumentation.documentationConfiguration(this.restDocumentation))
                .defaultRequest(options("/")
                        .accept(HAL_JSON_CHARSET_UTF_8)
                        .contentType(HAL_JSON_CHARSET_UTF_8))
                .build();
    }

    @After
    public void cleanUp() {
        itemRepository.deleteAll();
    }
}
