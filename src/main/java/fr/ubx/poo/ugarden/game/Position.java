package fr.ubx.poo.ugarden.game;

import java.util.Random;

public record Position (int level, int x, int y) {
    public static Position random(int level, int width, int height){
        Random random = new Random();
        int randomX = random.nextInt(width);
        int randomY = random.nextInt(height);
        return new Position(level, randomX, randomY);
    }
}