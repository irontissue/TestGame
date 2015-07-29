/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Bounces back to original speed if its speed reaches a threshold.
 * @author Ashok
 */
public class BouncingParticle extends Particle
{
    private double initXSpd, initYSpd;
    
    public BouncingParticle(TextureRegion tex, float texSize, float szInc, int lifetime, double x, double y, double xSpd, double ySpd, double xAccel, double yAccel, double rotSpd, boolean fading) {
        super(tex, texSize, szInc, lifetime, x, y, xSpd, ySpd, xAccel, yAccel, rotSpd, fading);
        initXSpd = xSpd;
        initYSpd = ySpd;
    }
    
    @Override
    public void update(float delta)
    {
        dx += d2x*delta;
        dy += d2y*delta;
        if(Math.abs(dx) > initXSpd)
            dx = initXSpd*Math.signum(-dx);
        if(Math.abs(dy) > initYSpd)
            dy = initYSpd*Math.signum(-dy);
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
}
