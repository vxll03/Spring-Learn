    package com.example.demo.controller;

    import com.example.demo.model.User;
    import com.example.demo.service.AuthenticationService;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping("/api/auth")
    public class AuthController {

        @Autowired
        AuthenticationService authenticationService;

        @Value("${testing.app.isProduction}")
        private boolean isProduction;

        @PostMapping("/register")
        public ResponseEntity<?> register(
                @RequestBody User user,
                HttpServletRequest request) {

            return authenticationService.register(user, request);
        }

        @PostMapping("/login")
        public ResponseEntity<?> login(
                @RequestBody User user,
                HttpServletResponse response,
                HttpServletRequest request,
                @RequestHeader(value = "User-Agent") String userAgent) throws Exception {

            return authenticationService.login(user, userAgent, request, response);
        }

        @PostMapping("/logout")
        public ResponseEntity<?> logout(
                HttpServletRequest request,
                HttpServletResponse response) {

            return authenticationService.logout(request, response);
        }

        @PostMapping("/refresh")
        public ResponseEntity<?> refreshToken(
                HttpServletRequest request,
                HttpServletResponse response) {

            return authenticationService.refresh(request, response);
        }


    }