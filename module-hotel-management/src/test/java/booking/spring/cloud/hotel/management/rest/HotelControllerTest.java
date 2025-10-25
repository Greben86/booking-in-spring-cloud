package booking.spring.cloud.hotel.management.rest;

import booking.spring.cloud.core.model.dto.UserRole;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static booking.spring.cloud.core.model.utils.Constants.AUTH_BEARER_PREFIX;
import static booking.spring.cloud.core.model.utils.Constants.AUTH_HEADER_NAME;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@DisplayName("Тестирование API отелей")
class HotelControllerTest {

    // Уникальный ключ для генерации токена
    @Value("${security.token.signing.key}")
    private String jwtSigningKey;
    @Autowired
    private MockMvc mockMvc;

    @Order(0)
    @DisplayName("Проверка, что MockMvc существует")
    @Test
    void contextLoads() {
        assertThat(mockMvc).isNotNull();
    }

    @SneakyThrows
    @Order(1)
    @DisplayName("Тест добавления отеля")
    @Test
    void save() {
        final var token = TestUtils.generateToken("Viktor", UserRole.ROLE_ADMIN, jwtSigningKey);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/hotels/hotel")
                        .header(AUTH_HEADER_NAME, AUTH_BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"name\": \"Ананас\"}")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Ананас"));
    }

    @SneakyThrows
    @Order(2)
    @DisplayName("Тест просмотра отеля")
    @Test
    void getById() {
        final var token = TestUtils.generateToken("Viktor", UserRole.ROLE_USER, jwtSigningKey);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/hotels/hotel/2")
                        .header(AUTH_HEADER_NAME, AUTH_BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Ананас"));
    }

    @SneakyThrows
    @Order(3)
    @DisplayName("Тест получения всех отелей")
    @Test
    void getAll() {
        final var token = TestUtils.generateToken("Viktor", UserRole.ROLE_USER, jwtSigningKey);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/hotels")
                        .header(AUTH_HEADER_NAME, AUTH_BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Общага"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value("2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("Ананас"));
    }

    @SneakyThrows
    @Order(4)
    @DisplayName("Тест поиска отеля но названию")
    @Test
    void findByName() {
        final var token = TestUtils.generateToken("Viktor", UserRole.ROLE_USER, jwtSigningKey);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/hotels/hotel/find")
                        .header(AUTH_HEADER_NAME, AUTH_BEARER_PREFIX + token)
                        .param("name", "Ананас")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Ананас"));
    }

    @SneakyThrows
    @Order(5)
    @DisplayName("Тест удаления отеля")
    @Test
    void delete() {
        final var token = TestUtils.generateToken("Viktor", UserRole.ROLE_ADMIN, jwtSigningKey);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/hotels/hotel/2")
                        .header(AUTH_HEADER_NAME, AUTH_BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}