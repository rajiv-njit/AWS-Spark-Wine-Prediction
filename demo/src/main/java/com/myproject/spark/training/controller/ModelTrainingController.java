package com.myproject.spark.training.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ModelTrainingController {
    @GetMapping("/train")
    public String trainModel() {
        // Implementation for model training
        return "Model Training Result";
    }
}
