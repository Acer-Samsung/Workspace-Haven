package org.example.chairservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "chairs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Chair {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int floor;
    private int room;

    private boolean isAvailable;

    @OneToOne
    private User holder;
}
