/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

/**
 *
 * @author Ashok
 */
public class Bullet
{
    private String name;    //pretty much the name of the image to use
    private String source;  //playername source. Leave as null to denote "sourceless"
    private int id;         //unique id for every bullet
    
    public final static float STANDARD_BULLET_SPEED = 0.3f/*(1.75*GameScreen.FRAME_RATE/1000.0)*/; //pixels per millisecond. First number is pixels/frame.
    public final static int STANDARD_BULLET_LIFETIME = 20000;
    
    private double x,y, speed;
    private double rotation;
    private int damage;
    private int currLifetime = 0, lifetime; //lifetime in millis
    
    public boolean piercesWalls, piercesTanks;
    
    public Bullet(String name, String source, int id, double x, double y, double speed, double rotation, int lifetime, int damage, boolean piercesWalls, boolean piercesTanks)
    {
        this.name = name;
        this.source = source;
        this.id = id;
        this.x = x;
        this.y = y;
        this.rotation = rotation;
        this.lifetime = lifetime;
        this.speed = speed;
        this.damage = damage;
        this.piercesTanks = piercesTanks;
        this.piercesWalls = piercesWalls;
    }
    
    public String getSource()
    {
        return source;
    }
    
    public int getID()
    {
        return id;
    }
    
    public boolean update(double deltaTime) //returns true if alive, false if dead
    {
        double xVel = speed*Math.cos(rotation);
        double yVel = speed*Math.sin(rotation);
        x += xVel*deltaTime;
        y += yVel*deltaTime;
        currLifetime += deltaTime;
        return lifetime >= currLifetime;
    }
    
    public double[] mockUpdate(double deltaTime)
    {
        double[] d = new double[3];
        double xVel = speed*Math.cos(rotation);
        double yVel = speed*Math.sin(rotation);
        d[0] = x + xVel*deltaTime;
        d[1] = y + yVel*deltaTime;
        d[2] = currLifetime + deltaTime;
        return d;
    }
    
    public String getName()
    {
        return name;
    }
    
    public double getX()
    {
        return x;
    }
    
    public double getY()
    {
        return y;
    }
    
    public double getRotation()
    {
        return rotation;
    }
    
    public double getSpeed()
    {
        return speed;
    }
    
    public int getLifetime()
    {
        return lifetime;
    }
    
    public int getDamage()
    {
        return damage;
    }
    
    public void setSpeed(double newSpeed)
    {
        speed = newSpeed;
    }
    
    public void setRotation(double dir)
    {
        rotation = dir;
    }
    
    public void setX(double newX)
    {
        x = newX;
    }
    
    public void setY(double newY)
    {
        y = newY;
    }
}
