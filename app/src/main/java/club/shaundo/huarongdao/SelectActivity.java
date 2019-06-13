package club.shaundo.huarongdao;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SelectActivity extends AppCompatActivity {

    private int[] rawfiles = {
            R.raw.mapfile1,
            R.raw.mapfile2,
            R.raw.mapfile3,
            R.raw.mapfile4,
            R.raw.mapfile5,
            R.raw.mapfile6,
            R.raw.mapfile7,
            R.raw.mapfile8,
            R.raw.mapfile9,
            R.raw.mapfile10
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        GridLayout gridLayout = findViewById(R.id.function_grid);
        int columnCount = gridLayout.getColumnCount();
        int widthCount = gridLayout.getRowCount();
        int screenWidth = this.getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = this.getWindowManager().getDefaultDisplay().getHeight();
        TextView mTextViewContent = findViewById(R.id.textView2);
        Typeface mtypeface=Typeface.createFromAsset(getAssets(),"font.TTF");
        mTextViewContent.setTypeface(mtypeface);
        for (int i = 0; i < rawfiles.length; ++i) {
            MyButton button = new MyButton(this);
            button.setText(Integer.toString(i + 1));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SelectActivity.this, MainActivity.class);
                    intent.putExtra("mapfile", rawfiles[Integer.parseInt((String)((Button)v).getText()) - 1]);
                    startActivity(intent);
                }
            });
            button.setWidth(screenWidth / columnCount);
            button.setHeight(screenHeight / widthCount);
            gridLayout.addView(button);
        }
    }
}
