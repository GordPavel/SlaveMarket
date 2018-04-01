/*
 * Copyright (c) 2018.
 * You may use, distribute and modify this code
 * under the terms of the TOT (take on trust) public license which unfortunately won't be
 * written for another century. So you can modify and copy this files.
 *
 */

package controllers.springControllers;

import com.google.gson.Gson;
import model.Model;
import model.PostgresModel;
import model.merchandises.Merchandise;
import model.postgresqlModel.Users;
import model.postgresqlModel.tables.News;
import model.postgresqlModel.tables.merchandises.Aliens;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Controller
@EnableWebMvc
@Scope(value = "session")
public class IndexController {
    private static final String VIEW_INDEX = "index";
    private static final String VIEW_NEWS = "news";
    private static final String VIEW_CART = "cart";
    private static final String VIEW_SHOP = "shop";
    private static final String VIEW_MERCHANDISE = "shop/item/";
    private final static Logger logger = LoggerFactory.getLogger(IndexController.class);
    private final static String VIEW_LOGIN = "login";
    private Model model = new PostgresModel();
    private Users user = null;
    private List<Merchandise> cart = new ArrayList<>();
    private String token = "";

    // TODO: 30.03.18 all that web stuff
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String welcomePage(ModelMap modelMap) {
        setAttr(modelMap);
        modelMap.addAttribute("news", model.getNews().stream().map(string -> {
            Gson gson = new Gson();
            return gson.fromJson(string, News.class);
        }).sorted(Comparator.comparingInt(News::getId)).collect(toList()));
        return VIEW_INDEX;
    }

    @RequestMapping(value = "/cart", method = RequestMethod.GET)
    public String showCart(ModelMap modelMap) {
        Aliens alien = new Aliens();
        alien.setName("alien" + cart.size());
        cart.add(alien);
        setAttr(modelMap);
        return VIEW_CART;
    }

    @RequestMapping(value = "/shop", method = RequestMethod.GET)
    public String openShop(ModelMap modelMap) {
        setAttr(modelMap);
        return VIEW_SHOP;
    }

    @RequestMapping(value = "/login")
    public String login(ModelMap modelMap) {
        setAttr(modelMap);
        return VIEW_LOGIN;
    }

    @RequestMapping(value = "/logout")
    public String logout() {
        return token;
    }

    @RequestMapping(value = "/loginReq", method = RequestMethod.POST)
    public ModelAndView loginRedirect(ModelMap modelMap) {
        user = new Users();
        modelMap.addAttribute("attribute", "redirectWithRedirectPrefix");
        return new ModelAndView("redirect:/", modelMap);
    }

    private void setAttr(ModelMap modelMap) {
        modelMap.addAttribute("cart", cart);
        modelMap.addAttribute("user", user);
    }
}
