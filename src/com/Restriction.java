/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com;

import java.util.List;

/**
 *
 * @author WaMa
 */
public class Restriction {
    private enum Type {BOARD, STUD};
    private final int id;

    public int getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public List<Interval> getList() {
        return list;
    }
    private final Type type;
    private final List<Interval> list;

    public Restriction(int id, Type type, List<Interval> list) {
        this.id = id;
        this.type = type;
        this.list = list;
    }
        
}
