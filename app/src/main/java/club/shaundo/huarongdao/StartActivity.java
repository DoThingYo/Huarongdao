package club.shaundo.huarongdao;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        TextView mTextViewContent = findViewById(R.id.textView);
        Typeface mtypeface=Typeface.createFromAsset(getAssets(),"font.TTF");
        mTextViewContent.setTypeface(mtypeface);
    }

    public void start(View view) {
        Intent intent = new Intent(this, SelectActivity.class);
        startActivity(intent);
    }

    public void exit(View view) {
        finish();
    }
}
