package com.krish.ai.interviewgenerator.dto.response;

import com.krish.ai.interviewgenerator.util.Difficulty;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponse {

    private Long id;
    private String question;
    private Difficulty difficulty;
    private String topic;
    private String type;
    private Instant createdAt;
    private Integer rating;

    public QuestionResponse(String question, Difficulty difficulty, String topic) {
        this.question = question;
        this.difficulty = difficulty;
        this.topic = topic;
    }
}
