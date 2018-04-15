/*
 * Copyright (c) 2018.
 * You may use, distribute and modify this code
 * under the terms of the TOT (take on trust) public license which unfortunately won't be
 * written for another century. So you can modify and copy this files.
 *
 */

package spring.springControllers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.merchandises.Merchandise;
import model.merchandises.MerchandiseImpl;
import model.postgresqlModel.Users;
import model.postgresqlModel.tables.Deals;
import model.postgresqlModel.tables.News;
import model.postgresqlModel.tables.merchandises.Aliens;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
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
    private static final String VIEW_ALL_NEWS = "news_list";
    private static final String VIEW_CART = "cart";
    private static final String VIEW_SHOP = "shop";
    private static final String VIEW_404 = "not_found";
    private static final String VIEW_ADMIN = "admin_panel";
    private final static Logger logger = LoggerFactory.getLogger(IndexController.class);
    private final static String VIEW_LOGIN = "login";
    private static final String VIEW_PROFILE = "profile";
    private PostgresSpringService model;
    private Users user = null;
    private List<Merchandise> cart = new ArrayList<>();

    // TODO: 30.03.18 all that web stuff
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String welcomePage(ModelMap modelMap) {
        setAttr(modelMap);
        modelMap.addAttribute("news", model.getNews().stream().map(string -> {
            Gson gson = new Gson();
            return gson.fromJson(string, News.class);
        }).sorted(Comparator.comparingInt(News::getId).reversed()).collect(toList()));
        modelMap.addAttribute("merchandises",
                model.searchMerchandise("").stream()
                        .map(item -> {
                            JsonParser parser = new JsonParser();
                            JsonObject object = parser.parse(item).getAsJsonObject();
                            MerchandiseImpl merchandise = new MerchandiseImpl() {
                            };
                            merchandise.setId(object.get("id").getAsInt());
                            merchandise.setName(object.get("name").getAsString());
                            merchandise.setBenefit(object.get("benefit").getAsFloat());
                            merchandise.setAllInfo(item);
                            return merchandise;
                        })
                        .sorted(Comparator.comparingDouble(Merchandise::getBenefit).reversed())
                        .limit(8).collect(toList()));
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

    @RequestMapping(value = "/loginRedirect", method = RequestMethod.GET)
    public ModelAndView loginRedirect(@RequestParam("token") String token) {
        String newUser = model.getUserByToken(token);
        Gson gson = new Gson();
        user = gson.fromJson(newUser, Users.class);
        return new ModelAndView("redirect:/profile");
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String openProfile(ModelMap modelMap) {
        setAttr(modelMap);
        if (null != user) {
//            modelMap.addAttribute("user", user);
            Gson gson = new Gson();
            JsonParser parser = new JsonParser();
            String deals = model.getDealsByUser(user.getUsername(), user.getToken(), 0, 5);
            List<Deals> dealsList = new ArrayList<>();
            if (!deals.equals("")) {
                JsonArray array = parser.parse(deals).getAsJsonObject().get("deals").getAsJsonArray();
                array.forEach(item -> dealsList.add(gson.fromJson(item, Deals.class)));
            }
            modelMap.addAttribute("myDeals", dealsList);
            return VIEW_PROFILE;
        } else {
            return VIEW_INDEX;
        }
    }

    /**
     * Method to  get specified news.
     *
     * @param newsId news's id
     * @param map    model map
     * @return page name
     */
    @RequestMapping(value = "/news/{newsId}", method = RequestMethod.GET)
    public String openNews(@PathVariable("newsId") int newsId, ModelMap map) {
        News news = new Gson().fromJson(model.getNewsById(newsId), News.class);
        setAttr(map);
        if (null != news) {

            map.addAttribute("news", news);
            return VIEW_NEWS;
        } else {
            return VIEW_404;
        }
    }

    /**
     * Method to perform logout action.
     *
     * @return redirect on home page
     */
    @RequestMapping(value = "/logout")
    public ModelAndView logout() {
        if (null != user) {
            model.disconnect(user.getUsername(), user.getToken());
            user = null;
        }
        return new ModelAndView("redirect:/");
    }


    private void setAttr(ModelMap modelMap) {
        modelMap.addAttribute("cart", cart);
        modelMap.addAttribute("user", user);
    }

    @Autowired
    @Qualifier("psqlService")
    public void setModel(PostgresSpringService model) {
        this.model = model;
    }

    @RequestMapping(value = "/news/")
    public String showNews(ModelMap model) {
        return VIEW_ALL_NEWS;
    }

    private void someMethod() {
        News news = new News();

        news.setHeader("New website");
        news.setDescription("If you want to be aware of all the events of this site, then this is for you.");
        news.setText("This site was created to show you what I can. And the entire system was created in order to prove that I'm not an empty place in programming. It was hard, but our team coped. Now it seems that there is nothing with which our team could not cope.\n" +
                "Our team consists of one person who was terribly tired while writing all this. Really. Why did I even create this news? The next news will be just about them.");
        news.setSlider(false);
        news.setAuthor(19);
        try {
            BufferedImage image2 = ImageIO.read(new File("/home/s3rius/Development/Projects/slaveMarket/server/web/resources/images/iot .jpg"));
            Image scaled = image2.getScaledInstance(1366, 768, Image.SCALE_SMOOTH);
            BufferedImage result = new BufferedImage(1366, 768, image2.getType());
            Graphics2D g = result.createGraphics();
            g.drawImage(scaled, 0, 0, null);
            g.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(result, "jpg", baos);
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            news.setImage(imageInByte);

        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
