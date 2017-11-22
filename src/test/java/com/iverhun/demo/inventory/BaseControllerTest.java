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

//    @Autowired
//    private Filter springSecurityFilterChain;

    @Autowired
    protected ItemRepository itemRepository;

    @Autowired
    private ItemService itemService;

    protected MockMvc mockMvc;

    @Rule
    public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("build/generated-snippets");

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
//                .addFilters(springSecurityFilterChain)
                .apply(MockMvcRestDocumentation.documentationConfiguration(this.restDocumentation))
                .defaultRequest(options("/")
//                        .with(new JwtRequestPostProcessor(tokenService))
                        .accept(HAL_JSON_CHARSET_UTF_8)
                        .contentType(HAL_JSON_CHARSET_UTF_8))
                .build();
    }

//    protected ResponseFieldsSnippet validationErrorResponseFieldSnippet = responseFields(
//            fieldWithPath("_embedded.validationErrors").description("Errors that were found during validation."),
//            fieldWithPath("_embedded.validationErrors[].property").description("Invalid property name of posted json entity."),
//            fieldWithPath("_embedded.validationErrors[].message").description("The message, extracted from validation provider exception."),
//            fieldWithPath("_embedded.validationErrors[].invalidValue").description("Invalid value that had not passed validation"));
//
//    protected ResponseFieldsSnippet errorMessageResponseFieldSnippet = responseFields(fieldWithPath("message").description("Error message."));
//

    @After
    public void cleanUp() {
        itemRepository.deleteAll();
    }
}
