package booking.spring.cloud.booking.rest;

import booking.spring.cloud.booking.entities.User;
import booking.spring.cloud.booking.mapper.UserMapper;
import booking.spring.cloud.core.model.dto.JwtAuthenticationResponse;
import booking.spring.cloud.core.model.dto.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static booking.spring.cloud.core.model.utils.Constants.AUTH_BEARER_PREFIX;
import static booking.spring.cloud.core.model.utils.Constants.AUTH_HEADER_NAME;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerTest {

    @MockitoSpyBean
    private UserMapper mapper;
    @Autowired
    private MockMvc mockMvc;

    @Order(0)
    @Test
    void getById() throws Exception {
        when(mapper.dtoToEntity(any())).thenAnswer(invocationOnMock -> {
            final var user = new User();
            user.setUsername("Viktor");
            user.setPassword("password123");
            user.setRole(UserRole.ROLE_ADMIN);
            return user;
        });

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

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/user/1")
                        .header(AUTH_HEADER_NAME, AUTH_BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("Viktor"));
    }

    @Order(1)
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

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users")
                        .header(AUTH_HEADER_NAME, AUTH_BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].username").value("Viktor"));
    }

    @Order(2)
    @Test
    void setAdmin() throws Exception {
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

        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/1/set-admin")
                        .header(AUTH_HEADER_NAME, AUTH_BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Order(3)
    @Test
    void deleteUser() throws Exception {
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

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/user")
                        .header(AUTH_HEADER_NAME, AUTH_BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"username\":\"Viktor\", \"password\":\"password123\"}")
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