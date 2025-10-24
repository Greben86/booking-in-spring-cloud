package booking.spring.cloud.booking.rest;

import booking.spring.cloud.booking.clients.HotelManagementClient;
import booking.spring.cloud.core.model.dto.HotelResponse;
import booking.spring.cloud.core.model.dto.JwtAuthenticationResponse;
import booking.spring.cloud.core.model.dto.ReservationDto;
import booking.spring.cloud.core.model.dto.RoomResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static booking.spring.cloud.core.model.utils.Constants.AUTH_BEARER_PREFIX;
import static booking.spring.cloud.core.model.utils.Constants.AUTH_HEADER_NAME;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookingControllerTest {

    @MockitoBean
    private HotelManagementClient httpClient;
    @Autowired
    private MockMvc mockMvc;

    @Order(0)
    @Test
    void saveBooking() throws Exception {
        when(httpClient.findByName(any())).thenAnswer(invocationOnMock ->
                ResponseEntity.ok(new HotelResponse(1, "Общага")));
        when(httpClient.findRoomByNumber(any(), any())).thenAnswer(invocationOnMock ->
                ResponseEntity.ok(new RoomResponse(1, 1, "313", true, 0)));
        when(httpClient.confirmAvailability(any(), any())).thenAnswer(invocationOnMock ->
                ResponseEntity.ok(new ReservationDto(1, 1, "313", LocalDate.now())));

        var answerSignUp = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/sign/up")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"username\":\"Viktor\", \"password\":\"password123\"}")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsByteArray();
        var token = getTokenFromAnswer(answerSignUp);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/bookings/booking")
                        .header(AUTH_HEADER_NAME, AUTH_BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"hotel\": \"Общага\", \"room\": \"313\", \"start\": \"23-10-2025\", \"finish\": \"24-10-2025\"}")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.hotel").value("Общага"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.room").value("313"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.start").value("23-10-2025"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.finish").value("24-10-2025"));
    }

    @Order(1)
    @Test
    void getById() throws Exception {
        var answerSignUp = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/sign/in")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"username\":\"Viktor\", \"password\":\"password123\"}")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsByteArray();
        var token = getTokenFromAnswer(answerSignUp);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/bookings/booking/1")
                        .header(AUTH_HEADER_NAME, AUTH_BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hotel").value("Общага"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.room").value("313"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.start").value("23-10-2025"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.finish").value("24-10-2025"));
    }

    @Order(2)
    @Test
    void getAll() throws Exception {
        var answerSignUp = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/sign/in")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"username\":\"Viktor\", \"password\":\"password123\"}")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsByteArray();
        var token = getTokenFromAnswer(answerSignUp);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/bookings")
                        .header(AUTH_HEADER_NAME, AUTH_BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].hotel").value("Общага"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].room").value("313"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].start").value("23-10-2025"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].finish").value("24-10-2025"));
    }

    @Order(3)
    @Test
    void deleteBooking() throws Exception {
        when(httpClient.findByName(any())).thenAnswer(invocationOnMock ->
                ResponseEntity.ok(new HotelResponse(1, "Общага")));
        when(httpClient.findRoomByNumber(any(), any())).thenAnswer(invocationOnMock ->
                ResponseEntity.ok(new RoomResponse(1, 1, "313", true, 0)));
        when(httpClient.releaseRoom(any(), any())).thenAnswer(invocationOnMock ->
                ResponseEntity.ok(new ReservationDto(1, 1, "313", LocalDate.now())));

        var answerSignUp = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/sign/in")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"username\":\"Viktor\", \"password\":\"password123\"}")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsByteArray();
        var token = getTokenFromAnswer(answerSignUp);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/bookings/booking/1")
                        .header(AUTH_HEADER_NAME, AUTH_BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    private String getTokenFromAnswer(final byte[] answer) throws IOException {
        final var str = new String(answer, StandardCharsets.UTF_8);
        final var objectMapper = new ObjectMapper();
        try (final var response = objectMapper.createParser(str)) {
            final var jwt = response.readValueAs(JwtAuthenticationResponse.class);
            return jwt.token();
        }
    }
}