package hu.virgo.calendar.application.exception;

import org.springframework.http.HttpStatusCode;

public record Problem(String detail, HttpStatusCode httpStatusCode) {
}
