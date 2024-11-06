package fr.ubx.poo.ugarden.game;

import fr.ubx.poo.ugarden.go.personage.Gardener;


public class Game {

    private final Configuration configuration;
    private final World world;
    private final Gardener gardener;
    private final Position hedgehogPosition;
    private boolean switchLevelRequested = false;
    private int switchLevel;
    public Game(World world, Configuration configuration, Position gardenerPosition, Position hedgehogPosition) {
        this.configuration = configuration;
        this.world = world;
        this.hedgehogPosition=hedgehogPosition;
        gardener = new Gardener(this, gardenerPosition);
    }

    public Configuration configuration() {
        return configuration;
    }

    public Gardener getGardener() {
        return this.gardener;
    }
    public Position getHedgehogPosition() {
        return this.hedgehogPosition;
    }

    public World world() {
        return world;
    }

    public boolean isSwitchLevelRequested() {
        return switchLevelRequested;
    }

    public int getSwitchLevel() {
        return switchLevel;
    }

    public void requestSwitchLevel(int level) {
        this.switchLevel = level;
        world().setCurrentLevel(level);
        switchLevelRequested = true;
    }

    public void clearSwitchLevel() {
        switchLevelRequested = false;
    }

}
