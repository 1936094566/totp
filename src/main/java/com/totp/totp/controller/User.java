package com.totp.totp.controller;

import lombok.Data;

import java.util.List;

/**
 * @author machao
 * @date 2019/7/17
 * @time 9:02
 * @description
 **/
@Data
public class User {
    private String name;
    private List<String> list;
    private String[] array;
}
