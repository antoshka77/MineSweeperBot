package sweeper.solver;

import sweeper.game.Coord;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;

public class MinesGroup implements Iterable<Coord> {

    int minesAround;
    Set<Coord> group;

    MinesGroup(int minesAround) {
        this.minesAround = minesAround;
        group = new HashSet<>();
    }

    void minusAssign(MinesGroup other) {
        minesAround -= other.minesAround;
        group.removeAll(other.group);
    }

    boolean contains(MinesGroup other) {
        return group.containsAll(other.group);
    }

    @Override
    public String toString() {
        return "{" + minesAround + ":" + group.toString() + "}";
    }

    @Override
    public boolean equals(Object other){
        if (other instanceof MinesGroup) {
            final MinesGroup otherGroup = (MinesGroup) other;
            boolean eg = group.equals(otherGroup.group);
            if (eg && minesAround != otherGroup.minesAround) {
                System.out.println("STRANGE BEHAVIOUR!!!!");
                minesAround = minesAround > otherGroup.minesAround ? minesAround : otherGroup.minesAround;
            }
            return minesAround == otherGroup.minesAround && eg;
        }
        return false;
    }

    @Override
    public int hashCode(){
        return ~minesAround ^ group.hashCode();
    }

    @Override
    public Iterator<Coord> iterator() {
        return group.iterator();
    }

    @Override
    public void forEach(Consumer<? super Coord> action) {
        group.forEach(action);
    }

    @Override
    public Spliterator<Coord> spliterator() {
        return group.spliterator();
    }
}
