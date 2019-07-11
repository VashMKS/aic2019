package alpha5;

import aic2019.Direction;

public class MovingUnit extends Unit {

    Movement movement;

    // random movement as placeholder
    void move() {
        Direction dir = tools.randomDir();
        if (uc.canMove(dir)) uc.move(dir);
    }

}
