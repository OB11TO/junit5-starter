package com.ob11to.junit.service;

import com.ob11to.junit.dto.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserService {

    private final List<User> users = new ArrayList<>(); //пока что вместо dao

    public List<User> getAll(){
        return users;
    }

    public boolean add(User user) {
        return users.add(user);
    }
}
