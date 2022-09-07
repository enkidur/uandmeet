package com.project.uandmeet.config;

public enum DatabaseType {

    SOURCE("SOURCE"), REPLICA("REPLICA");

    private String type;

    DatabaseType(String type) {
        this.type = type;
    }

    public String getType(){return this.type;}

}
