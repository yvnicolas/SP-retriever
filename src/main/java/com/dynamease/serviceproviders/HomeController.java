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
package com.dynamease.serviceproviders;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.dynamease.serviceproviders.config.Uris;
import com.dynamease.serviceproviders.user.User;

/**
 * Simple little @Controller that invokes Facebook and renders the result. The injected
 * {@link Facebook} reference is configured with the required authorization credentials for the
 * current user behind the scenes.
 * 
 * @author Keith Donald
 */
@Controller
public class HomeController {

    @Autowired
    private SPResolver spResolver;

    @RequestMapping(value = Uris.MAIN, method = RequestMethod.GET)
    public String home(Model model) {
        List<Person> connections;
        try {
            connections = spResolver.getSPConnectionRetriever().getConnections();
            model.addAttribute("connections", connections);
            model.addAttribute("serviceProvider", spResolver.getSPConnectionRetriever().getActiveSP().toString());
        } catch (IllegalStateException | SpInfoRetrievingException e) {
            // SP not defined, no contacts to show"
            model.addAttribute("serviceProvider", "No Service Provider selected : no contact to show");
        }
        return Uris.WORK;
    }

    @RequestMapping(value = Uris.IDPROCESS, method = RequestMethod.POST)
    public ModelAndView login(HttpServletRequest request, @RequestParam("id") String id) {

        HttpSession session = request.getSession();
        spResolver.connectUser(new User(id));
        session.setAttribute("userId", id);
        ModelAndView mav = new ModelAndView(Uris.SIGNINCONFIRM);
        mav.addObject("nom", id);
        return mav;
    }

    @RequestMapping(value = Uris.SPCHOICE, method = RequestMethod.POST)
    public ModelAndView Spchoice(@RequestParam("sp") String sp) {

        ServiceProviders spasenum = ServiceProviders.valueOf(sp);
        ModelAndView mav = new ModelAndView(Uris.SPCONFIRM);
        spResolver.setCurrentSP(spasenum);
        mav.addObject("sp", sp);
        return mav;
    }

}
