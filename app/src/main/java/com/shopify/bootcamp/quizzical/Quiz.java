package com.shopify.bootcamp.quizzical;

import java.util.ArrayList;
import java.util.List;

public class Quiz {

    private String title;
    private int id;
    private List<Question> questions = new ArrayList();

    public List<Question> getQuestions() {
        return questions;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public void addQuestion(Question question) {
        questions.add(question);
    }

    private static Quiz quiz;

    public static Quiz getInstance() {
        if (quiz == null) {
            quiz = new Quiz();
            quiz.addQuestion(new Question("The moon is made of cheese", false));
            quiz.addQuestion(new Question("The sum of a triangle's internal angles are 180", true));
        }
        return quiz;
    }
}
