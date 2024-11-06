    /*
     * Copyright (c) 2020. Laurent Réveillère
     */

    package fr.ubx.poo.ugarden.engine;


    import fr.ubx.poo.ugarden.Functions;
    import fr.ubx.poo.ugarden.game.Direction;
    import fr.ubx.poo.ugarden.game.Game;
    import fr.ubx.poo.ugarden.game.Position;
    import fr.ubx.poo.ugarden.go.bonus.Insecticide;
    import fr.ubx.poo.ugarden.go.bonus.Nest;
    import fr.ubx.poo.ugarden.go.decor.ClosedDoor;
    import fr.ubx.poo.ugarden.go.decor.Decor;
    import fr.ubx.poo.ugarden.go.decor.OpenedDoor;
    import fr.ubx.poo.ugarden.go.decor.ground.Grass;
    import fr.ubx.poo.ugarden.go.personage.Gardener;
    import fr.ubx.poo.ugarden.go.personage.Hornet;
    import fr.ubx.poo.ugarden.view.*;
    import javafx.animation.AnimationTimer;
    import javafx.application.Platform;
    import javafx.scene.Group;
    import javafx.scene.Scene;
    import javafx.scene.layout.Pane;
    import javafx.scene.layout.StackPane;
    import javafx.scene.paint.Color;
    import javafx.scene.text.Font;
    import javafx.scene.text.Text;
    import javafx.scene.text.TextAlignment;
    import javafx.stage.Stage;

    import java.util.*;
    import java.util.Timer;


    public final class GameEngine {

        private static AnimationTimer gameLoop;
        private final Game game;
        private Timer timer=new Timer();
        private final Gardener gardener;
        private List<Sprite> sprites = new LinkedList<>();
        private final Set<Sprite> cleanUpSprites = new HashSet<>();
        private final Stage stage;
        private final Pane layer = new Pane();
        private StatusBar statusBar;
        private Input input;

        public GameEngine(Game game, final Stage stage) {
            this.stage = stage;
            this.game = game;
            this.gardener = game.getGardener();
            initialize();
            buildAndSetGameLoop();
        }

        private void initialize() {
            Group root = new Group();
            sprites = new LinkedList<>();
            int height = game.world().getGrid().height();
            int width = game.world().getGrid().width();
            int sceneWidth = width * ImageResource.size;
            int sceneHeight = height * ImageResource.size;
            Scene scene = new Scene(root, sceneWidth, sceneHeight + StatusBar.height);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/application.css")).toExternalForm());

            stage.setScene(scene);
            stage.setResizable(false);
            stage.sizeToScene();
            stage.hide();
            stage.show();

            input = new Input(scene);
            root.getChildren().add(layer);
            statusBar = new StatusBar(root, sceneWidth, sceneHeight);

            // Create sprites
            int currentLevel = game.world().currentLevel();

            for (var decor : game.world().getGrid().values()) {
                if(decor.getBonus() instanceof Nest){

                    timer=new Timer();
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            Hornet hornet=new Hornet(game,decor.getPosition(), Functions.stringGenerator(12));
                            hornet.moveHornet(game.world().getGrid().width(),game.world().getGrid().height());
                            sprites.add(new SpriteHornet(layer, hornet));

                            Position bombPos;
                            do{
                                Random random = new Random();
                                int x = random.nextInt(game.world().getGrid().width());
                                int y = random.nextInt(game.world().getGrid().height());
                                bombPos=new Position(game.world().currentLevel(),x, y);
                            }while(!(game.world().getGrid().get(bombPos) instanceof Grass) || game.world().getGrid().get(bombPos).getBonus()!=null);

                            Insecticide bomb=new Insecticide(game,bombPos,game.world().getGrid().get(bombPos));
                            game.world().getGrid().get(bombPos).setBonus(bomb);
                            sprites.add(SpriteFactory.create(layer, bomb));
                        }
                    };

                    timer.scheduleAtFixedRate(task, 2000, 10000);
                }
                sprites.add(SpriteFactory.create(layer, decor));
                decor.setModified(true);
                var bonus = decor.getBonus();
                if (bonus != null) {
                    sprites.add(SpriteFactory.create(layer, bonus));
                    bonus.setModified(true);

                }
            }

            List<Position> positions=Functions.randomPositions(3,game.world().getGrid().width(), game.world().getGrid().height(),gardener.getPosition(),game.world().currentLevel());
            for(Position x : positions){
                Hornet hornet=new Hornet(game,x,Functions.stringGenerator(12));
                hornet.moveHornet(game.world().getGrid().width(),game.world().getGrid().height());
                sprites.add(new SpriteHornet(layer, hornet));
            }
            sprites.add(new SpriteGardener(layer, gardener));

        }

        void buildAndSetGameLoop() {
            gameLoop = new AnimationTimer() {
                public void handle(long now) {
                    checkLevel();

                    // Check keyboard actions
                    processInput();

                    // Do actions
                    update(now);
                    checkCollision();

                    // Graphic update
                    cleanupSprites();
                    render();
                    statusBar.update(game);
                }
            };
        }


        private void checkLevel() {
            if (game.isSwitchLevelRequested()) {
                cleanupSprites();
                game.clearSwitchLevel();

                for(int i=0;i<game.world().getGrid().width();i++){
                    for(int j=0;j<game.world().getGrid().width();j++){
                        Position position= new Position(game.world().currentLevel(),i,j);
                        Decor decor=game.world().getGrid().get(position);
                        if(decor instanceof OpenedDoor){
                            game.getGardener().setPosition(position);
                            break;
                        }
                    }
                }
                timer.cancel();
                stage.close();
                initialize();
            }
        }

        private void checkCollision() {
            for (Sprite item : sprites) {
                if(gardener.getPosition().equals(item.getPosition()) && item instanceof SpriteHornet && Objects.equals(((Hornet) item.getGameObject()).getName(), ((Hornet) item.getGameObject()).getName())){
                    if(gardener.getInsecticideSprays()>0) gardener.setInsecticideSprays(gardener.getInsecticideSprays()-1);
                    else gardener.hurt(20);
                    int i = sprites.indexOf(item);
                    if (i != -1) {
                        sprites.get(i).remove();
                        sprites.get(i).getGameObject().remove();
                    }
                }
                else if(game.world().getGrid().get(item.getPosition()).getBonus() instanceof Insecticide){
                    if(item instanceof SpriteHornet && Objects.equals(((Hornet) item.getGameObject()).getName(), ((Hornet) item.getGameObject()).getName())){
                        int i = sprites.indexOf(item);
                        if (i != -1) {
                            sprites.get(i).remove();
                            sprites.get(i).getGameObject().remove();
                            ((Insecticide) game.world().getGrid().get(item.getPosition()).getBonus()).cancelTimer();
                            game.world().getGrid().get(item.getPosition()).getBonus().remove();
                        }
                    }
                }

                else if(gardener.getPosition().equals(item.getPosition()) && item.getGameObject() instanceof ClosedDoor){
                    if(gardener.getNumberOfKeys()>0){
                        game.requestSwitchLevel(game.world().currentLevel()+1);
                        gardener.setKeys(gardener.getNumberOfKeys()-1);
                    }
                }

            }
        }
        private void processInput() {
            if (input.isExit()) {
                gameLoop.stop();
                Platform.exit();
                System.exit(0);
            } else if (input.isMoveDown()) {
                gardener.requestMove(Direction.DOWN);
            } else if (input.isMoveLeft()) {
                gardener.requestMove(Direction.LEFT);
            } else if (input.isMoveRight()) {
                gardener.requestMove(Direction.RIGHT);
            } else if (input.isMoveUp()) {
                gardener.requestMove(Direction.UP);
            }
            input.clear();
        }

        private void showMessage(String msg, Color color) {
            Text waitingForKey = new Text(msg);
            waitingForKey.setTextAlignment(TextAlignment.CENTER);
            waitingForKey.setFont(new Font(60));
            waitingForKey.setFill(color);
            StackPane root = new StackPane();
            root.getChildren().add(waitingForKey);
            Scene scene = new Scene(root, 400, 200, Color.WHITE);
            stage.setScene(scene);
            input = new Input(scene);
            stage.show();
            new AnimationTimer() {
                public void handle(long now) {
                    processInput();
                }
            }.start();
        }

        private void update(long now) {
            game.world().getGrid().values().forEach(decor -> decor.update(now));

            gardener.update(now);
            if (gardener.getPosition().equals(game.getHedgehogPosition())) {
                gameLoop.stop();
                showMessage("Won!", Color.GREEN);
            }else if (gardener.getEnergy() < 0) {
                gameLoop.stop();
                showMessage("Lost!", Color.RED);
            }

        }

        public void cleanupSprites() {
            sprites.forEach(sprite -> {
                if (sprite.getGameObject().isDeleted()) {
                    cleanUpSprites.add(sprite);
                }
            });
            cleanUpSprites.forEach(Sprite::remove);
            sprites.removeAll(cleanUpSprites);
            cleanUpSprites.clear();
        }

        private void render() {
            sprites.forEach(Sprite::render);
        }

        public void start() {
            gameLoop.start();
        }
    }