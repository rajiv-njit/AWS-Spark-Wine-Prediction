package com.myproject.prediction.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WinePredictionController {
    @GetMapping("/predict")
    public String predictWine() {
        // Implementation for predicting wine quality
        return "Wine Prediction Result";
    }
}
