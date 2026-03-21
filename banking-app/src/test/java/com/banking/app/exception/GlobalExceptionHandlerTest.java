package com.banking.app.exception;

import com.banking.app.model.Account;
import jakarta.validation.Valid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(new ExceptionProbeController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Nested
    @DisplayName("HTTP status mapping")
    class StatusMappingTests {

        @Test
        void accountNotFoundReturns404() throws Exception {
            mockMvc.perform(get("/probe/not-found/account"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.error").value("Not Found"));
        }

        @Test
        void resourceNotFoundReturns404() throws Exception {
            mockMvc.perform(get("/probe/not-found/resource"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("missing"));
        }

        @Test
        void illegalArgumentReturns400() throws Exception {
            mockMvc.perform(get("/probe/bad-request"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.message").value("invalid input"));
        }

        @Test
        void insufficientBalanceReturns422() throws Exception {
            mockMvc.perform(get("/probe/insufficient"))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.status").value(422));
        }

        @Test
        void unauthorizedExceptionReturns401() throws Exception {
            mockMvc.perform(get("/probe/unauthorized"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status").value(401));
        }

        @Test
        void badCredentialsReturns401() throws Exception {
            mockMvc.perform(get("/probe/bad-credentials"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void duplicateResourceReturns409() throws Exception {
            mockMvc.perform(get("/probe/duplicate"))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value(409));
        }

        @Test
        void missingRequestParameterReturns400() throws Exception {
            mockMvc.perform(get("/probe/required-param"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", containsString("q")));
        }

        @Test
        void pathVariableTypeMismatchReturns400() throws Exception {
            mockMvc.perform(get("/probe/long-path/not-a-number"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", containsString("id")));
        }

        @Test
        void validationFailureReturns400WithFieldErrors() throws Exception {
            mockMvc.perform(post("/probe/valid-account")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Validation Failed"))
                    .andExpect(jsonPath("$.fieldErrors").isArray())
                    .andExpect(jsonPath("$.fieldErrors[0].field").exists());
        }
    }

    @RestController
    static class ExceptionProbeController {

        @GetMapping("/probe/not-found/account")
        void accountNotFound() {
            throw new AccountNotFoundException("no account");
        }

        @GetMapping("/probe/not-found/resource")
        void resourceNotFound() {
            throw new ResourceNotFoundException("missing");
        }

        @GetMapping("/probe/bad-request")
        void badRequest() {
            throw new IllegalArgumentException("invalid input");
        }

        @GetMapping("/probe/insufficient")
        void insufficient() {
            throw new InsufficientBalanceException("low");
        }

        @GetMapping("/probe/unauthorized")
        void unauthorized() {
            throw new UnauthorizedException("denied");
        }

        @GetMapping("/probe/bad-credentials")
        void badCredentials() {
            throw new BadCredentialsException("bad");
        }

        @GetMapping("/probe/duplicate")
        void duplicate() {
            throw new DuplicateResourceException("exists");
        }

        @GetMapping("/probe/required-param")
        String requiredParam(@RequestParam("q") String q) {
            return q;
        }

        @GetMapping("/probe/long-path/{id}")
        String longPath(@PathVariable Long id) {
            return String.valueOf(id);
        }

        @PostMapping("/probe/valid-account")
        String validAccount(@Valid @RequestBody Account account) {
            return "ok";
        }
    }
}
