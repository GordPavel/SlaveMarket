/*
 * Copyright (c) 2018.
 * You may use, distribute and modify this code
 * under the terms of the TOT (take on trust) public license which unfortunately won't be
 * written for another century. So you can modify and copy this files.
 *
 */

package spring.springControllers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.org.apache.xpath.internal.operations.Bool;
import exceptions.CreateMerchandiseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/rest/methods")
public class SpringRestController {

    private PostgresSpringService model;


    @RequestMapping(value = "/loginReq", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<? extends String> loginRequest(@RequestParam String username, @RequestParam String password) {
        try {
            return new ResponseEntity<>(model.login(username, password), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/logoutReq", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<? extends Boolean> logoutRequest(@RequestParam("username") String username, @RequestParam("token") String token) {
        try {
            model.disconnect(username, token);
            return new ResponseEntity<>(true, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);

        }
    }

    @RequestMapping(value = "/tokenUpdate", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<? extends String> tokenReset(@RequestParam("username") String username, @RequestParam("password") String password) {
        try {
            return new ResponseEntity<>(model.updateToken(username, password), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/registerReq", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<? extends String> registerRequest(@RequestParam("username") String username, @RequestParam("password") String password) {
        try {
            model.register(username, password);
            return new ResponseEntity<>("User successfully registered", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/availableClasses", method = RequestMethod.POST)
    public ResponseEntity<List<String>> getAvailableClasses() {
        try {
            return ResponseEntity.ok(model.getAvailableClasses());
        } catch (Exception e) {
            return ResponseEntity.noContent().build();
        }
    }

    @RequestMapping(value = "/getFields", method = RequestMethod.POST)
    public ResponseEntity<List<String>> getFields(@RequestParam("className") String className) {
        try {
            return ResponseEntity.ok(model.getMandatoryFields(className));
        } catch (Exception e) {
            return ResponseEntity.noContent().build();
        }
    }

    @RequestMapping(value = "/getFieldsWithTypes", method = RequestMethod.POST)
    public ResponseEntity<String> getFieldsWithTypes(@RequestParam("className") String className) {
        try {
            return ResponseEntity.ok(model.getMandatoryFieldsWithTypes(className));
        } catch (Exception e) {
            return ResponseEntity.noContent().build();
        }
    }

    @RequestMapping(value = "/getAllDeals", method = RequestMethod.POST)
    public ResponseEntity<List<String>> getAllDeals(@RequestParam("username") String username, @RequestParam("token") String token) {
        try {
            return ResponseEntity.ok(model.getDealsByUser(username, token));
        } catch (Exception e) {
            return ResponseEntity.noContent().build();
        }
    }

    @RequestMapping(value = "/getDeals", method = RequestMethod.POST)
    public ResponseEntity<String> getDeals(@RequestParam("username") String username,
                                           @RequestParam("token") String token,
                                           @RequestParam("offset") int offset,
                                           @RequestParam("limit") int limit) {
        try {
            return ResponseEntity.ok(model.getDealsByUser(username, token, offset, limit));
        } catch (Exception e) {
            return ResponseEntity.noContent().build();
        }
    }

    @RequestMapping(value = "/addMerch", method = RequestMethod.POST)
    public ResponseEntity<Boolean> addMerch(@RequestParam String className, @RequestParam String fields, @RequestParam String username, @RequestParam String token) {
        Map<String, String> kvs = new HashMap<>();
        JsonParser parser = new JsonParser();
        JsonObject merch = parser.parse(fields).getAsJsonObject();
        merch.entrySet().forEach(item -> {
            if (!item.getKey().equals("price"))
                kvs.put(item.getKey(), item.getValue().getAsString());
        });
        try {
            model.addMerchandiseByMap(className, kvs, username, token, Integer.parseInt(merch.get("price").getAsString()));
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(false);
        }
    }

    @Autowired
    @Qualifier("psqlService")
    public void setModel(PostgresSpringService model) {
        this.model = model;
    }
}
