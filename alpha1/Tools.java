package alpha1;

import aic2019.*;

public class Tools {

    UnitController uc;
    Data data;

    public Tools (UnitController _uc, Data _data) {
        this.uc = _uc;
        this.data = _data;
    }

    //Returns a random direction
    Direction randomDir() {
        int randomNum = (int)(Math.random()*8);
        return data.dirs[randomNum];
    }

    //Return a general direction towards the favourite direction
    Direction generalDir(Direction favDir){
        UnitController uc = data.uc;
        for (int k = 0; k < 8 && !uc.canMove(favDir); ++k) {
            if (k%2 == 0) for (int j = 0; j < k; ++j) favDir = favDir.rotateLeft();
            else for (int j = 0; j < k; ++j) favDir = favDir.rotateRight();
        }
        return favDir;
    }

    //Returns the barycenter of a number of locations
    Location barycenter(Location[] locs) {
        Location b = new Location(0,0);
        for (Location loc : locs) {
            b.x = b.x + loc.x;
            b.y = b.y + loc.y;
        }
        b.x = b.x/locs.length;
        b.y = b.y/locs.length;
        return b;
    }

    //Returns the barycenter of nearby allied units of a given type
    Location barycenter(UnitType type){
        UnitInfo[] units = data.uc.senseUnits(data.allyTeam, false );
        Location b = new Location(0,0);
        int n = 0;
        for (UnitInfo unit : units) {
            if (unit.getType() == type) {
                b.x = b.x + unit.getLocation().x;
                b.y = b.y + unit.getLocation().y;
                n++;
            }
        }
        b.x = b.x/n;
        b.y = b.y/n;
        return b;
    }

    //Returns the location with certain barycentric coordinates in the barycentric reference B
    Location barCoord(Location[] B, int[] coord) {
        int s = 0;
        Location R = new Location(0,0);
        for (int i = 0; i < B.length; i++) {
            s += coord[i];
            R.x = R.x + coord[i]*B[i].x;
            R.y = R.y + coord[i]*B[i].y;
        }
        R.x = R.x/s;
        R.y = R.y/s;
        return R;
    }

    //Turn a pair of coordinates into an integer
    int encrypt(int x, int y) {
        return x*1000 + y;
    }

    //Decrypt a location from an integer
    Location decrypt(int n) {
        return new Location(n/1000, n%1000);
    }

    //True if no friendly units are in that location
    boolean noFriendlyUnitsAt(Location loc) {
        if (data.uc.getLocation().isEqual(loc)) return false;
        UnitInfo[] nearbyAllies = data.uc.senseUnits(data.allyTeam, false);
        for (UnitInfo unit : nearbyAllies) {
            if (unit.getLocation().isEqual(loc)) return false;
        }
        return true;
    }

    //Returns the # of allies of a given type in a squared radius
    int matesAround(int radius, UnitType type) {
        UnitInfo[] unitsNear = data.uc.senseUnits(data.uc.getLocation(), radius, data.allyTeam);
        int i = 0;
        for (UnitInfo unit : unitsNear)
            if (unit.getType() == type) i++;
        return i;
    }

    //Returns the number of adjacent units
    int adjacent(UnitInfo[] units) {
        Location myLoc = data.uc.getLocation();
        int count = 0;
        for (Direction dir : data.dirs) {
            for (UnitInfo unit : units) {
                if (myLoc.add(dir).isEqual(unit.getLocation())) count += 1;
            }
        }
        return count;
    }

    //Returns the number of adjacent units of the given type
    int adjacent(UnitInfo[] units, UnitType type) {
        Location myLoc = data.uc.getLocation();
        int count = 0;
        for (Direction dir : data.dirs) {
            for (UnitInfo unit : units) {
                if (unit.getType() == type) {
                    if (myLoc.add(dir).isEqual(unit.getLocation())) count += 1;
                }
            }
        }
        return count;
    }

    boolean areAdjacent(Location loc1, Location loc2) {
        for (Direction dir : data.dirs) {
            if (loc1.add(dir).isEqual(loc2)) return true;
        }
        return false;
    }

    boolean reportedMine(Location loc){
        for(int i = 0; i < data.nMines; ++i){
            Location mineLoc = decrypt(uc.read(1001+2*i));
            if (loc == mineLoc) return false;
        }
        return true;
    }

}
