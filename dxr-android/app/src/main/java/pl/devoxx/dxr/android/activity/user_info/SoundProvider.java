package pl.devoxx.dxr.android.activity.user_info;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.ArrayList;
import java.util.List;

import pl.devoxx.dxr.android.R;

/**
 * Created by wilk on 25/04/15.
 */
public class SoundProvider {

    private static SoundProvider INSTANCE;

    private final SoundPool soundPool;
    private final int successSoundId;
    private final int failureSoundId;
    private final List<Integer> loadedSounds;

    private SoundProvider(Context appContext){
        loadedSounds = new ArrayList<Integer>(2);
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loadedSounds.add(sampleId);
            }
        });
        successSoundId = soundPool.load(appContext, R.raw.success, 1);
        failureSoundId = soundPool.load(appContext, R.raw.failure, 1);
    }

    public static SoundProvider instance(Context appContext){
        if(INSTANCE == null){
            synchronized(SoundProvider.class) {
                if(INSTANCE == null) {
                    INSTANCE = new SoundProvider(appContext);
                }
            }
        }
        return INSTANCE;
    }

    public void playSuccessSound(){
        if(loadedSounds.contains(successSoundId)) {
            soundPool.play(successSoundId, 1.0f, 1.0f, 1, 0, 1f);
        }
    }

    public void playFailureSound(){
        if(loadedSounds.contains(failureSoundId)) {
            soundPool.play(failureSoundId, 1.0f, 1.0f, 1, 0, 1f);
        }
    }
}
