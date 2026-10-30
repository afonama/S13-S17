package org.example.s16;

public class ScoreModel {
    private int score;

    public int getScore() {
        return score;
    }

    public void add() {
        score++;
    }

    public void subtract() {
        if (score > 0) {
            score--;
        }
    }

    public void reset() {
        score = 0;
    }
}