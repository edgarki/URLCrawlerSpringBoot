package com.app.test;


import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Component
@PropertySource("classpath:application.properties")
public class URLmodel {

    // using application.prop settings
    @NotBlank
    private static String host;
    @Value("${redis.host}")
    public void setHost(String value){
        this.host = value;
    }
    @Min(1025)
    @Max(65536)
    private static int port;
    @Value("${redis.port}")
    public void setPort(int value){
        this.port = value;
    }

    private static JedisPool pool;


    public URLmodel(){
        pool = new JedisPool(host, port);
    }

    public void addURL(String value){
        String key = "URL";
        Jedis jedis = pool.getResource();
        try {
            // save to redis
            jedis.sadd(key, value);
            // close connection after add
            jedis.close();

        } catch (JedisException e) {
            // return to pool if anything is wrong
            if (null != jedis) {
                //pool.close();
                pool.destroy();
                jedis = null;
            }
        }

    }

    public List<String> getURL(){
        String key = "URL";
        Jedis jedis = pool.getResource();
        List<String> pages = new ArrayList<String>();
        try {
            for(Object object : jedis.smembers(key)) {
                String element = (String) object;
                //System.out.println( element );
                pages.add(element);
            }
            jedis.close();
            //return value;

        } catch (JedisException e) {
            // return to pool if anything is wrong
            if (null != jedis) {
                pool.destroy();
                jedis = null;
            }
        }
        return pages;
    }

    public void fetchURL(String url){
        Validate.isTrue( url.matches("^(http|https)://.*$"), "usage: http:// or https:// url is accepted");
        //Document doc = Jsoup.connect(url).get();
        Document doc = new Document("");

        try{
            doc = Jsoup.connect(url).get();
        } catch (IOException ex) {
            System.out.println (ex.toString());
        }


        Elements links = doc.select("a[href]");


        for (Element link : links) {
            if(  link.attr("href").matches("^(http|https)://.*$") ){
                addURL( link.attr("abs:href") );
            }
        }

    }

}
