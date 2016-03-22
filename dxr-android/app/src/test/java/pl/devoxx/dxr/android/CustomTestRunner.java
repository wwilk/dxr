package pl.devoxx.dxr.android;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.manifest.AndroidManifest;
import org.robolectric.res.Fs;

/**
 * Created by wilk on 20/12/15.
 */
public class CustomTestRunner extends RobolectricGradleTestRunner {

    public CustomTestRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected AndroidManifest getAppManifest(Config config) {

        String manifestPath = "src/main/AndroidManifest.xml";
        return new AndroidManifest(Fs.fileFromPath(manifestPath), Fs.fileFromPath("src/main/res"),
                Fs.fileFromPath("src/main/assets")) {
            @Override
            public int getTargetSdkVersion() {
                return 17;
            }
        };
    }
}
