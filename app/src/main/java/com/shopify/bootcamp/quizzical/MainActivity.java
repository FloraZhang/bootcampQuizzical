package com.shopify.bootcamp.quizzical;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements QuizRepository.QuizCallback {

    private static final String USER_ANSWER = "user_answer";
    private static final String QUESTION_ANSWERED = "question_answered";
    private static final String CURRENT_QUESTION_INDEX = "current_question_index";
    private static final String SCORE = "score";
    private static final String PROGRESS = "progress";

    private Button trueButton;
    private Button falseButton;
    private TextView answerTextView;
    private TextView questionTextView;
    private Button nextButton;
    private ProgressBar progressBar;

    private boolean userAnswer;
    private boolean questionAnswered = false;
    private Quiz quiz;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private int progress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        trueButton = findViewById(R.id.true_button);
        falseButton = findViewById(R.id.false_button);
        answerTextView = findViewById(R.id.answer_text);
        questionTextView = findViewById(R.id.question);
        nextButton = findViewById(R.id.next_button);
        progressBar = findViewById(R.id.progress_bar);

        trueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Hi", "True button clicked");
                checkAnswer(true);
            }
        });
        falseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Hi", "False button clicked");
                checkAnswer(false);
            }

        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextQuestion();
            }
        });

        if(savedInstanceState != null) {
            questionAnswered = savedInstanceState.getBoolean(QUESTION_ANSWERED, false);
            userAnswer = savedInstanceState.getBoolean(USER_ANSWER);
            currentQuestionIndex = savedInstanceState.getInt(CURRENT_QUESTION_INDEX, -1);
            score = savedInstanceState.getInt(SCORE, 0);
            progress = savedInstanceState.getInt(PROGRESS, 0);
        }

        int id = getIntent().getIntExtra("quiz_id", -1);
        progressBar.setMax(100);
        new QuizRepository(this).getRemoteQuiz(id, this);
    }

    private void nextQuestion() {
        currentQuestionIndex++;
        progressBar.incrementProgressBy(50);
        if (currentQuestionIndex >= quiz.getQuestions().size()) {
            //show result activity
            Intent intent = new Intent(this, ResultActivity.class);
            intent.putExtra(ResultActivity.KEY_SCORE, score);
            intent.putExtra(ResultActivity.KEY_TOTAL_QUESTIONS, quiz.getQuestions().size());
            startActivity(intent);
            finish();
        }
        else {
            questionAnswered =false;
            showQuestion();
        }
    }

    private void showQuestion() {
        Question question = quiz.getQuestions().get(currentQuestionIndex);
        questionTextView.setText(question.getStatement());
        answerTextView.setText("");
        trueButton.setEnabled(true);
        falseButton.setEnabled(true);
        nextButton.setEnabled(false);
        progressBar.setProgress(progress);
    }

    private void checkAnswer(boolean answerToCheck) {
        questionAnswered = true;
        userAnswer = answerToCheck;
        Question question = quiz.getQuestions().get(currentQuestionIndex);

        if (answerToCheck == question.getAnswer()) {
            answerTextView.setText(R.string.correct_answer);
            score++;
        } else {
            answerTextView.setText(R.string.wrong_answer);
        }

        trueButton.setEnabled(false);
        falseButton.setEnabled(false);
        progress += 100/quiz.getQuestions().size();
        progressBar.setProgress(progress);

        nextButton.setEnabled(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean(USER_ANSWER, userAnswer);
        bundle.putBoolean(QUESTION_ANSWERED, questionAnswered);
        bundle.putInt(CURRENT_QUESTION_INDEX, currentQuestionIndex);
        bundle.putInt(SCORE, score);
        bundle.putInt(PROGRESS, progress);
    }

    @Override
    public void onFailure() {
        Toast.makeText(this, "Unable to fetch quiz", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onSuccess(final Quiz quiz) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e("onSuccess", "Got quiz back");
                MainActivity.this.quiz = quiz;
                showQuestion();
                if(questionAnswered) {
                    checkAnswer(userAnswer);
                }
            }
        });

    }
}
