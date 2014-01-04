
package com.dynamease.serviceproviders;

import java.util.ArrayList;
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
    public String home(HttpServletRequest request, Model model) {
        List<Person> connections;

        List<SPInfo> SPStatusList = new ArrayList<SPInfo>();
        for (ServiceProviders sp : ServiceProviders.values()) {
            SPConnectionRetriever spAccess = spResolver.getSPConnection(sp);
            SPInfo thisSp = new SPInfo(sp.toString(), spAccess.isconnected(), spAccess.getPermissions(), spAccess
                    .getConnectUrl());
            SPStatusList.add(thisSp);
        }

        model.addAttribute("nom", request.getSession().getAttribute("userId"));
        model.addAttribute("serviceProviders", SPStatusList);

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

    /**
     * Rendering bean class used to exchange info with the JSP
     * 
     * @author Yves Nicolas
     * 
     */
    public class SPInfo {
        private String name;
        private boolean connected;
        private String permissions;
        private String URL;

        public SPInfo(String name, boolean isConnected, String permissions, String URL) {
            super();
            this.name = name;
            this.connected = isConnected;
            this.permissions = permissions;
            this.URL = URL;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isConnected() {
            return connected;
        }

        public void setConnected(boolean isConnected) {
            this.connected = isConnected;
        }

        public String getPermissions() {
            return permissions;
        }

        public void setPermissions(String permissions) {
            this.permissions = permissions;
        }

        public String getURL() {
            return URL;
        }
        

    }
}
