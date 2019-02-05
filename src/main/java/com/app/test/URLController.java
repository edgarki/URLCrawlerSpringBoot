package com.app.test;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;


@Controller
public class URLController {

    private List<String> urls = new ArrayList<>();

    // run when posting in form
    @PostMapping("/")
    public String handlePostRequest(String URL) {
        // add to crawled list
        urls.add(URL);
        // call model to manipulate urls
        URLmodel m = new URLmodel();
        m.fetchURL(URL);
        // fetch next level of URLs
        for ( String page : m.getURL() ){
            if ( page != null ){
                m.fetchURL(page);
            }
        }
        return "redirect:/";
    }

    // run on loading
    @GetMapping("/")
    public String handleGetRequest(Model model) {
        model.addAttribute("origin", urls);
        URLmodel m = new URLmodel();
        model.addAttribute("url", m.getURL());
        return "index";
    }

}