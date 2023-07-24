package gr.aegean.icsd.newspaperapp.security;

import org.springframework.security.core.context.SecurityContextHolder;

public final class UserUtils {



    public static boolean isCurator() {

        String userRole = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();

        return userRole.equals("[ROLE_CURATOR]");
    }


    public static boolean isJournalist() {

        String userRole = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();

        return userRole.equals("[ROLE_JOURNALIST]");
    }


    public static boolean isVisitor() {

        String userRole = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();

        return userRole.equals("[ROLE_ANONYMOUS]");
    }


    public static String getUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }


}
