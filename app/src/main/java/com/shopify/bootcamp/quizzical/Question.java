package com.shopify.bootcamp.quizzical;

public class Question {

    private boolean answer;
    private String statement;

    public Question(String statement, boolean answer) {
        this.answer = answer;
        this.statement = statement;
    }

    public boolean getAnswer() {
        return answer;
    }

    public String getStatement() {
        return statement;
    }
}
