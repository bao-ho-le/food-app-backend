package com.example.foodie.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Bias {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Min(value = 1, message = "score phải >= 1")
    @Max(value = 5, message = "score phải <= 5")
    @Builder.Default
    private Float score = 2.5f;

    @NotNull
    @ManyToOne
    @JoinColumn(name="tag_id")
    private Tag tag;

    @NotNull
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @NotNull
    @Column(updatable=false)
    private LocalDateTime createdAt;

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
