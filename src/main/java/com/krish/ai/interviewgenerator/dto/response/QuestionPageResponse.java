package com.krish.ai.interviewgenerator.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionPageResponse {
    private List<QuestionResponse> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
