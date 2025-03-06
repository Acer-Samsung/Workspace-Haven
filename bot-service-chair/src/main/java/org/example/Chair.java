package org.example;

import lombok.Data;

@Data
public class Chair {
    private Long id;
    private int floor;
    private String room;
    private String holder;
    private boolean available;
}
