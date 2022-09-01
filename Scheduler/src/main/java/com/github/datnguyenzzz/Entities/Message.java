package com.github.datnguyenzzz.Entities;

import lombok.Getter;
import lombok.Setter;

public class Message extends Object {
    
    @Getter @Setter
    private String key;

    @Getter @Setter
    private String value;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\t[\n");
        sb.append("\t\tkey : " + key + "\n");
        sb.append("\t\tvalue : " + value + "\n");
        sb.append("\t]\n");

        return sb.toString();
    }
}
