package com.ob11to.junit.dao;

public class UserDaoMock extends UserDao{

    @Override
    public boolean delete(Integer id) {
        return false; //вернется по умолчанию
    }
}
