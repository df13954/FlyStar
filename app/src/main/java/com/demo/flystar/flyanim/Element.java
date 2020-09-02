package com.demo.flystar.flyanim;


public class Element {
    // 360
    public double direction;
    public float speed;
    // 可看做是偏移
    public float x = 0;
    public float y = 0;

    public Element(double direction, float speed) {
        this.direction = direction;
        this.speed = speed;
    }
}
