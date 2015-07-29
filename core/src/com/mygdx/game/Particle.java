/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Encapsulates all necessary traits of one particle effect.
 * @author Ashok
 */
public class Particle
{
    public TextureRegion img;
    
    public double x,y,dx,dy,d2x,d2y;
    public double rotation = 0, rotSpd;
    public float szInc;
    public float texSize;
    public float alpha = 1f;
    public int lifetime; //millis
    public int max_lifetime;
    
    public boolean fading;
    
    /**
     * Particle effect. Call update() from main thread to update this particle.
     * @param tex       The textureregion to draw
     * @param texSize   The size (width/height) of the textureregion
     * @param szInc     rate of change of texSize
     * @param lifetime  lifetime in millis. Lifetime of 0 means this particle will NEVER DIE
     * @param x         initial x
     * @param y         initial y
     * @param xSpd      dx
     * @param ySpd      dy
     * @param xAccel    d2x
     * @param yAccel    d2y
     * @param rotSpd    rate of change of rotation
     * @param fading    if true, the alpha will change according to lifetime. If false, alpha = 1f.
     */
    public Particle(TextureRegion tex, float texSize, float szInc, int lifetime, double x, double y, double xSpd, double ySpd, double xAccel, double yAccel, double rotSpd, boolean fading)
    {
        this.img = tex;
        this.texSize = texSize;
        this.szInc = szInc;
        this.lifetime = lifetime;
        max_lifetime = lifetime;
        this.x = x;
        this.y = y;
        this.dx = xSpd;
        this.dy = ySpd;
        this.d2x = xAccel;
        this.d2y = yAccel;
        this.rotSpd = rotSpd;
        this.fading = fading;
    }
    
    /**
     * Simpler constructor
     */
    public Particle(TextureRegion tex, float texSize, int lifetime, double x, double y, double xSpd, double ySpd, boolean fading)
    {
        this(tex, texSize, 0, lifetime, x, y, xSpd, ySpd, 0, 0, 0, fading);
    }
    
    /**
     * Simplest constructor
     */
    public Particle(TextureRegion tex, int lifetime, double x, double y)
    {
        this(tex, Math.max(tex.getRegionWidth(), tex.getRegionHeight()),lifetime,x,y,0,0,false);
    }
    
    /**
     * Updates the particle based on delta.
     * @param delta Elapsed time from last update, in millis
     */
    public void update(float delta)
    {
        dx += d2x*delta;
        dy += d2y*delta;
        x += dx*delta;
        y += dy*delta;
        lifetime -= delta;
        rotation += rotSpd*delta;
        texSize += szInc*delta;
        if(lifetime < 0) {
            lifetime = 0;
        }
        if(fading)
            alpha = (float)lifetime/max_lifetime;
    }
    
    public boolean isDone()
    {
        if(max_lifetime == 0)
        {
            return false;
        }
        else if(lifetime <= 0)
        {
            lifetime = 0;
            return true;
        }
        return false;
    }
}
