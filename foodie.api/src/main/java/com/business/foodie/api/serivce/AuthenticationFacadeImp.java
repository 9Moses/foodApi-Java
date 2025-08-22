package com.business.foodie.api.serivce;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFacadeImp implements AuthenticationFacade {
    @Override
    public Authentication getAuthentication() {
       return SecurityContextHolder.getContext().getAuthentication();
    }
}
