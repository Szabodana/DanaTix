package com.project.danatix.utils;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.List;

public class FieldErrorsExtractor {
    List<FieldError> fieldErrorsList = new ArrayList<>();
    List<String> failedFields = new ArrayList<>();

    public FieldErrorsExtractor(MethodArgumentNotValidException exception) {
        fieldErrorsList = exception
                .getBindingResult()
                .getFieldErrors();

        failedFields = exception
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getField)
                .distinct()
                .toList();
    }

    public List<FieldError> getFieldErrorsList() {
        return fieldErrorsList;
    }

    public void setFieldErrorsList(List<FieldError> fieldErrorsList) {
        this.fieldErrorsList = fieldErrorsList;
    }

    public List<String> getFailedFields() {
        return failedFields;
    }

    public void setFailedFields(List<String> failedFields) {
        this.failedFields = failedFields;
    }

    public FieldError getFirstError() {
        return fieldErrorsList.get(0);
    }
}
