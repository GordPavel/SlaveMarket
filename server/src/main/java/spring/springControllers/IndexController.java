/*
 * Copyright (c) 2018.
 * You may use, distribute and modify this code
 * under the terms of the TOT (take on trust) public license which unfortunately won't be
 * written for another century. So you can modify and copy this files.
 *
 */

package spring.springControllers;

import com.google.gson.*;
import exceptions.UserException;
import model.merchandises.MerchandiseImpl;
import model.postgresqlModel.Users;
import model.postgresqlModel.tables.Deals;
import model.postgresqlModel.tables.News;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
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
    private static final String VIEW_ABOUT = "about_us";
    private final static Logger logger = LoggerFactory.getLogger(IndexController.class);
    private final static String VIEW_LOGIN = "login";
    private static final String VIEW_PROFILE = "profile";
    private static final String VIEW_CONTACTS = "contacts";
    private PostgresSpringService model;
    private Users user = null;
    private List<Integer> cart = new ArrayList<>();

    // TODO: 30.03.18 all that web stuff
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String welcomePage(ModelMap modelMap) {
        setAttr(modelMap);
        modelMap.addAttribute("news", model.getNews().stream().map(string -> {
            Gson gson = new Gson();
            return gson.fromJson(string, News.class);
        }).sorted(Comparator.comparingInt(News::getId).reversed()).collect(toList()));
        modelMap.addAttribute("cart", cart);
        modelMap.addAttribute("merchandises",
                model.searchMerchandise("", 10, "benefit", true).stream()
                        .map(item -> {
                            JsonParser parser = new JsonParser();
                            JsonObject object = parser.parse(item).getAsJsonObject();
                            MerchandiseImpl merchandise = new MerchandiseImpl() {
                            };
                            merchandise.setId(object.get("id").getAsInt());
                            merchandise.setImage(object.get("image").getAsString());
                            merchandise.setName(object.get("name").getAsString());
                            merchandise.setBenefit(object.get("benefit").getAsFloat());
                            merchandise.setClassName(object.get("class").getAsString());
                            merchandise.setPrice(object.get("price").getAsInt());
                            merchandise.setAllInfo(item);
                            return merchandise;
                        }).collect(toList()));
        return VIEW_INDEX;
    }

    @RequestMapping(value = "/info/{page}")
    public String aboutUs(ModelMap map, @PathVariable String page) {
        setAttr(map);
        switch (page) {
            case "contacts":
                return VIEW_CONTACTS;
            case "about":
                return VIEW_ABOUT;
        }
        return VIEW_404;
    }


    @RequestMapping(value = "/cart", method = RequestMethod.GET)
    public String showCart(ModelMap modelMap) {
        setAttr(modelMap);
        JsonParser parser = new JsonParser();
        modelMap.addAttribute("cart", model
                .getGroupMerchandises(cart)
                .stream()
                .map(item -> parser.parse(item).getAsJsonObject())
                .collect(toList()));
        return VIEW_CART;
    }

    @RequestMapping(value = "/cart", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Boolean> addToCart(@RequestParam int id) {
        if (cart.contains(id)) {
            return ResponseEntity.badRequest().body(false);
        } else {
            cart.add(id);
            return ResponseEntity.ok(true);
        }
    }


    @RequestMapping(value = "/cart", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Boolean> deleteFromCart(@RequestParam int id) {
        if (cart.contains(id)) {
            int target = cart.indexOf(id);
            cart.remove(target);
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.badRequest().body(false);
        }
    }


    @RequestMapping(value = "/cart/info", method = RequestMethod.POST)
    public ResponseEntity<String> getCartInfo(@RequestParam String type) {
        switch (type) {
            case "size":
                JsonObject object = new JsonObject();
                object.add("size", new JsonPrimitive(cart.size()));
                return ResponseEntity.ok(object.toString());
            case "items":
                JsonObject jCart = new JsonObject();
                JsonParser parser = new JsonParser();
                JsonArray items = new JsonArray();
                cart.forEach(integer -> {
                    String merchant = model.getMerchantById(integer);
                    JsonObject item = parser.parse(merchant).getAsJsonObject();
                    items.add(item);
                });
                jCart.add("count", new JsonPrimitive(items.size()));
                jCart.add("items", items);
                return ResponseEntity.ok(jCart.toString());
            case "amITeaPot":
                // If you ever forget about what type of organism you are.
                return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body("Maybe");
            default:
                return ResponseEntity.badRequest().body("Can't find query for type \"" + type + "\"");
        }
    }

    @RequestMapping(value = "/shop", method = RequestMethod.GET)
    public String openShop(ModelMap modelMap) {
        setAttr(modelMap);
        return VIEW_SHOP;
    }

    @RequestMapping(value = "/login")
    public ModelAndView login(ModelMap modelMap) {
        setAttr(modelMap);
        if (null == user) {
            return new ModelAndView(VIEW_LOGIN);
        } else {
            return loginRedirect(user.getToken());
        }
    }

    @RequestMapping(value = "/loginRedirect", method = RequestMethod.GET)
    public ModelAndView loginRedirect(@RequestParam("token") String token) {
        String newUser = model.getUserByToken(token);
        Gson gson = new Gson();
        if (null == newUser) {
            user = null;
            return new ModelAndView("redirect:/");
        }
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
            return welcomePage(modelMap);
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
            try {
                model.disconnect(user.getUsername(), user.getToken());
            } catch (UserException exception) {
                logger.error("user " + user.getUsername() + " has wrong token. Logging out.");
            } catch (Exception e) {
                logger.error("user can't logout because " + e.getMessage());
            }
            user = null;
        }
        return new ModelAndView("redirect:/");
    }

    @RequestMapping(value = "/change/login", method = RequestMethod.POST)
    public ResponseEntity<Boolean> chageUsername(@RequestParam String username,
                                                 @RequestParam String newUsername,
                                                 @RequestParam String token) {
        try {
            if (model.changeLogin(username, newUsername, token)) {
                user.setUsername(newUsername);
                return ResponseEntity.ok(true);
            } else {
                return ResponseEntity.ok(false);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(false);
        }
    }

    @RequestMapping(value = "/change/password", method = RequestMethod.POST)
    public ResponseEntity<Boolean> chagePassword(@RequestParam String username,
                                                 @RequestParam String newPassword,
                                                 @RequestParam String token) {
        try {
            Gson gson = new Gson();
            Users newUser = gson.fromJson(model.getUserByToken(token), Users.class);
            model.changePassword(username, newPassword, token);
            user = newUser;
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(false);
        }
    }

    private void setAttr(ModelMap modelMap) {
        modelMap.addAttribute("user", user);
    }

    @Autowired
    @Qualifier("psqlService")
    public void setModel(PostgresSpringService model) {
        this.model = model;
    }

    @RequestMapping(value = "/news/")
    public String showNews(ModelMap modelMap) {
        setAttr(modelMap);
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
            BufferedImage image2 = ImageIO.read(new File("/home/s3rius/Development/Projects/slaveMarket/server/web/resources/images/iot.jpg"));
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
