package sweeper.game;

public class Game {
    private Bomb bomb;
    private Flag flag;
    private GameState state;

    Game(int cols, int rows, int bombs){
        Ranges.setSize(new Coord(cols, rows));
        bomb = new Bomb(bombs);
        flag = new Flag();
    }

    public Game(Bomb bombs) {
        bomb = bombs;
        flag = new Flag();
    }

    void start(){
       bomb.start();
       flag.start();
       state = GameState.PLAYED;
    }

    public void testStart() {
        flag.start();
        state = GameState.PLAYED;
    }

    Box getBox(Coord coord) {
        if (flag.get(coord) == Box.OPENED)
            return bomb.get(coord);
        else
            return flag.get(coord);
    }

    public Bomb getBomb() {
        return bomb;
    }

    public Flag getFlag() {
        return flag;
    }

    public void pressLeftButton(Coord coord) {
        if (gameOver()) return;
        openBox(coord);
        checkWinner();
    }

     private void checkWinner(){
        if (state == GameState.PLAYED)
            if (flag.getCountOfClosedBoxes() == bomb.getTotalBombs())
                state = GameState.WINNER;
    }

    private void openBox(Coord coord){
        switch (flag.get(coord)){
            case OPENED: setOpenedToClosedBoxesAroundNumber(coord); return;
            case FLAGED: return;
            case CLOSED:
                switch (bomb.get(coord)){
                    case ZERO: openBoxesAround(coord); return;
                    case BOMB: openBombs(coord); return;
                    default: flag.setOpenedToBox(coord);
                }
        }
    }

    private void setOpenedToClosedBoxesAroundNumber(Coord coord){
        if (bomb.get(coord) != Box.BOMB)
            if (flag.getCountOfFlagedBoxesAround(coord) == bomb.get(coord).getNumber())
                for (Coord around : Ranges.getCoordsAround(coord))
                    if (flag.get(around) == Box.CLOSED)
                        openBox(around);
    }


    private void openBombs(Coord bombed){
       state = GameState.BOMBED;
       flag.setBombedToBox(bombed);
       for (Coord coord : Ranges.getAllCoords())
           if (bomb.get(coord) == Box.BOMB)
               flag.setOpenedToClosedBombBox(coord);
           else
               flag.setNobombToSafeBox(coord);
    }

    private void openBoxesAround(Coord coord) {
        flag.setOpenedToBox(coord);
        for (Coord around : Ranges.getCoordsAround(coord))
            openBox(around);
    }

    public void pressRightButton(Coord coord) {
        if (gameOver()) return;
        flag.toggleFlagedToBox(coord);
    }

    private boolean gameOver() {
        switch (state) {
            case PLAYED:
                return false;
            case BOMBED:
                Main.showRestartDialog("Вы проиграли");
                break;
            case WINNER:
                Main.showRestartDialog("Вы выиграли!");
                break;
        }
        return true;
    }

    public GameState getState() {
        return state;
    }
}
