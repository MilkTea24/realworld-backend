package com.milktea.main.util.exceptions;

import java.util.List;

public record ErrorResponse(Errors errors) {
    public record Errors(List<String> body) {
    }
}

