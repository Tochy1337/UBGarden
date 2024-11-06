package fr.ubx.poo.ugarden.go.bonus;

import fr.ubx.poo.ugarden.game.Game;
import fr.ubx.poo.ugarden.game.Position;
import fr.ubx.poo.ugarden.go.decor.Decor;
import fr.ubx.poo.ugarden.go.personage.Gardener;

import java.util.Timer;
import java.util.TimerTask;

public class Insecticide extends Bonus {
    private  Game game;
    private final Timer timer;

    public Insecticide(Game game, Position position, Decor decor) {
        super(position, decor);
        this.game=game;
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                game.world().getGrid().get(position).getBonus().remove();
            }
        };
        timer.schedule(task, 10000);
    }

    public void cancelTimer(){
        timer.cancel();
    }
    @Override
    public void takenBy(Gardener gardener) {
        gardener.take(this);
    }
}
