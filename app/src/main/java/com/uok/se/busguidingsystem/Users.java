package com.uok.se.busguidingsystem;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by USER on 14-09-2018.
 */

public class Users {
    public Users(Map<String, Location> users) {
        this.users = users;
    }

    public Users() {
    }

    public Map<String, Location> getUsers() {
        return users;
    }

    public void setUsers(Map<String, Location> users) {
        this.users = users;
    }

    private Map<String, Location> users;
}
