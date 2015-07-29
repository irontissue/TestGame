/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Ashok
 */
public class GameMessage
{
    public ArrayList<String> message; //designed to be used for non-repetitive messages
    public ArrayList<Color> color;
    public ArrayList<Boolean> isImmutables;
    
    public GameMessage()
    {
        message = new ArrayList<String>();
        color = new ArrayList<Color>();
        isImmutables = new ArrayList<Boolean>();
    }
    
    public GameMessage(String str, Color c)
    {
        message = new ArrayList<String>();
        color = new ArrayList<Color>();
        isImmutables = new ArrayList<Boolean>();
        message.add(str);
        color.add(c);
        isImmutables.add(false);
    }
    
    public void addString(String str, Color c)
    {
        message.add(str);
        color.add(c);
        isImmutables.add(false);
    }
    
    public String getMessage()
    {
        String str = "";
        for(String s : message)
        {
            str += s;
        }
        return str;
    }

    /**
     * sets all WHOLE WORD instances of str to color c. If isName is true, then
     * that section of the string is stored as true and won't be modified any
     * further
     * 
     * @param str       The string to search for
     * @param c         The color to make that string
     * @param isName    true = make all instances of str immutable
     */
    /*public void setStringColor(String str, Color c, boolean isName)
    {
        for(int i = 0; i < message.size(); i++)
        {
            String s = message.get(i);
            if(wholeWordHelper(s, str) && !isImmutables.get(i))
            {
                int index = message.indexOf(s);
                message.remove(index);
                Color orig = color.remove(index);
                isImmutables.remove(index);
                String[] msg = s.split(str);
                for(String ss : msg)
                {
                    if(ss.equals("")) {
                        message.add(str);
                        color.add(c);
                        isImmutables.add(isName);
                        ++i;
                    } else {
                        message.add(ss);
                        color.add(orig);
                        isImmutables.add(false);
                        ++i;
                        if(!msg[msg.length-1].equals(ss) || s.endsWith(str))
                        {
                            message.add(str);
                            color.add(c);
                            isImmutables.add(isName);
                            ++i;
                        }
                    }
                }
            }
        }
    }*/
}
