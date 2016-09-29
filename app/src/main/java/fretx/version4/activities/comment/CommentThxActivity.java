package fretx.version4.activities.comment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import fretx.version4.R;
import fretx.version4.activities.main.MainActivity;

public class CommentThxActivity extends AppCompatActivity {
    private Button buttonGoBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_thx);

        buttonGoBack = (Button)findViewById(R.id.btnGotoMenu);
        buttonGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CommentThxActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}
