/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import java.util.HashMap;

/**
 *
 * @author Ashok
 */
public class AudioPlayer
{
    private HashMap<String, Sound> sfx;
    
    private HashMap<String, Music> music;
    
    private float musicVolume, sfxVolume;
    
    public AudioPlayer()
    {
        sfx = new HashMap<String, Sound>();
        FileHandle f = Gdx.files.internal("click.ogg"); sfx.put(f.nameWithoutExtension(), Gdx.audio.newSound(f));
        f = Gdx.files.internal("countdown.ogg"); sfx.put(f.nameWithoutExtension(), Gdx.audio.newSound(f));
        f = Gdx.files.internal("death.ogg"); sfx.put(f.nameWithoutExtension(), Gdx.audio.newSound(f));
        f = Gdx.files.internal("death2.ogg"); sfx.put(f.nameWithoutExtension(), Gdx.audio.newSound(f));
        f = Gdx.files.internal("death3.ogg"); sfx.put(f.nameWithoutExtension(), Gdx.audio.newSound(f));
        f = Gdx.files.internal("dig.ogg"); sfx.put(f.nameWithoutExtension(), Gdx.audio.newSound(f));
        f = Gdx.files.internal("hit.ogg"); sfx.put(f.nameWithoutExtension(), Gdx.audio.newSound(f));
        f = Gdx.files.internal("hitwall.ogg"); sfx.put(f.nameWithoutExtension(), Gdx.audio.newSound(f));
        f = Gdx.files.internal("msg.ogg"); sfx.put(f.nameWithoutExtension(), Gdx.audio.newSound(f));
        f = Gdx.files.internal("shoot.ogg"); sfx.put(f.nameWithoutExtension(), Gdx.audio.newSound(f));
        f = Gdx.files.internal("shoot2.ogg"); sfx.put(f.nameWithoutExtension(), Gdx.audio.newSound(f));
        f = Gdx.files.internal("dig2.wav"); sfx.put(f.nameWithoutExtension(), Gdx.audio.newSound(f));
        music = new HashMap<String, Music>();
        f = Gdx.files.internal("title2.mp3"); music.put("title", Gdx.audio.newMusic(f));
        f = Gdx.files.internal("game.ogg"); music.put(f.nameWithoutExtension(), Gdx.audio.newMusic(f));
        musicVolume = 1f;
        sfxVolume = 1f;
    }
    
    public void playMusic(String key, boolean looping, float vol) //stops music and plays
    {
        stopMusic();
        Music m = music.get(key);
        m.setLooping(looping);
        m.setVolume(musicVolume*vol);
        m.play();
    }
    
    public void playMusic(String key) //stops all playing musics and plays looping music
    {
        playMusic(key, true, 1f);
    }
    
    public void playMusic(String key, float vol)
    {
        playMusic(key, true, vol);
    }
    
    public void playMusicWithoutStopping(String key) //plays music on top of any others
    {
        Music m = music.get(key);
        m.setLooping(true);
        m.setVolume(musicVolume);
        m.play();
    }
    
    public void stopMusic(String key) //stops a specific music
    {
        music.get(key).stop();
    }
    
    public void stopMusic() //stops all music
    {
        for(String s : music.keySet())
            stopMusic(s);
    }
    
    public long playSfx(String key) //default is non-looping
    {
        return playSfx(key, false, 1f);
    }
    
    public long playSfx(String key, boolean looping)
    {
        return playSfx(key, looping, 1f);
    }
    
    public long playSfx(String key, boolean looping, float vol)
    {
        Sound s = sfx.get(key);
        long myID = s.play(vol*sfxVolume);
        s.setLooping(myID, looping);
        return myID;
    }
    
    public void stopSfx(String key, long id) //only necessary for looping sfx's
    {
        sfx.get(key).stop(id);
    }
    
    public void stopSfx(String key)
    {
        sfx.get(key).stop();
    }
    
    public void setMusicVolume(float vol)
    {
        musicVolume = vol;
        for(Music m : music.values())
        {
            m.setVolume(musicVolume);
        }
    }
    
    public void setSfxVolume(float vol)
    {
        sfxVolume = vol;
    }
    
    public float getMusicVolume()
    {
        return musicVolume;
    }
    
    public float getSfxVolume()
    {
        return sfxVolume;
    }
    
    public void dispose()
    {
        for(Sound s : sfx.values())
            s.dispose();
        for(Music m : music.values())
            m.dispose();
    }
}
