package fr.ubx.poo.ugarden.launcher;

import fr.ubx.poo.ugarden.game.Position;

import static fr.ubx.poo.ugarden.launcher.MapEntity.*;

public class MapLevel {

    private final int width;
    private final int height;
    private MapEntity[][] grid;
    private int level;


    private Position gardenerPosition = null;
    private Position hedgehogPosition= null;

    public MapLevel(int width, int height, int level) {
        this.width = width;
        this.height = height;
        this.level=level;
        this.grid = new MapEntity[height][width];
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public MapEntity get(int i, int j) {
        return grid[j][i];
    }

    public void set(int i, int j, MapEntity mapEntity) {
        grid[j][i] = mapEntity;
    }
    public void setGrid(MapEntity mapEntity[][]) {
        this.grid = mapEntity;
    }


    public Position getGardenerPosition() {
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                if (grid[j][i] == Gardener) {
                    if (gardenerPosition != null)
                        throw new RuntimeException("Multiple definition of gardener");
                    set(i, j, Grass);
                    // Gardener can be only on level 1
                    gardenerPosition = new Position(level, i, j);
                }
        return gardenerPosition;
    }

    public Position getHedgeHogPosition() {
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                if (grid[j][i] == Hedgehog) {
                    if (hedgehogPosition != null)
                        throw new RuntimeException("Multiple definition of hedgehog");
                    // Gardener can be only on level 1
                    hedgehogPosition = new Position(level,i, j);
                }
        return hedgehogPosition;
    }
}
