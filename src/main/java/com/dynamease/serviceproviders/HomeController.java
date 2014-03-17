package com.dynamease.serviceproviders;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.dynamease.entities.PersonBasic;
import com.dynamease.entities.PersonWthAddress;
import com.dynamease.profiles.SpInfoPerson;
import com.dynamease.serviceproviders.config.Uris;
import com.dynamease.serviceproviders.user.CurrentUserContext;

/**
 * Simple little @Controller that invokes Facebook and renders the result. The injected
 * {@link Facebook} reference is configured with the required authorization credentials for the
 * current user behind the scenes.
 * 
 * @author Keith Donald
 */
@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private SPResolver spResolver;

    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    private CurrentUserContext currentUser;

    @RequestMapping(value = Uris.MAIN, method = RequestMethod.GET)
    public String home(HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        List<PersonBasic> connections;

        List<SPInfo> SPStatusList = new ArrayList<SPInfo>();
        for (ServiceProviders sp : ServiceProviders.values()) {
            SPConnectionRetriever spAccess = spResolver.getSPConnection(sp);
            SPInfo thisSp = new SPInfo(sp.toString());
            thisSp.update(spAccess);
            SPStatusList.add(thisSp);
        }

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("serviceProviders", SPStatusList);

        ServiceProviders currentSp = (ServiceProviders) session.getAttribute("sp");

        if (currentSp != null) {
            SPConnectionRetriever spr = spResolver.getSPConnection(currentSp);
            try {
                connections = spr.getConnections();
                model.addAttribute("connections", connections);
                model.addAttribute("serviceProvider", spr.getActiveSP().toString());
            } catch (SpInfoRetrievingException e) {
                model.addAttribute(
                        "serviceProvider",
                        String.format("Unable to retrieve connections for %s : %s", spr.getActiveSP().toString(),
                                e.getMessage()));
                logger.error(
                        String.format("Unable to retrieve connections for %s : %s", spr.getActiveSP().toString(),
                                e.getMessage()), e);
            }

        } else {
            // SP not defined, no contacts to show"
            model.addAttribute("serviceProvider", "No Service Provider selected : no contact to show");
        }
        return Uris.WORK;
    }

    @RequestMapping(value = Uris.IDPROCESS, method = RequestMethod.POST)
    public RedirectView login(HttpServletRequest request, @RequestParam("id") String id) {

        currentUser.connect(id);
        // ModelAndView mav = new ModelAndView(Uris.SIGNINCONFIRM);
        // mav.addObject("nom", id);
        return new RedirectView(Uris.MAIN);
    }

    @RequestMapping(value = Uris.DISCONNECT, method = RequestMethod.POST)
    public RedirectView disconnect(@RequestParam("sp") ServiceProviders sp) {

        connectionRepository.removeConnections(sp.toString().toLowerCase());

        return new RedirectView(Uris.URISPREFIX + Uris.MAIN);
    }

    @RequestMapping(value = Uris.SELECT, method = RequestMethod.POST)
    public RedirectView changeSelect(@RequestParam("sp") ServiceProviders sp) {
        SPConnectionRetriever spr = spResolver.getSPConnection(sp);
        if (spr.isSelected())
            spr.unselect();
        else
            spr.select();
        return new RedirectView(Uris.URISPREFIX + Uris.MAIN);
    }

    @RequestMapping(value = Uris.SPCHOICE, method = RequestMethod.POST)
    public ModelAndView Spchoice(HttpServletRequest request, @RequestParam("sp") String sp) {

        ServiceProviders spasenum = ServiceProviders.valueOf(sp);
        HttpSession session = request.getSession();
        session.setAttribute("sp", spasenum);
        ModelAndView mav = new ModelAndView(Uris.SPCONFIRM);
        mav.addObject("sp", sp);
        return mav;
    }

    @RequestMapping(value = Uris.NAMELOOKUP, method = RequestMethod.GET)
    public String nameInput() {
        return Uris.NAMELOOKUP;
    }

    @RequestMapping(value = Uris.NAMELOOKUP, method = RequestMethod.POST)
    public String nameSearch(@RequestParam("first") String first, @RequestParam("last") String last, Model model) {

        List<SpNameSearch> resultList = new ArrayList<>();
        for (ServiceProviders sp : ServiceProviders.values()) {
            SPConnectionRetriever spAccess = spResolver.getSPConnection(sp);
            if (spAccess.isSelected()) {
                SpNameSearch thisSp = new SpNameSearch(new SPInfo(sp.toString()).update(spAccess));
                try {
                    thisSp.setListInfo(spAccess.getPersonInfo(new PersonWthAddress(first, last)));
                } catch (SpInfoRetrievingException e) {
                    logger.warn(String.format("Not able to retrieve %s info for %s %s : %s", sp.toString(), first,
                            last, e.getMessage()));
                }
                resultList.add(thisSp);
            }
        }

        model.addAttribute("results", resultList);
        model.addAttribute("name", first + " " + last);
        return Uris.SEARCHRESULT;
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
        private boolean selected;
        private String permissions;
        private String URL;

        public SPInfo(String name, boolean isConnected, String permissions, String URL) {
            super();
            this.name = name;
            this.connected = isConnected;
            this.permissions = permissions;
            this.URL = URL;
        }

        public SPInfo(String name) {
            this.name = name;
            this.connected = false;
            this.permissions = null;
            this.URL = null;
            this.selected = false;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
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

        public SPInfo update(SPConnectionRetriever spr) {
            if (spr != null) {
                connected = spr.isconnected();
                permissions = spr.getPermissions();
                URL = spr.getConnectUrl();
                selected = spr.isSelected();
            }
            return this;
        }

    }

    public class SpNameSearch {
        private SPInfo info;
        private List<SpInfoPerson> listInfo;

        public SpNameSearch(SPInfo info) {
            this.info = info;
        }

        public SPInfo getInfo() {
            return info;
        }

        public void setInfo(SPInfo info) {
            this.info = info;
        }

        public List<SpInfoPerson> getListInfo() {
            return listInfo;
        }

        public void setListInfo(List<SpInfoPerson> listInfo) {
            this.listInfo = listInfo;
        }

    }
}
