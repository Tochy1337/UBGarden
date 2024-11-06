/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ugarden.go.personage;

import fr.ubx.poo.ugarden.game.Direction;
import fr.ubx.poo.ugarden.game.Game;
import fr.ubx.poo.ugarden.game.Position;
import fr.ubx.poo.ugarden.go.Movable;
import fr.ubx.poo.ugarden.go.WalkVisitor;
import fr.ubx.poo.ugarden.go.Walkable;
import fr.ubx.poo.ugarden.go.decor.Decor;
import fr.ubx.poo.ugarden.go.decor.Flowers;
import fr.ubx.poo.ugarden.go.decor.Tree;

import java.util.Timer;
import java.util.TimerTask;

public class Hornet extends Decor implements Movable, Walkable, WalkVisitor {
    private Direction direction;
    private Timer timer;
    private Game game;
    private String name;

    public Hornet(Game game,Position position,String id) {

        super(position);
        this.name =id;
        this.game=game;
        this.direction = Direction.RIGHT;
    }

    public String getName() {
        return name;
    }
    @Override
    public void doMove(Direction direction) {

    }
    @Override
    public final boolean canMove(Direction direction) {

        return true;
    }
    @Override
    public final boolean canWalkOn(Flowers flowers) {

        return true;
    }

    public void moveHornet(int width, int height) {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Position position;
                Direction direction1;
                do{
                    direction1=Direction.random();
                    position=direction1.nextPosition(getPosition(),1);
                }while(game.world().getGrid().get(position) instanceof Tree || position.x()<0 || position.y()<0 ||position.x()>=width || position.y()>=height);
                setPosition(position);
                direction=direction1;
            }
        };

        timer.scheduleAtFixedRate(task, 1000, game.configuration().hornetMoveFrequency()* 1000L);
    }

    public Direction getDirection() {
        return direction;
    }
}