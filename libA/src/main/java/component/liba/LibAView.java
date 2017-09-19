package component.liba;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by ws on 17/9/9.
 */

public class LibAView extends android.support.v7.widget.AppCompatTextView {
    public LibAView(Context context) {
        this(context, null);
    }

    public LibAView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LibAView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setText(R.string.tv_test_liba);

        setBackgroundResource(R.drawable.lib_a);

    }


}
