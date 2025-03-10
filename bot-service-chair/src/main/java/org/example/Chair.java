package org.example;

import lombok.Data;

@Data
public class Chair {
    private Long id;
    private int floor;
    private int room;
    private String holder;
    private boolean available;
}
