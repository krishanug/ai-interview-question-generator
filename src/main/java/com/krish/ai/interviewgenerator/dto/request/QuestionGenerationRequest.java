package com.krish.ai.interviewgenerator.dto.request;

import com.krish.ai.interviewgenerator.util.Difficulty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class QuestionGenerationRequest {

    @NotBlank
    private String topic;

    @NotNull
    private Difficulty difficulty;

    @Min(1)
    @Max(20)
    private Integer count;
}
