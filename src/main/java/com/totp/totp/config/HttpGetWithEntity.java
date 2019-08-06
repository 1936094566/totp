package com.totp.totp.config;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.net.URI;

/**
 * @author machao
 * @date 2019/8/6
 * @time 15:09
 * @description
 **/
public class HttpGetWithEntity extends HttpEntityEnclosingRequestBase {

    private final static String METHOD_NAME = "GET";

    @Override
    public String getMethod() {
        return METHOD_NAME;
    }

    public HttpGetWithEntity() {
        super();
    }

    public HttpGetWithEntity(final URI uri) {
        super();
        setURI(uri);
    }

    HttpGetWithEntity(final String uri) {
        super();
        setURI(URI.create(uri));
    }

}
