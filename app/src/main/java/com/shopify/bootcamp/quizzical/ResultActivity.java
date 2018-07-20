package com.shopify.bootcamp.quizzical;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    public static final String KEY_SCORE = "score";
    public static final String KEY_TOTAL_QUESTIONS = "total";

    private TextView resultTextView;
    private Button retry_button;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        resultTextView = findViewById(R.id.result_text);
        retry_button = findViewById(R.id.retry_button);

        retry_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetQuiz();
            }
        });

        Intent intent = getIntent();
        int score = intent.getIntExtra(KEY_SCORE, -1);
        int totalQuestions = intent.getIntExtra(KEY_TOTAL_QUESTIONS, -1);

        String result = String.format("%d / %d", score, totalQuestions);
        resultTextView.setText(result);
    }

    private void resetQuiz() {
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
        finish();
    }
}
