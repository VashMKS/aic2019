package huevoplanta4;

import aic2019.*;

public class Movement {

    UnitController uc;
    Data data;

    public Movement(UnitController _uc, Data _data) {
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

    boolean doMicro(Direction targetDir){

        // if(!uc.canMove()) uc.println("I'm on cooldown");

        UnitInfo[] enemiesAround = uc.senseUnits(data.allyTeam, true);
        if (uc.canMove() && (enemiesAround.length > 0 || uc.getLocation().distanceSquared(data.enemyBase) <= 72)) {
            //uc.println(uc.getType() + " report in Round: " + uc.getRound());
            MicroInfo[] micro = new MicroInfo[9];
            for (int i = 0; i < 9; ++i) {
                micro[i] = new MicroInfo(uc.getLocation().add(data.dirs[i]));
                micro[i].checkTargetDirection(targetDir);
            }
            for (int i = 0; i < Math.min(enemiesAround.length,10); ++i) {
                UnitInfo enemy = enemiesAround[i];
                for (MicroInfo m : micro) m.update(enemy);
            }

            //Siempre nos podemos quedar quietos
            int bestIndex = 8;

            for (int i = 8; i >= 0; --i) {
                if (!uc.canMove( data.dirs[i]) ) continue;
                /*
                uc.println("Info in " + data.dirs[i] + " (" + micro[i].loc.x + " " + micro[i].loc.y + "). " +
                            " MinDistance to enemy: " + micro[i].minDistToEnemy + ", maxDamage: " + micro[i].maxDamage +
                            ", minEnemyHealth: " + micro[i].minEnemyHealth + ", CanAttack: " + micro[i].canAttack() );
                */
                if (uc.getType() == UnitType.WORKER || uc.getType() == UnitType.EXPLORER){
                    if (micro[i].isBetterExplorer(micro[bestIndex])) bestIndex = i;
                }
                if (uc.getType() == UnitType.ARCHER || uc.getType() == UnitType.MAGE) {
                    if (micro[i].isBetterRanged(micro[bestIndex])) bestIndex = i;
                }
                if (uc.getType() == UnitType.SOLDIER || uc.getType() == UnitType.KNIGHT){
                    if (micro[i].isBetterMelee(micro[bestIndex])) bestIndex = i;
                }
            }

            //uc.println("The best direction is: " + data.dirs[bestIndex]);

            uc.drawLine(uc.getLocation(), uc.getLocation().add(data.dirs[bestIndex]), "FF69B4");
            uc.move(data.dirs[bestIndex]);
            return true;
        }

        return false;

    }

    class MicroInfo{
        int maxDamage = 0;
        int minDistToEnemy = 1000;
        int minEnemyHealth = 1000;
        boolean canAttack = false;
        boolean isDiagonal = false;
        boolean tooCloseToEnemyBase = false;
        boolean onRouteToTarget = false;

        Location loc;

        public MicroInfo (Location _loc){
            this.loc = _loc;

            if(loc.directionTo(uc.getLocation()) == Direction.NORTHEAST  || loc.directionTo(uc.getLocation()) == Direction.NORTHWEST ||
                    loc.directionTo(uc.getLocation()) == Direction.SOUTHEAST  || loc.directionTo(uc.getLocation()) == Direction.SOUTHWEST ){
                isDiagonal = true;
            }

            if(loc.distanceSquared(data.enemyBase) <= 50 ) tooCloseToEnemyBase = true;

            if (uc.senseImpact(loc) <= uc.getType().movementDelay ) maxDamage += 20;

        }

        void checkTargetDirection(Direction targetDir){
            if(uc.getLocation().directionTo(loc) == targetDir) onRouteToTarget = true;
        }



        void update(UnitInfo enemy) {

            int d = loc.distanceSquared(enemy.getLocation());

            if (d <= uc.getType().attackRangeSquared && d >= uc.getType().minAttackRangeSquared
                && !uc.isObstructed(loc, enemy.getLocation())) {

                canAttack = true;

                if (minEnemyHealth > enemy.getHealth()) {
                    minEnemyHealth = enemy.getHealth();
                }

            }

            //Solo guardamos las distancias a unidades que podamos ver
            if (d < minDistToEnemy && !uc.isObstructed(loc, enemy.getLocation() ) ){
                minDistToEnemy = d;
            }

            if (d <= enemy.getType().attackRangeSquared &&
                d >= enemy.getType().minAttackRangeSquared) {

                maxDamage += enemy.getType().attack;
            }
        }

        void senseImpact(){
            //mira si el siguiente turno impactara una catapulta en el sitio
            if (uc.senseImpact(loc) <= uc.getType().movementDelay ) maxDamage += 20;
        }

        /*
        boolean canAttack(){
            return (uc.getType().attackRangeSquared >= minDistToEnemy &&
                    uc.getType().minAttackRangeSquared <= minDistToEnemy);
        }
        */


        boolean isBetterMelee(MicroInfo mic) {

            float preference = 0;

            int dmg = uc.getType().attack;
            int hp = uc.getInfo().getHealth();

            //Prioriza no entrar en el rango de vision de la Base enemiga
            if(!tooCloseToEnemyBase && mic.tooCloseToEnemyBase) preference += 10;
            if(tooCloseToEnemyBase && !mic.tooCloseToEnemyBase) preference -= 10;

            //Prioriza lo primero no morir este turno
            if (maxDamage < hp && mic.maxDamage >= hp) preference += 10;
            if (maxDamage >= hp && mic.maxDamage < hp) preference -= 10;

            if (uc.canAttack() ) {

                //Prioriza poder atacar
                if (canAttack && !mic.canAttack) preference += 5;
                if (!canAttack && mic.canAttack) preference -= 5;

                //Prioriza las casillas en las que puede hacer killingBlow
                if (minEnemyHealth <= dmg && mic.minEnemyHealth > dmg) preference += 10;
                if (minEnemyHealth > dmg && mic.minEnemyHealth <= dmg) preference -= 10;
            }

            //Prioriza las casillas en las que menos daño le pueden hacer
            if(maxDamage < mic.maxDamage) preference += (mic.maxDamage - maxDamage)/2;
            if(maxDamage > mic.maxDamage) preference -= (maxDamage - mic.maxDamage)/2;

            //prioriza acercarse al enemigo
            if(minDistToEnemy < mic.minDistToEnemy) preference += 1;
            if(minDistToEnemy > mic.minDistToEnemy) preference -= 1;

            if(!isDiagonal && mic.isDiagonal) preference += 0.5;
            if(isDiagonal && !mic.isDiagonal) preference -= 0.5;

            //prioriza acercarse al objectivo
            if(onRouteToTarget && !mic.onRouteToTarget) preference += 0.25;
            if(onRouteToTarget && !mic.onRouteToTarget) preference += 0.25;

            //Si las posiciones son equivalentes mejor no cambiar
            if (preference >= 0) return true;
            return false;

        }

        boolean isBetterRanged (Movement.MicroInfo mic){

            float preference = 0;

            int dmg = uc.getType().attack;
            int hp = uc.getInfo().getHealth();

            //Prioriza no entrar en el rango de vision de la Base enemiga
            if(!tooCloseToEnemyBase && mic.tooCloseToEnemyBase) preference += 20;
            if(tooCloseToEnemyBase && !mic.tooCloseToEnemyBase) preference -= 20;

            //Prioriza lo primero no morir este turno
            if (maxDamage < hp && mic.maxDamage >= hp) preference += 10;
            if (maxDamage >= hp && mic.maxDamage < hp) preference -= 10;

            if(uc.canAttack() ) {

                //Prioriza poder atacar
                if (canAttack && !mic.canAttack) preference += 3;
                if (!canAttack && mic.canAttack) preference -= 3;

                //Prioriza las casillas en las que puede hacer killingBlow
                if (minEnemyHealth <= dmg && mic.minEnemyHealth > dmg) preference += 7;
                if (minEnemyHealth > dmg && mic.minEnemyHealth <= dmg) preference -= 7;

            }

            //Prioriza las casillas en las que menos daño le pueden hacer
            if(maxDamage < mic.maxDamage) preference += (mic.maxDamage - maxDamage);
            if(maxDamage > mic.maxDamage) preference -= (maxDamage - mic.maxDamage);

            //prioriza alejarse del enemigo
            //if(minDistToEnemy > mic.minDistToEnemy) preference += 1;
            //if(minDistToEnemy < mic.minDistToEnemy) preference -= 1;

            if(!isDiagonal && mic.isDiagonal) preference += 0.5;
            if(isDiagonal && !mic.isDiagonal) preference -= 0.5;

            //prioriza acercarse al objectivo
            if(onRouteToTarget && !mic.onRouteToTarget) preference += 0.25;
            if(onRouteToTarget && !mic.onRouteToTarget) preference += 0.25;

            //Si las posiciones son equivalentes mejor no cambiar
            if (preference >= 0) return true;
            return false;

        }

        boolean isBetterExplorer (Movement.MicroInfo mic){

            float preference = 0;

            int dmg = uc.getType().attack;

            //Prioriza no entrar en el rango de vision de la Base enemiga
            if(!tooCloseToEnemyBase && mic.tooCloseToEnemyBase) preference += 100;
            if(tooCloseToEnemyBase && !mic.tooCloseToEnemyBase) preference -= 100;

            //Prioriza las casillas en las que menos daño le pueden hacer
            if(maxDamage < mic.maxDamage) preference += (mic.maxDamage - maxDamage);
            if(maxDamage > mic.maxDamage) preference -= (maxDamage - mic.maxDamage);

            //prioriza alejarse del enemigo
            if(minDistToEnemy > mic.minDistToEnemy) preference += 1;
            if(minDistToEnemy < mic.minDistToEnemy) preference -= 1;

            if(uc.canAttack() ) {
                //Prioriza las casillas en las que puede hacer killingBlow
                if (minEnemyHealth <= dmg && mic.minEnemyHealth > dmg) preference += 300;
                if (minEnemyHealth > dmg && mic.minEnemyHealth <= dmg) preference -= 300;

            }

            //prioriza no moverse en diagonal (genera mas cooldown)
            if(!isDiagonal && mic.isDiagonal) preference += 0.5;
            if(isDiagonal && !mic.isDiagonal) preference -= 0.5;

            //prioriza acercarse al objectivo
            if(onRouteToTarget && !mic.onRouteToTarget) preference += 0.25;
            if(onRouteToTarget && !mic.onRouteToTarget) preference += 0.25;

            //Si las posiciones son equivalentes mejor no cambiar
            if (preference >= 0) return true;
            return false;

        }

    }




}
