package alpha5;

import aic2019.*;

public class Movement {

    UnitController uc;
    Data data;

    public Movement (UnitController _uc, Data _data) {
        this.uc = _uc;
        this.data = _data;
    }

    private final int INF = Integer.MAX_VALUE;

    private boolean rotateRight = true;         // if I should rotate right or left
    private Location lastObstacleFound = null;  // latest obstacle I've found in my way
    private int minDistToTarget = INF;          // minimum distance I've been to the target while going around an obstacle
    private Location prevTarget = null;         // previous target

    void moveTo(Location target){
        // No target? ==> bye!
        if (target == null) return;

        // Different target? ==> previous data does not help!
        if (prevTarget == null || !target.isEqual(prevTarget)) resetMovement();

        // If I'm at a minimum distance to the target, I'm free!
        Location myLoc = uc.getLocation();
        int d = myLoc.distanceSquared(target);
        if (d <= minDistToTarget) resetMovement();

        // update data
        prevTarget = target;
        minDistToTarget = Math.min(d, minDistToTarget);

        // If there's an obstacle I try to go around it [until I'm free] instead of going to the target directly
        Direction dir = myLoc.directionTo(target);
        if (lastObstacleFound != null) dir = myLoc.directionTo(lastObstacleFound);

        // This should not happen for a single unit, but whatever
        if (uc.canMove(dir)) resetMovement();

        // I rotate clockwise or counterclockwise (depends on 'rotateRight'). If I try to go out of the map I change the orientation
        // Note that we have to try at most 16 times since we can switch orientation in the middle of the loop. (It can be done more efficiently)
        for (int i = 0; i < 16; ++i) {
            if (uc.canMove(dir)) {
                uc.move(dir);
                return;
            }
            Location newLoc = myLoc.add(dir);
            if (uc.isOutOfMap(newLoc)) rotateRight = !rotateRight;
            // If I could not go in that direction and it was not outside of the map, then this is the latest obstacle found
            else lastObstacleFound = myLoc.add(dir);

            if (rotateRight) dir = dir.rotateRight();
            else dir = dir.rotateLeft();
        }

        if (uc.canMove(dir)) uc.move(dir);
    }

    // clear some of the previous data
    void resetMovement(){
        lastObstacleFound = null;
        minDistToTarget = INF;
    }

    boolean doMicro(){

        UnitInfo[] enemiesAround = uc.senseUnits(data.allyTeam, true);
        if (enemiesAround.length > 0) {
            MicroInfo[] micro = new MicroInfo[9];
            for (int i = 0; i < 9; ++i) {
                micro[i] = new MicroInfo(uc.getLocation().add(data.dirs[i]));
                micro[i].senseImpact();
            }
            for (UnitInfo enemy : enemiesAround) {
                for (MicroInfo m : micro) m.update(enemy);
            }

            //Siempre nos podemos quedar quietos
            int bestIndex = 8;

            for (int i = 0; i < 8; ++i) {
                if (!uc.canMove(data.dirs[i])) continue;
                if (micro[i].isBetter(micro[bestIndex])) bestIndex = i;
            }

            uc.move(data.dirs[bestIndex]);
            return true;
        }

        return false;

    }

    class MicroInfo{
        int maxDamage;
        int minDistToEnemy;
        int minEnemyHealth;

        Location loc;

        public MicroInfo (Location _loc){
            this.loc = _loc;
            int maxDamage = 0;
            int minDistToEnemy = data.INF;
            int minEnemyHealth = data.INF;
        }



        void update(UnitInfo enemy) {

            if (uc.canAttack(enemy.getLocation())) {

                if( minEnemyHealth > enemy.getHealth() ){
                    minEnemyHealth = enemy.getHealth();
                }

                int d = loc.distanceSquared(enemy.getLocation());
                if (d < minDistToEnemy) {
                    minDistToEnemy = d;
                }
                if (enemy.getType().attackRangeSquared < d &&
                    enemy.getType().minAttackRangeSquared > d) {

                    maxDamage += enemy.getType().attack;
                }
            }
        }

        void senseImpact(){
            //mira si el siguiente turno impactara una catapulta en el sitio
            if (uc.senseImpact(loc) == 1 ) maxDamage += 20;
        }

        boolean canAttack(){

            //TODO: mirar montañas
            return (uc.getType().attackRangeSquared >= minDistToEnemy &&
                    uc.getType().minAttackRangeSquared <= minDistToEnemy);
        }

        boolean isBetter(MicroInfo mic){

            int dmg = uc.getType().attack;
            int hp = uc.getInfo().getHealth();

            //Prioriza lo primero no morir este turno
            if(maxDamage < hp && mic.maxDamage >= hp) return true;
            if(maxDamage >= hp && mic.maxDamage < hp) return false;

            //Prioriza poder atacar
            if(canAttack() && !mic.canAttack()) return true;
            if(!canAttack() && mic.canAttack()) return false;

            //Prioriza las casillas en las que puede hacer killingBlow
            if(minEnemyHealth <= dmg && mic.minEnemyHealth > dmg) return true;
            if(minEnemyHealth > dmg && mic.minEnemyHealth <= dmg) return false;

            //Prioriza las casillas en las que menos daño le pueden hacer
            if(maxDamage < mic.maxDamage) return true;
            if(maxDamage > mic.maxDamage) return true;

            //prioriza acercarse al enemigo
            return minDistToEnemy <= mic.minDistToEnemy;




        }

    }




}
