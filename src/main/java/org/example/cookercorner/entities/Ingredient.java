package org.example.cookercorner.entities;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private double amount;

    private String measurement;

    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;
}
