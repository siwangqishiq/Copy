package panyi.xyz.copy;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import panyi.xyz.copy.library.SelectableText;

public class TestActivity extends AppCompatActivity {
    private SelectableText mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mTextView = findViewById(R.id.text);
        mTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mTextView.showSelectable();
                return true;
            }
        });

        mTextView.setOnClickListener((v)->{
            mTextView.hideSelectable();
        });
    }
}
