package booking.spring.cloud.booking.rest;

import booking.spring.cloud.booking.clients.HotelManagementClient;
import booking.spring.cloud.core.model.dto.HotelResponse;
import booking.spring.cloud.core.model.dto.ReservationDto;
import booking.spring.cloud.core.model.dto.RoomResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

import static booking.spring.cloud.core.model.utils.Constants.AUTH_BEARER_PREFIX;
import static booking.spring.cloud.core.model.utils.Constants.AUTH_HEADER_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@DisplayName("Тестирование API бронирования")
class BookingControllerTest {

    @MockitoBean
    private HotelManagementClient httpClient;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        when(httpClient.findByName(any())).thenAnswer(invocationOnMock ->
                ResponseEntity.ok(new HotelResponse(1, "Общага")));
        when(httpClient.findRoomByNumber(any(), any())).thenAnswer(invocationOnMock ->
                ResponseEntity.ok(new RoomResponse(1, 1, "313", true, 0)));
        when(httpClient.confirmAvailability(any(), any(), any(), any())).thenAnswer(invocationOnMock ->
                ResponseEntity.ok(new ReservationDto("test", 1, 1, "313", LocalDate.now(), LocalDate.now())));
        when(httpClient.releaseRoom(any(), any())).thenAnswer(invocationOnMock ->
                ResponseEntity.ok(new ReservationDto("test", 1, 1, "313", LocalDate.now(), LocalDate.now())));
    }

    @Order(0)
    @DisplayName("Проверка, что MockMvc существует")
    @Test
    void contextLoads() {
        assertThat(mockMvc).isNotNull();
    }

    @SneakyThrows
    @Order(1)
    @DisplayName("Тест добавления брони")
    @Test
    void saveBooking() {
        final var token = TestUtils.signUp(mockMvc, "Viktor", "password123");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/bookings/booking")
                        .header(AUTH_HEADER_NAME, AUTH_BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"requestId\": \"test\", \"hotel\": \"Общага\", \"room\": \"313\", \"start\": \"23-10-2025\", \"finish\": \"24-10-2025\"}")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestId").value("test"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hotel").value("Общага"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.room").value("313"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.start").value("23-10-2025"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.finish").value("24-10-2025"));
    }

    @SneakyThrows
    @Order(2)
    @DisplayName("Тест просмотра брони")
    @Test
    void getById() {
        final var token = TestUtils.signIn(mockMvc, "Viktor", "password123");

        mockMvc.perform(MockMvcRequestBuilders.get("/api/bookings/booking/1")
                        .header(AUTH_HEADER_NAME, AUTH_BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestId").value("test"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hotel").value("Общага"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.room").value("313"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.start").value("23-10-2025"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.finish").value("24-10-2025"));
    }

    @SneakyThrows
    @Order(3)
    @DisplayName("Тест получения всех бронирований пользователя")
    @Test
    void getAll() {
        final var token = TestUtils.signIn(mockMvc, "Viktor", "password123");

        mockMvc.perform(MockMvcRequestBuilders.get("/api/bookings")
                        .header(AUTH_HEADER_NAME, AUTH_BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].requestId").value("test"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].hotel").value("Общага"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].room").value("313"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].start").value("23-10-2025"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].finish").value("24-10-2025"));
    }

    @SneakyThrows
    @Order(4)
    @DisplayName("Тест удаления брони")
    @Test
    void deleteBooking(){
        final var token = TestUtils.signIn(mockMvc, "Viktor", "password123");

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/bookings/booking/1")
                        .header(AUTH_HEADER_NAME, AUTH_BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @SneakyThrows
    @Order(5)
    @DisplayName("Тест ошибки доступа")
    @Test
    void testForbidden(){
        mockMvc.perform(MockMvcRequestBuilders.get("/api/bookings")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}