package pl.bartlomiejstepien.mcsm.web.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.bartlomiejstepien.mcsm.Routes;
import pl.bartlomiejstepien.mcsm.auth.AuthenticatedUser;
import pl.bartlomiejstepien.mcsm.auth.AuthenticationFacade;
import pl.bartlomiejstepien.mcsm.auth.jwt.JwtUtils;

import javax.validation.Valid;

@RestController
@RequestMapping(Routes.API_AUTH)
public class AuthRestController
{
    private final AuthenticationFacade authenticationFacade;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Autowired
    public AuthRestController(final AuthenticationFacade authenticationFacade,
                              final AuthenticationManager authenticationManager,
                              final JwtUtils jwtUtils)
    {
        this.authenticationFacade = authenticationFacade;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        authenticationFacade.setCurrentUser(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        return ResponseEntity.ok(new JwtResponse(jwt, authenticatedUser.getId(), authenticatedUser.getUsername()));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout()
    {
        authenticationFacade.setCurrentUser(null);
        return ResponseEntity.ok("ok");
    }

    private static class LoginRequest
    {
        private final String username;
        private final String password;

        public LoginRequest(final String username, final String password)
        {
            this.username = username;
            this.password = password;
        }

        public String getUsername()
        {
            return username;
        }

        public String getPassword()
        {
            return password;
        }
    }

    private static class JwtResponse
    {
        private final int id;
        private final String username;
        private final String jwt;

        public JwtResponse(final String jwt, final int id, final String username)
        {
            this.jwt = jwt;
            this.id = id;
            this.username = username;
        }

        public int getId()
        {
            return id;
        }

        public String getUsername()
        {
            return username;
        }

        public String getJwt()
        {
            return jwt;
        }
    }
}
