package club.shaundo.huarongdao;

import android.content.Context;
import android.util.AttributeSet;

public class MyButton extends android.support.v7.widget.AppCompatButton {

    public MyButton(Context context) {
        super(context);
        setBackground(getResources().getDrawable(R.drawable.button_selector));
    }
    public MyButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackground(getResources().getDrawable(R.drawable.button_selector));
    }
}
