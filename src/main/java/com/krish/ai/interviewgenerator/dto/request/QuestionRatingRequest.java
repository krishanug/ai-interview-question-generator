package com.krish.ai.interviewgenerator.dto.request;

import com.krish.ai.interviewgenerator.constants.AppConstants;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class QuestionRatingRequest {

    @NotNull
    @Min(AppConstants.Validation.RATING_MIN)
    @Max(AppConstants.Validation.RATING_MAX)
    private Integer score;
}
