package com.example.foodie.services.interfaces;

import com.example.foodie.dtos.BiasDTO;
import com.example.foodie.models.Bias;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface BiasService {
    Bias addBias(Authentication authentication, BiasDTO biasDTO);
    Bias updateBias(Authentication authentication, BiasDTO biasDTO);
    List<Bias> getAllBiasByUser(Authentication authentication);
    void attachAllBiasesToUser(String email);
}
