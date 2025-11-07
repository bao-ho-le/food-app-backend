package com.example.foodie.controllers;

import com.example.foodie.dtos.BiasDTO;
import com.example.foodie.models.Bias;
import com.example.foodie.services.interfaces.BiasService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/bias")
@AllArgsConstructor
public class BiasController {
    private BiasService biasService;

    @PostMapping
    public ResponseEntity<?> addBias(Authentication authentication, @Valid @RequestBody BiasDTO biasDTO){
        try {
            Bias newBias = biasService.addBias(authentication, biasDTO);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(newBias);

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> updateBias(Authentication authentication,@Valid @RequestBody BiasDTO biasDTO){
        try{

            Bias updatedBias = biasService.updateBias(authentication, biasDTO);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(updatedBias);

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllBiases(Authentication authentication){
        try{
            List<Bias> biases = biasService.getAllBiasByUser(authentication);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(biases);

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
}
