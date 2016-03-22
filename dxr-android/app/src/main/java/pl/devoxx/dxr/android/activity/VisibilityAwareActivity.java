package pl.devoxx.dxr.android.activity;

/**
 * Created by wilk on 31/05/15.
 */
public class VisibilityAwareActivity extends DbActivity {

    private boolean visible;

    public boolean isVisible(){
        return visible;
    }

    @Override
    protected void onStart(){
        super.onStart();
        visible = true;
    }

    @Override
    protected void onPause(){
        super.onPause();
        visible = false;
    }


}
