package com.alborgis.ting.mainapp.common;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;

public class TINGSound {
	
	private static SoundPool soundPool = null;
	
	public static void playSound(final Context _ctx, int soundResID){
		if(soundPool == null){
			soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
		}
		final int soundId = soundPool.load(_ctx, soundResID, 1);
		soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
				AudioManager audioManager = (AudioManager)_ctx.getSystemService(Context.AUDIO_SERVICE);
		        float curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		        float maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		        float leftVolume = curVolume/maxVolume;
		        float rightVolume = curVolume/maxVolume;
		        int priority = 1;
		        int no_loop = 0;
		        float normal_playback_rate = 1f;
		        soundPool.play(soundId, leftVolume, rightVolume, priority, no_loop, normal_playback_rate);
			}
		});
		
	}
}
