/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import java.util.HashMap;

/**
 *
 * @author Ashok
 */
public class Tank
{
    private String name;
    
    private int team;
    private double health;
    public static final int MAX_HEALTH = 150;
    
    private boolean isDead = false;
    
    public final static double TANK_SPEED = 0.07/*(1.0*GameScreen.FRAME_RATE/1000.0)*/; //pixels per millisecond. The first number is pixels/frame.
    public final static double TANK_ROTATION_SPEED = Math.PI/1000; //radians per millisecond
    
    public static float imgScale = 3;
    public float explosionStateTime = 0f;
    
    private HashMap<String, Boolean> visibility;
    private HashMap<Integer, Boolean> visibilityB;
    
    private double x,y,xVel,yVel, speed = 0, rotation = 0, rotationSpeed = 0;
    public double initX, initY;
    
    public Tank(String name, float x, float y)
    {
        this.name = name;
        this.x = x;
        this.y = y;
        xVel = 0;
        yVel = 0;
        health = MAX_HEALTH;
        visibility = new HashMap<String, Boolean>();
        visibilityB = new HashMap<Integer, Boolean>();
    }
    
    public HashMap<String, Boolean> getVisibility()
    {
        return visibility;
    }
    
    /**
     * 
     * @param tName
     * @param visible
     * @return true if visibility has changed, false if not.
     */
    public boolean setVisibility(String tName, boolean visible)
    {
        if(!visibility.containsKey(tName) || visible != visibility.get(tName))
        {
            visibility.put(tName, visible);
            return true;
        }
        return false;
    }
    
    public boolean isVisible(String tName)
    {
        if(visibility.containsKey(tName))
            return visibility.get(tName);
        return false;
    }
    
    /**
     * 
     * @param bName
     * @param visible
     * @return true if visibility has changed, false if not.
     */
    public boolean setVisibilityB(int bName, boolean visible)
    {
        if(!visibilityB.containsKey(bName) || visible != visibilityB.get(bName))
        {
            visibilityB.put(bName, visible);
            return true;
        }
        return false;
    }
    
    public boolean isVisibleB(int bName)
    {
        if(visibilityB.containsKey(bName))
            return visibilityB.get(bName);
        return false;
    }
    
    public void update(long deltaTime)
    {
        x += xVel*deltaTime;
        y += yVel*deltaTime;
        if(xVel > 0)
        {
            if(yVel == 0)
            {
                rotation = 0;
            }
            else if(yVel > 0)
            {
                rotation = Math.PI/4;
            }
            else
            {
                rotation = Math.PI*7/4;
            }
        }
        else if(xVel < 0)
        {
            if(yVel == 0)
            {
                rotation = Math.PI;
            }
            else if(yVel > 0)
            {
                rotation = Math.PI*3/4;
            }
            else
            {
                rotation = Math.PI*5/4;
            }
        }
        else
        {
            if(yVel > 0)
            {
                rotation = Math.PI/2;
            }
            else if(yVel < 0)
            {
                rotation = Math.PI*3/2;
            }
        }
    }
    
    public double[] mockUpdate(long deltaTime)
    {
        double mockX = x, mockY = y, mockRotation = rotation;
        mockX += xVel*deltaTime;
        mockY += yVel*deltaTime;
        if(xVel > 0)
        {
            if(yVel == 0)
            {
                mockRotation = 0;
            }
            else if(yVel > 0)
            {
                mockRotation = Math.PI/4;
            }
            else
            {
                mockRotation = Math.PI*7/4;
            }
        }
        else if(xVel < 0)
        {
            if(yVel == 0)
            {
                mockRotation = Math.PI;
            }
            else if(yVel > 0)
            {
                mockRotation = Math.PI*3/4;
            }
            else
            {
                mockRotation = Math.PI*5/4;
            }
        }
        else
        {
            if(yVel > 0)
            {
                mockRotation = Math.PI/2;
            }
            else if(yVel < 0)
            {
                mockRotation = Math.PI*3/2;
            }
        }
        double[] d = {mockX,mockY,mockRotation};
        return d;
    }
    
    public void updateR(long deltaTime)
    {
        rotation += rotationSpeed*deltaTime;
        if(rotation >= 360)
        {
            rotation -= 360;
        }
        else if(rotation < 0)
        {
            rotation += 360;
        }
        double myXVel = Math.cos(rotation)*speed*deltaTime;
        double myYVel = Math.sin(rotation)*speed*deltaTime;
        x += myXVel;
        y += myYVel;
    }
    
    public double[] mockUpdateR(long deltaTime)
    {
        double mockRotation = rotation, mockX = x, mockY = y;
        mockRotation += rotationSpeed*deltaTime;
        if(mockRotation >= 360)
        {
            mockRotation -= 360;
        }
        else if(mockRotation < 0)
        {
            mockRotation += 360;
        }
        double myXVel = Math.cos(mockRotation)*TANK_SPEED*deltaTime;
        double myYVel = Math.sin(mockRotation)*TANK_SPEED*deltaTime;
        mockX += myXVel;
        mockY += myYVel;
        double[] d = {mockX,mockY,mockRotation};
        return d;
    }
    
    public boolean isDead()
    {
        return isDead;
    }
    
    public void setDead(boolean dead)
    {
        isDead = dead;
    }
    
    public double getHealth()
    {
        return health;
    }
    
    public void setHealth(double newHealth)
    {
        health = newHealth;
    }
    
    public int getTeam()
    {
        return team;
    }
    
    public void setTeam(int team)
    {
        this.team = team;
    }
    
    public String getName()
    {
        return name;
    }

    public void setName(String nombre)
    {
        name = nombre;
    }
    
    public double getX()
    {
        return x;
    }
    
    public double getY()
    {
        return y;
    }
    
    public double getXSpeed()
    {
        return xVel;
    }
    
    public double getYSpeed()
    {
        return yVel;
    }
    
    public double getSpeed()
    {
        return speed;
    }
    
    public double getRotation()
    {
        return rotation;
    }
    
    public double getRotationSpeed()
    {
        return rotationSpeed;
    }
    
    public void setXSpeed(double xSpd)
    {
        xVel = xSpd;
    }
    
    public void setYSpeed(double ySpd)
    {
        yVel = ySpd;
    }
    
    public void setX(double newX)
    {
        x = newX;
    }
    
    public void setY(double newY)
    {
        y = newY;
    }
    
    public void setRotation(double newRot)
    {
        rotation = newRot;
    }
    
    public void setRotationSpeed(double rotSpd)
    {
        rotationSpeed = rotSpd;
    }
    
    public void setSpeed(double newSpd)
    {
        speed = newSpd;
    }
    
    public void rotate(double angle)
    {
        rotation += angle;
    }
}
