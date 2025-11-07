package com.example.foodie.services.impl;

import com.example.foodie.dtos.BiasDTO;
import com.example.foodie.models.Bias;
import com.example.foodie.models.Tag;
import com.example.foodie.models.User;
import com.example.foodie.repos.BiasRepository;
import com.example.foodie.repos.TagRepository;
import com.example.foodie.repos.UserRepository;
import com.example.foodie.services.interfaces.BiasService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BiasServiceImpl implements BiasService {
    private BiasRepository biasRepository;
    private UserRepository userRepository;
    private TagRepository tagRepository;

    @Override
    public Bias addBias(Authentication authentication, BiasDTO biasDTO){

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        Tag tag = tagRepository.findById(biasDTO.getTagId())
                .orElseThrow(() -> new RuntimeException("Tag không tồn tại"));

        Optional<Bias> biasExisting = biasRepository.findByUser_idAndTag_Id(
                user.getId(),
                biasDTO.getTagId()
        );

        if (biasExisting.isPresent()){
            throw new RuntimeException("Tag này đã có rồi.");
        }

        Bias newBias = Bias.builder()
                .user(user)
                .tag(tag)
                .score(biasDTO.getScore())
                .build();

        return biasRepository.save(newBias);
    }

    @Override
    public Bias updateBias(Authentication authentication, BiasDTO biasDTO){

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        Tag tag = tagRepository.findById(biasDTO.getTagId())
                .orElseThrow(() -> new RuntimeException("Tag không tồn tại"));

        Optional<Bias> biasExisting = biasRepository.findByUser_idAndTag_Id(
                user.getId(),
                biasDTO.getTagId()
        );

        if(biasExisting.isPresent()){
            Bias updatedBias = biasExisting.get();
            updatedBias.setScore(biasDTO.getScore());
            return biasRepository.save(updatedBias);
        } else{
            Bias newBias = Bias.builder()
                    .user(user)
                    .tag(tag)
                    .score(biasDTO.getScore())
                    .build();

            return biasRepository.save(newBias);
        }
    }

    @Override
    public List<Bias> getAllBiasByUser(Authentication authentication){
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        List<Bias> biases = user.getBiases();

        return biases;
    }

    @Override
    public void attachAllBiasesToUser(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        List<Tag> allTags = tagRepository.findAll();

        if (allTags.isEmpty())
            throw new RuntimeException("Hiện chưa có tag nào trong hệ thống");

        for (Tag tag : allTags) {
            // Kiểm tra user đã có bias này chưa
            boolean alreadyHas = user.getBiases().stream()
                    .anyMatch(bias -> bias.getTag().getId().equals(tag.getId()));

            if (!alreadyHas) {
                Bias bias = new Bias();
                bias.setTag(tag);
                bias.setUser(user);
                bias.setScore(2.5f);
                user.getBiases().add(bias);
            }
        }

        userRepository.save(user);
    }
}
