package fr.ubx.poo.ugarden.launcher;

import fr.ubx.poo.ugarden.Functions;
import fr.ubx.poo.ugarden.game.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class GameLauncher {

    private int levels;
    private MapLevel[] mapLevels;

    private GameLauncher() {
    }

    public static GameLauncher getInstance() {
        return LoadSingleton.INSTANCE;
    }

    private int integerProperty(Properties properties, String name, int defaultValue) {
        return Integer.parseInt(properties.getProperty(name, Integer.toString(defaultValue)));
    }

    private boolean booleanProperty(Properties properties, String name, boolean defaultValue) {
        return Boolean.parseBoolean(properties.getProperty(name, Boolean.toString(defaultValue)));
    }

    private Configuration getConfiguration(Properties properties) {

        // Load parameters
        int hornetMoveFrequency = integerProperty(properties, "hornetMoveFrequency", 1);
        int gardenerEnergy = integerProperty(properties, "gardenerEnergy", 100);
        int energyBoost = integerProperty(properties, "energyBoost", 50);
        int energyRecoverDuration = integerProperty(properties, "energyRecoverDuration", 1);
        int diseaseDuration = integerProperty(properties, "diseaseDuration", 5);

        return new Configuration(gardenerEnergy, energyBoost, hornetMoveFrequency, energyRecoverDuration, diseaseDuration);
    }

    public Game load(File file) throws IOException {
        List<MapLevel> mapLevels= Functions.readLevelsFromfile(file);
        Properties config = new Properties();
        Position gardenerPosition = mapLevels.get(0).getGardenerPosition();
        Configuration configuration = getConfiguration(config);
        if (gardenerPosition == null)
            throw new RuntimeException("Gardener not found");
        World world = new World(mapLevels.size());
        Game game = new Game(world, configuration, mapLevels.get(0).getGardenerPosition(), mapLevels.get(mapLevels.size()-1).getHedgeHogPosition());
        for(int i=0;i<mapLevels.size();i++){
            MapLevel level = mapLevels.get(i);
            Map newlvl = new Level(game, i+1, level);
            world.put(i+1, newlvl);
        }

        return game;
    }
    public Game load(MapLevel mapLevel, int levelx) {
        Properties config = new Properties();
        Position gardenerPosition = mapLevel.getGardenerPosition();
        Position hedgehogPosition = mapLevel.getHedgeHogPosition();
        if (gardenerPosition == null)
            throw new RuntimeException("Gardener not found");
        Configuration configuration = getConfiguration(config);
        World world = new World(1);
        Game game = new Game(world, configuration, gardenerPosition,hedgehogPosition);
        Map level = new Level(game, levelx, mapLevel);
        world.put(levelx, level);
        return game;
    }

    private static class LoadSingleton {
        static final GameLauncher INSTANCE = new GameLauncher();
    }

}
