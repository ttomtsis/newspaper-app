package gr.aegean.icsd.newspaperapp.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    @GetMapping("/")
    public String home(@AuthenticationPrincipal OidcUser principal) {

        if (principal != null) {
            return "Welcome, " + principal.getNickName();
        }

        return "You are currently not logged in";
    }

}
