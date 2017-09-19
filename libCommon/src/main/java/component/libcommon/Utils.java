package component.libcommon;

import android.util.Log;

import java.util.Arrays;

/**
 * Created by ws on 17/9/8.
 */

public class Utils {

    public static final String getUtilName(String suffix) {
        return "from utils " + suffix;
    }

    public static final void printComponentName(String... compA) {
        Log.d("Utils", "componets:" + Arrays.toString(compA));
    }
}
