/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dynamease.serviceproviders.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.view.RedirectView;

import com.dynamease.serviceproviders.config.Uris;

/**
 * Before a request is handled: 1. sets the current User in the {@link SecurityContext} from a
 * cookie, if present and the user is still connected to Facebook. 2. requires that the user sign-in
 * if he or she hasn't already.
 * 
 * NB : this user interceptor is actually invoked only if the view is added in the registry, see
 * WebMVC Config
 * 
 * @author Yves Nicolas adapted from Keith Donald Samples
 */
@Service
public final class UserInterceptor extends HandlerInterceptorAdapter {
    private static final Logger logger = LoggerFactory.getLogger(UserInterceptor.class);

    @Autowired
    private UserCookieGenerator userCookieGenerator;

    @Autowired
    private CurrentUserContext currentUser;

    public UserInterceptor() {
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // Handle the requests that should go thru
        if (shouldGoThru(request))
            return true;

        // checking whether connection do the application has been made
        if (!rememberUser(request, response)) {
            logger.debug(String.format("Prehandling : no recognised user, redirecting to signup"));
            new RedirectView(Uris.APPLICATIONIDINPUT, true).render(null, request, response);
            return false;
        }

        // Signing out
        if (handleSignOut(request, response)) {
            return false;
        }

        // At this stage, we can proceed to the regular controller as signing is
        // effective
        return true;
    }

    private boolean shouldGoThru(HttpServletRequest request) {
        if (request.getServletPath().startsWith(Uris.APPCONNECTPREFIX))
            return true;

        if (request.getServletPath().startsWith(Uris.SPRINGCONNECTPREFIX))
            return true;

        return false;
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {

    }

    // internal helpers

    // Gets a potential user ID from cookies existing on the system.
    private boolean rememberUser(HttpServletRequest request, HttpServletResponse response) {

        String userId;

        // First checks whether we have a user in the ongoing session or thru a cookie

        // userId = (String) session.getAttribute("userId");
        if (currentUser.isConnected()) {
            logger.debug(String.format("userId %s is found from Http Session attributes ", currentUser.getId()));
            return true;
        }

        userId = userCookieGenerator.readCookieValue(request);
        if (userId == null) {

            // No Cookie : no potential user found
            return false;
        } else if (!userIsValid(userId)) {

            // Cookie referencing an invalid user, should be removed
            // Then proceed as if no user on system
            userCookieGenerator.removeCookie(response);
            return false;
        }

        else {
            // User Id is a valid one found from a Cookie, recording it as the current user and
            // storing the cookie
            currentUser.connect(userId);
            userCookieGenerator.addCookie(null, response);
            return true;
        }

    }

    // If signout has been asked
    private boolean handleSignOut(HttpServletRequest request, HttpServletResponse response) throws Exception {

        if ((currentUser.isConnected()) && request.getServletPath().startsWith(Uris.SIGNOUT)) {
            logger.debug(String.format("%s requested disconnection", currentUser.getId()));
            currentUser.disconnect();
            userCookieGenerator.removeCookie(response);
            new RedirectView(Uris.MAIN, true).render(null, request, response);
            return true;
        } else
            return false;
    }

    // Checks validity of userId
    // For the moment, any non void String is considered valid
    private boolean userIsValid(String userId) {
        // Any userId is valid except a void string
        return (userId != "");
    }
}
