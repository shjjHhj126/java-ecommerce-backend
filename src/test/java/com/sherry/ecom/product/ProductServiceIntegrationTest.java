package com.sherry.ecom.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sherry.ecom.auth.AuthenticationResponse;
import com.sherry.ecom.auth.RegisterRequest;
import com.sherry.ecom.auth.AuthenticationService;
import com.sherry.ecom.category.Category;
import com.sherry.ecom.category.CategoryService;
import com.sherry.ecom.product.Request.ProductRequest;
import com.sherry.ecom.product.service.ProductService;
import com.sherry.ecom.user.Role;
import com.sherry.ecom.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserService userService;

    @Test
    void testSaveProduct() throws Exception {
        // Prepare test data
        Category category = Category.builder()
                .name("Sunglasses")
                .level(1)
                .build();
        Category savedCategory = categoryService.create(category);

        // Register a user and authenticate to get access token
        RegisterRequest registerRequest = RegisterRequest.builder()
                .firstName("The")
                .lastName("Admin")
                .email("theAdmin@gmail.com")//but is still a user
                .password("password")
                .build();
        AuthenticationResponse authResponse = authenticationService.register(registerRequest);

        String accessToken = authResponse.getAccessToken();

        ProductRequest productRequest = ProductRequest.builder()
                .name("黑色迫降墨鏡")
                .spu("SKU_245_24_093")
                .description("市面上太陽眼鏡良莠不齊，價格也有天壤之別，有顏色的鏡片就能擋住陽光?\n" +
                        "其實劣質的太陽眼鏡鏡片會讓瞳孔放大反而會讓眼球吸收更多的紫外線!\n" +
                        "所以購買太陽眼鏡")
                .categoryId(savedCategory.getId())
                .build();

        mockMvc.perform(post("/api/v1/products")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(productRequest.getName()))
                .andExpect(jsonPath("$.spu").value(productRequest.getSpu()))
                .andExpect(jsonPath("$.description").value(productRequest.getDescription()))
                .andExpect(jsonPath("$.categoryId").value(productRequest.getCategoryId()));

    }
}
