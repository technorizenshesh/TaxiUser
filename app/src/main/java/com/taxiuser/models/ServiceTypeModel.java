package com.taxiuser.models;

public class ServiceTypeModel {
    String id, name;
    boolean chk;

    public ServiceTypeModel(String id, String name, boolean chk) {
        this.id = id;
        this.name = name;
        this.chk = chk;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChk() {
        return chk;
    }

    public void setChk(boolean chk) {
        this.chk = chk;
    }
}
