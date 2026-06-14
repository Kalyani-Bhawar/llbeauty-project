package com.llbeauty.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Object handleException(Exception ex, HttpServletRequest request) {
        String requestedUri = request.getRequestURI();
        
        // If it's an API request, return JSON response
        if (requestedUri.startsWith("/api/")) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()));
        }

        // If it's an admin request, return an admin error view or redirect
        if (requestedUri.startsWith("/admin/")) {
            ModelAndView mav = new ModelAndView();
            mav.addObject("errorMessage", "An error occurred: " + ex.getMessage());
            mav.setViewName("admin/error"); // Make sure this view exists, or use a general one
            return mav;
        }

        // Otherwise return a generic error page
        ModelAndView mav = new ModelAndView();
        mav.addObject("errorMessage", "An unexpected error occurred.");
        mav.setViewName("error");
        return mav;
    }
    
    public static class ErrorResponse {
        private int status;
        private String message;

        public ErrorResponse(int status, String message) {
            this.status = status;
            this.message = message;
        }

        public int getStatus() { return status; }
        public void setStatus(int status) { this.status = status; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
