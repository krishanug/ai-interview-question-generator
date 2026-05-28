package com.krish.ai.interviewgenerator.constants;

public final class AppConstants {

    private AppConstants() {
    }

    public static final class ApiPath {
        public static final String QUESTIONS_BASE = "/api/v1/questions";
        public static final String GENERATE = "/generate";
        public static final String RATE = "/{id}/rating";

        private ApiPath() {
        }
    }

    public static final class ApiDefaults {
        public static final String PAGE = "0";
        public static final String SIZE = "10";
        public static final String SORT_BY = "createdAt";
        public static final String SORT_DIR = "desc";
        public static final String SORT_DIR_DESC = "desc";
        public static final String SORT_FIELD_RATING = "rating";
        public static final String SORT_FIELD_CREATED_AT = "createdAt";

        private ApiDefaults() {
        }
    }

    public static final class Messages {
        public static final String REQUEST_PROCESSED = "Request processed successfully";
        public static final String QUESTIONS_GENERATED = "Questions generated successfully";
        public static final String QUESTIONS_FETCHED = "Questions fetched successfully";
        public static final String QUESTION_RATED = "Question rated successfully";

        private Messages() {
        }
    }

    public static final class EntityFields {
        public static final String TOPIC = "topic";
        public static final String DIFFICULTY = "difficulty";
        public static final String RATING = "rating";

        private EntityFields() {
        }
    }

    public static final class QuestionDefaults {
        public static final String TYPE_GENERATED = "GENERATED";
        public static final int RATING_DEFAULT = 0;

        private QuestionDefaults() {
        }
    }

    public static final class Validation {
        public static final int QUESTION_MIN_LENGTH = 8;
        public static final int QUESTION_MAX_LENGTH = 1000;
        public static final int RATING_MIN = 1;
        public static final int RATING_MAX = 5;

        private Validation() {
        }
    }
}
