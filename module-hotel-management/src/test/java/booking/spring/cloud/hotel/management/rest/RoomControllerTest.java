package booking.spring.cloud.hotel.management.rest;

import booking.spring.cloud.core.model.dto.JwtAuthenticationResponse;
import booking.spring.cloud.core.model.dto.RoomResponse;
import booking.spring.cloud.core.model.dto.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static booking.spring.cloud.core.model.utils.Constants.AUTH_BEARER_PREFIX;
import static booking.spring.cloud.core.model.utils.Constants.AUTH_HEADER_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@DisplayName("Тестирование API апартаментов")
class RoomControllerTest {

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
    @DisplayName("Тест добавления апартаментов")
    @Test
    void save() {
        final var token = TestUtils.generateToken("Viktor", UserRole.ROLE_ADMIN, jwtSigningKey);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/rooms/room")
                        .header(AUTH_HEADER_NAME, AUTH_BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"hotelId\": 1, \"number\": \"test\"}")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.hotelId").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.number").value("test"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.times_booked").value(0));
    }

    @SneakyThrows
    @Order(2)
    @DisplayName("Тест просмотра апартаментов")
    @Test
    void getById() {
        final var token = TestUtils.generateToken("Viktor", UserRole.ROLE_USER, jwtSigningKey);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/rooms/room/1")
                        .header(AUTH_HEADER_NAME, AUTH_BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hotelId").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.number").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.available").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.times_booked").exists());
    }

    @SneakyThrows
    @Order(3)
    @DisplayName("Тест просмотра всех апартаментов")
    @Test
    void getAll() {
        final var token = TestUtils.generateToken("Viktor", UserRole.ROLE_USER, jwtSigningKey);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/rooms")
                        .header(AUTH_HEADER_NAME, AUTH_BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].hotelId").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].number").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].available").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].times_booked").exists());
    }

    @SneakyThrows
    @Order(4)
    @DisplayName("Тест поиска апартаментов по номеру")
    @Test
    void findRoomByNumber() {
        final var token = TestUtils.generateToken("Viktor", UserRole.ROLE_USER, jwtSigningKey);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/rooms/find-by-hotel/1")
                        .header(AUTH_HEADER_NAME, AUTH_BEARER_PREFIX + token)
                        .param("number", "test")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.hotelId").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.number").value("test"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.times_booked").value(0));
    }

    @SneakyThrows
    @Order(5)
    @DisplayName("Тест получения рекомендованных апартаментов")
    @Test
    void getRecommend() {
        final var token = TestUtils.generateToken("Viktor", UserRole.ROLE_USER, jwtSigningKey);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/rooms/room/recommend-by-hotel/1")
                        .header(AUTH_HEADER_NAME, AUTH_BEARER_PREFIX + token)
                        .param("date", "2000-10-31")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].hotelId").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].number").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].available").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].times_booked").exists());
    }

    @SneakyThrows
    @Order(6)
    @DisplayName("Тест включения доступности апартаментов")
    @Test
    void setAvailable() {
        final var token = TestUtils.generateToken("Viktor", UserRole.ROLE_ADMIN, jwtSigningKey);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/rooms/room/1/set-available")
                        .header(AUTH_HEADER_NAME, AUTH_BEARER_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @SneakyThrows
    @Order(6)
    @DisplayName("Тест выключения доступности апартаментов")
    @Test
    void unsetAvailable() {
        final var token = TestUtils.generateToken("Viktor", UserRole.ROLE_ADMIN, jwtSigningKey);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/rooms/room/1/unset-available")
                        .header(AUTH_HEADER_NAME, AUTH_BEARER_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @SneakyThrows
    @Order(7)
    @DisplayName("Тест бронирования апартаментов")
    @Test
    void confirmAvailability() {
        final var token = TestUtils.generateToken("Viktor", UserRole.ROLE_USER, jwtSigningKey);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/rooms/room/1/confirm-availability")
                        .header(AUTH_HEADER_NAME, AUTH_BEARER_PREFIX + token)
                        .param("date", "2000-10-31")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hotelId").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.roomId").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.roomNumber").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.date").value("31-10-2000"));
    }

    @SneakyThrows
    @Order(8)
    @DisplayName("Тест снятия бронирования апартаментов")
    @Test
    void release() {
        final var token = TestUtils.generateToken("Viktor", UserRole.ROLE_USER, jwtSigningKey);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/rooms/room/1/release")
                        .header(AUTH_HEADER_NAME, AUTH_BEARER_PREFIX + token)
                        .param("date", "2000-10-31")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @SneakyThrows
    @Order(9)
    @DisplayName("Тест удаления апартаментов")
    @Test
    void delete() {
        final var token = TestUtils.generateToken("Viktor", UserRole.ROLE_ADMIN, jwtSigningKey);

        final var response = mockMvc.perform(MockMvcRequestBuilders.get("/api/rooms/find-by-hotel/1")
                        .header(AUTH_HEADER_NAME, AUTH_BEARER_PREFIX + token)
                        .param("number", "test")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andReturn()
                .getResponse()
                .getContentAsByteArray();

        final var str = new String(response, StandardCharsets.UTF_8);
        final var objectMapper = new ObjectMapper();
        try (final var parser = objectMapper.createParser(str)) {
            final var room = parser.readValueAs(RoomResponse.class);

            mockMvc.perform(MockMvcRequestBuilders.delete("/api/rooms/room/"+room.id())
                            .header(AUTH_HEADER_NAME, AUTH_BEARER_PREFIX + token)
                            .accept(MediaType.APPLICATION_JSON_VALUE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isNoContent());
        }
    }
}