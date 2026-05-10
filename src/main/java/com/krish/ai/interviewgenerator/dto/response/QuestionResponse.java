package com.krish.ai.interviewgenerator.dto.response;

import com.krish.ai.interviewgenerator.util.Difficulty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponse {

    private String question;
    private Difficulty difficulty;
    private String topic;
}