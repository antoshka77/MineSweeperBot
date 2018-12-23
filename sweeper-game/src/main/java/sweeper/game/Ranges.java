package sweeper.game;

import java.util.ArrayList;
import java.util.Random;

public class Ranges {
    private static Coord size;
    private static ArrayList<Coord> allCoords;
    private static Random randon = new Random();

    static void setSize(Coord _size){
        size = _size;
        allCoords = new ArrayList<>();
        for (int y = 0; y < size.y; y++)
            for (int x = 0; x < size.x; x++)
                allCoords.add(new Coord(x, y));
    }

    public static Coord getSize() {
        return size;
    }

    public static ArrayList<Coord> getAllCoords(){
        return allCoords;
    }

    public static boolean inRange (Coord coord){
        return coord.x >= 0 && coord.x < size.x &&
                coord.y >= 0 && coord.y < size.y;
    }

    public static Coord getRandomCoord(){
        return new Coord(randon.nextInt(size.x),
                         randon.nextInt(size.y));
    }

    public static ArrayList<Coord> getCoordsAround(Coord coord){
        Coord around;
        ArrayList<Coord> list = new ArrayList<>();
        for (int y = coord.y - 1; y <= coord.y + 1; y ++)
            for (int x = coord.x - 1; x <= coord.x + 1; x ++)
                if (inRange(around = new Coord(x, y)))
                    if (!around.equals(coord))
                        list.add(around);
        return list;
    }
}
