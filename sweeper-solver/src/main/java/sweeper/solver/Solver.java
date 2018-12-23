package sweeper.solver;

import sweeper.game.*;

import java.util.Optional;

class Solver {
    private Game state;
    private Bomb bombs;
    private Flag flags;
    private CoordAction action;

    Solver(CoordAction action){
        this.action = action;
    }

    public interface CoordAction {
        void performCoordAction(Coord coord);
    }

    void update(Game newstate){
        assert newstate != null;
        state = newstate;
        bombs = newstate.getBomb();
        flags = newstate.getFlag();
    }

    MoveType step(){
        if (state.getState() != GameState.PLAYED)
            return MoveType.NoStep;
        Coord size = Ranges.getSize();
        if (flags.getCountOfClosedBoxes() == size.getX() * size.getY()){
            openBox(Ranges.getRandomCoord());
            return MoveType.First;
        } else if (logicalMove())
            return MoveType.Logical;
        else if (probabilityMove())
            return MoveType.Probability;
        else {
            randomMove();
            return MoveType.Random;
        }
    }

    private void openBox(Coord coord){
        action.performCoordAction(coord);
        state.pressLeftButton(coord);
    }

    private void flagBox(Coord coord){
        action.performCoordAction(coord);
        state.pressRightButton(coord);
    }

    private int countCellsAround(Coord coord, Box type){
        int n = 0;
        for (Coord place : Ranges.getCoordsAround(coord)){
            if (flags.get(place) == type)
                n++;
        }
        return n;
    }

    private void cellAroundFromClosedToFlagged(Coord coord){
        Optional<Coord> around = Ranges.getCoordsAround(coord)
                .stream()
                .filter(it -> flags.get(it) == Box.CLOSED)
                .findFirst();
        around.ifPresent(this::flagBox);
    }

    private boolean logicalMove(){
        for (Coord coord : Ranges.getAllCoords()){
            Box status = flags.get(coord);
            if (status == Box.CLOSED) continue;
            int number = bombs.bombsN(coord);
            if (number == 0) continue;
            int countClosedCellsAround = countCellsAround(coord, Box.CLOSED);
            int countFlaggedCellsAround = countCellsAround(coord, Box.FLAGED);
            if (countClosedCellsAround + countFlaggedCellsAround == number && countClosedCellsAround != 0){
                cellAroundFromClosedToFlagged(coord);
                return true;
            }
            if (number - countFlaggedCellsAround == 0)
                for (Coord place : Ranges.getCoordsAround(coord))
                    if (flags.get(place) == Box.CLOSED){
                        openBox(coord);
                        return true;
                    }
        }
        return false;
    }

    private static double fullProbability(double[] field, int k){
        int segment = 1;
        while (segment != k){
            if (field[segment] == 0) return field[0];
            field[0] = (field[0] + field[segment]) / 2;
            segment++;
        }
        return field[0];
    }

    private boolean probabilityMove(){
        int bestX = 0;
        int bestY = 0;
        double bestProbability = 1;
        int flag = 0;
        int closed = 0;
        Coord size = Ranges.getSize();
        double[][][] field = new double[size.getX()][size.getY()][8];
        for (Coord coord : Ranges.getAllCoords()){
            Box status = flags.get(coord);
            if (status == Box.CLOSED){
                closed++;
                continue;
            }
            if (status == Box.FLAGED){
                flag++;
                continue;
            }
            int number = bombs.bombsN(coord);
            if (number == 0) continue;
            int flaggedCellsAroundN = countCellsAround(coord, Box.FLAGED);
            if (flaggedCellsAroundN != 0 && number != flaggedCellsAroundN){
                double probabilityMove = ((double) (number - flaggedCellsAroundN)) / flaggedCellsAroundN;
                for (Coord around : Ranges.getCoordsAround(coord)){
                    Box aroundStatus = flags.get(around);
                    if (aroundStatus == Box.CLOSED){
                        int segment = 0;
                        while (field[around.getX()][around.getY()][segment] != 0.0)
                            segment++;
                        field[around.getX()][around.getY()][segment] = probabilityMove;
                    }
                }
            }
        }
        for (Coord coord : Ranges.getAllCoords()){
            int x = coord.getX();
            int y = coord.getY();
            if (field[x][y][0] == 0) continue;
            int segment = 0;
            while (field[x][y][segment] != 0)
                segment++;
            double fullProbability = fullProbability(field[x][y],segment - 1);
            if (fullProbability < bestProbability){
                bestProbability = fullProbability;
                bestX = x;
                bestY = y;
            }
        }
        if (bestProbability > (8 - flag) / closed)
            return false;
        openBox(new Coord(bestX, bestY));
        return true;
    }

    private void randomMove(){
        Optional<Coord> move = Ranges.getAllCoords()
                .stream()
                .filter(coord -> flags.get(coord) == Box.CLOSED)
                .findFirst();
        if (move.isPresent())
            openBox(move.get());
        else
            throw new IllegalStateException("There must be at least 1 closed cell");
    }
}
