/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ugarden.go.personage;

import fr.ubx.poo.ugarden.game.Direction;
import fr.ubx.poo.ugarden.game.Game;
import fr.ubx.poo.ugarden.game.Position;
import fr.ubx.poo.ugarden.go.GameObject;
import fr.ubx.poo.ugarden.go.Movable;
import fr.ubx.poo.ugarden.go.TakeVisitor;
import fr.ubx.poo.ugarden.go.WalkVisitor;
import fr.ubx.poo.ugarden.go.bonus.*;
import fr.ubx.poo.ugarden.go.decor.Decor;

import java.util.Timer;
import java.util.TimerTask;

public class Gardener extends GameObject implements Movable, TakeVisitor, WalkVisitor {

    private int energy;
    private int keys;
    private Timer timer;
    private int diseaseLevel;
    private int insecticideSprays;
    private Direction direction;
    private boolean moveRequested = false;

    public Gardener(Game game, Position position) {

        super(game, position);
        this.direction = Direction.DOWN;
        this.energy = game.configuration().gardenerEnergy();
        this.keys=0;
        this.diseaseLevel=1;
        this.insecticideSprays=0;
    }

    @Override
    public void take(Key key) {
        keys+=1;
        key.remove();

    }
    public void take(Insecticide bomb) {
        insecticideSprays+=1;
        bomb.remove();
        bomb.cancelTimer();
    }
    public void take(Apple apple) {
        energy+=game.configuration().energyBoost();
        apple.remove();
        diseaseLevel=1;

    }


    public void take(PoisonedApple poisApple) {

        diseaseLevel+=1;
        poisApple.remove();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                if(diseaseLevel>=2) diseaseLevel-=1;
            }
        }, 1000L *game.configuration().diseaseDuration());
    }

    public int getEnergy() {
        return this.energy;
    }

    public int getDiseaseLevel() {
        return diseaseLevel;
    }

    public int getNumberOfKeys() {
        return keys;
    }

    public int getInsecticideSprays() {
        return insecticideSprays;
    }

    public void setInsecticideSprays(int insecticideSprays) {
        this.insecticideSprays = insecticideSprays;
    }

    public void requestMove(Direction direction) {
        if (direction != this.direction) {
            this.direction = direction;
            setModified(true);
        }
        moveRequested = true;
    }

    @Override
    public final boolean canMove(Direction direction) {
        // TO UPDATE
        return true;
    }

    @Override
    public void doMove(Direction direction) {
        if(timer!=null) timer.cancel();
        Position nextPos = direction.nextPosition(getPosition());
        Decor next = game.world().getGrid().get(nextPos);
        int x=game.world().getGrid().width();
        int y=game.world().getGrid().height();
        if(nextPos.x()<x && nextPos.y()<y && nextPos.x()>=0 && nextPos.y()>=0){
            if(game.world().getGrid().get(nextPos).walkableBy(this)){
                setPosition(nextPos);
                hurt(game.world().getGrid().get(nextPos).energyConsumptionWalk()*diseaseLevel);

            }
        }
        if (next != null)
            next.takenBy(this);
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(energy<game.configuration().gardenerEnergy())
                    hurt(-1);
                else timer.cancel();
            }
        };
        timer.scheduleAtFixedRate(task, 1000, 1000);
    }

    public void setKeys(int keys) {
        this.keys = keys;
    }

    public void update(long now) {
        if (moveRequested) {
            if (canMove(direction)) {
                doMove(direction);
            }
        }
        moveRequested = false;
    }

    public void hurt(int damage) {
        energy=energy-damage;
    }


    public Direction getDirection() {
        return direction;
    }


}
