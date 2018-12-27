package sweeper.game;

public class Matrix {
    private Box[][] matrix;

    public Matrix(Box defaultBox){
        matrix = new Box[Ranges.getSize().x][Ranges.getSize().y];
        for (Coord coord : Ranges.getAllCoords())
            matrix[coord.x][coord.y] = defaultBox;
    }

    Box get(Coord coord){
        if (Ranges.inRange(coord))
            return matrix[coord.x][coord.y];
        return null;
    }

    public void set (Coord coord, Box box){
        if (Ranges.inRange(coord))
            matrix [coord.x][coord.y] = box;
    }
}
