package mazegenerator;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author mallory
 */
public class Maze {

    private int width = 1;
    private int height = 1;
    private int startX = 0;
    private int startY = 0;
    private MazeTile[][] maze;
    private Coordinate lastCoordinate = null;
    private double pathAverage;
    private int tilesCreated = 0;
    private ArrayList<Coordinate> openPaths = new ArrayList<>();
    private boolean mazeDone = false;
    
    public Maze(int size){
        width = size;
        height = size;
        maze = new MazeTile[height][width];
    }
    
    public Maze(int width, int height){
        this.width = width;
        this.height = height;
        maze = new MazeTile[height][width];
    }
    
    public Maze(int size, int startX, int startY){
        width = size;
        height = size;
        this.startX = startX;
        this.startY = startY;
        maze = new MazeTile[height][width];
    }

    public Maze(int width, int height,int startX, int startY){
        this.width = width;
        this.height = height;
        this.startX = startX;
        this.startY = startY;
        maze = new MazeTile[height][width];
    }    
    
    public void generate() throws Exception{
        while(!mazeDone){
            if(tilesCreated == 0){
                this.generateFirstTile();
            }else{
                this.generateTile();
            }
        }
        this.nullifyMissing();
    }
    
    public void regenerate() throws Exception{
        for(int y = 0; y < maze.length; y++){
            for(int x = 0; x < maze[0].length;x++){
                maze[y][x] = null;
            }
        }
        lastCoordinate = null;
        pathAverage = 0;
        tilesCreated = 0;
        openPaths.removeAll(openPaths);
        mazeDone = false;
        generate();
    }
    
    private MazeTile generateFirstTile() throws Exception{
        MazeTile tile = null;
        if(!this.isInBounds(new Coordinate(startX, startY))) {
            throw new Exception("Start Coordinates Out of Bound! OutOfBoundException");
        }
        if(this.isAtBounds(new Coordinate(startX, startY))){
            tile = this.generateBoundedTile(new Coordinate(startX, startY), true);
            maze[startY][startX] = tile;
            int pathSum = (tile.getDown() ? 1 : 0) + (tile.getUp()? 1 : 0);
            pathSum += (tile.getLeft()? 1 : 0) + (tile.getRight()? 1 : 0);
            this.tilesCreated++;
            this.pathAverage = (this.pathAverage * (tilesCreated-1) + pathSum)/ tilesCreated;
        }else{
            int pathSumDecider = this.getRandomInt(4)+1;
            if(pathSumDecider == 4){
                int randomTile = this.getRandomInt(MazeTile.fourWayPath.length/2)+ MazeTile.fourWayPath.length/2;
                tile = MazeTile.fourWayPath[randomTile];
                maze[startY][startX] = tile;
            }else if(pathSumDecider == 3){
                int randomTile = this.getRandomInt(MazeTile.threeWayPath.length/2)+ MazeTile.threeWayPath.length/2;
                tile = MazeTile.threeWayPath[randomTile];
                maze[startY][startX] = tile;
            }else if(pathSumDecider == 2){
                int randomTile = this.getRandomInt(MazeTile.twoWayPath.length/2)+ MazeTile.twoWayPath.length/2;
                tile = MazeTile.twoWayPath[randomTile];
                maze[startY][startX] = tile;
            }else{
                int randomTile = this.getRandomInt(MazeTile.oneWayPath.length/2)+ MazeTile.oneWayPath.length/2;
                tile = MazeTile.oneWayPath[randomTile];
                maze[startY][startX] = tile;
            }
            this.tilesCreated++;
            this.pathAverage = ((this.pathAverage * (tilesCreated-1)) + pathSumDecider)/ tilesCreated;
        }
        openPaths.add(new Coordinate(startX,startY));
        lastCoordinate = new Coordinate(startX, startY);
        return tile;
    }
    
    private MazeTile generateTile(){
        MazeTile tile = null;
        int randomInt = this.getRandomInt(openPaths.size()-1);
        if(randomInt == -1){
            mazeDone = true;
            maze[lastCoordinate.getY()][lastCoordinate.getX()].addStar();
            return null;
        }
        Coordinate oldCoord = openPaths.get(randomInt);
        MazeTile oldTile = maze[oldCoord.getY()][oldCoord.getX()];
        Coordinate activeTile = null;
        ArrayList<Direction> openDirections = new ArrayList<>();
        if(oldTile.getLeft() &&  !this.doesTileExist(new Coordinate(oldCoord.getX()-1, oldCoord.getY()))) openDirections.add(Direction.left);
        if(oldTile.getRight()&&  !this.doesTileExist(new Coordinate(oldCoord.getX()+1, oldCoord.getY()))) openDirections.add(Direction.right);
        if(oldTile.getUp()   &&  !this.doesTileExist(new Coordinate(oldCoord.getX(), oldCoord.getY()-1))) openDirections.add(Direction.up);
        if(oldTile.getDown() &&  !this.doesTileExist(new Coordinate(oldCoord.getX(), oldCoord.getY()+1))) openDirections.add(Direction.down);
        if(openDirections.isEmpty()){
            openPaths.remove(randomInt);
            return null;
        }
        int randomDirectionInt = this.getRandomInt(openDirections.size());
        Direction nextDirection = openDirections.get(randomDirectionInt);
        if(nextDirection == Direction.left)  activeTile = new Coordinate(oldCoord.getX()-1,oldCoord.getY());
        if(nextDirection == Direction.right) activeTile = new Coordinate(oldCoord.getX()+1,oldCoord.getY());
        if(nextDirection == Direction.down)  activeTile = new Coordinate(oldCoord.getX(),oldCoord.getY()+1);
        if(nextDirection == Direction.up)    activeTile = new Coordinate(oldCoord.getX(),oldCoord.getY()-1);
        ArrayList<Direction> validDirections = this.getOptionalDirections(activeTile);
        ArrayList<Direction> requiredDirections = this.getRequiredDirections(activeTile);
        ArrayList<Direction> activePath = new ArrayList<>();
        for (Direction requiredDirection : requiredDirections) {
            activePath.add(requiredDirection);
        }
        int pathSize;
        if(this.pathAverage < 2){
            //more paths must be generated
            //1 way path - 10% , 2 way path - 20%
            //3 way path - 30% , 4 way path - 40%
            pathSize = this.getRandomIntFromRatios(10, 20, 30, 40) + 1;
        }else if(this.pathAverage > 2){
            if(this.pathAverage < 2.5){
                //there are a decent amount of paths
                //1 way path - 20% , 2 way path - 30%
                //3 way path - 30% , 4 way path - 20%
                pathSize = this.getRandomIntFromRatios(20, 30, 30, 20) + 1;
            }else{
                //there are too many paths
                //1 way path - 40% , 2 way path - 30%
                //3 way path - 20% , 4 way path - 10%
                pathSize = this.getRandomIntFromRatios(40, 30, 20, 10) + 1;
            }
        }else{
            //paths can be created or destroyed
            //1 way path - 25% , 2 way path - 25%
            //3 way path - 25% , 4 way path - 25%
            pathSize = this.getRandomIntFromRatios(25, 25, 25, 25);
        }
        if(requiredDirections.size() < pathSize){
            int neededPaths = pathSize - requiredDirections.size();
            for(; neededPaths > 0; neededPaths--){
                int left = validDirections.contains(Direction.left) ? 1 : 0;
                int right = validDirections.contains(Direction.right) ? 1 : 0;
                int down = validDirections.contains(Direction.down) ? 1 : 0;
                int up = validDirections.contains(Direction.up) ? 1 : 0;
                Direction randomDirection = this.getRandomDirectionFromRatios(left, right, down, up);
                if(left != 0 || right != 0 || down != 0 || up != 0){
                    activePath.add(randomDirection);
                    validDirections.remove(randomDirection);
                }
            }
        }             
        this.tilesCreated++;
        tile = new MazeTile(activePath);
        pathSize = activePath.size();
        this.pathAverage = ((this.pathAverage * (tilesCreated-1)) + pathSize)/ tilesCreated;
        maze[activeTile.getY()][activeTile.getX()] = tile;
        openPaths.add(activeTile);
        lastCoordinate = activeTile;
        return tile;
    }
    
    private void nullifyMissing(){
        for(int y = 0; y < maze.length; y++){
            for(int x = 0; x < maze[0].length;x++){
                if(!this.doesTileExist(new Coordinate(x,y))){
                    maze[y][x] = new MazeTile((byte)0);
                }
            }
        }
    }
    
    private ArrayList<Direction> getOptionalDirections(Coordinate coord){
        ArrayList<Direction> validDirections = new ArrayList<>();
        int x = coord.getX();
        int y = coord.getY();
        if(this.isInBounds(new Coordinate(x - 1, y))){
            if(!this.doesTileExist(new Coordinate(x - 1, y))){
                validDirections.add(Direction.left);
            }
        }
        if(this.isInBounds(new Coordinate(x, y + 1))){
            if(!this.doesTileExist(new Coordinate(x, y + 1))){
                validDirections.add(Direction.down);
            }
        }
        if(this.isInBounds(new Coordinate(x + 1, y))){
            if(!this.doesTileExist(new Coordinate(x + 1, y))){
                validDirections.add(Direction.right);
            }
        }
        if(this.isInBounds(new Coordinate(x, y - 1))){
            if(!this.doesTileExist(new Coordinate(x, y - 1))){
                validDirections.add(Direction.up);
            }
        }
        return validDirections;
    }
    
    private ArrayList<Direction> getRequiredDirections(Coordinate coord){
        ArrayList<Direction> validDirections = new ArrayList<>();
        int x = coord.getX();
        int y = coord.getY();
        if(this.isInBounds(new Coordinate(x - 1, y))){
            if(this.doesTileExist(new Coordinate(x - 1, y)) && maze[y][x-1].getRight()){
                validDirections.add(Direction.left);
            }
        }
        if(this.isInBounds(new Coordinate(x, y + 1))){
            if(this.doesTileExist(new Coordinate(x, y + 1)) && maze[y + 1][x].getUp()){
                validDirections.add(Direction.down);
            }
        }
        if(this.isInBounds(new Coordinate(x + 1, y))){
            if(this.doesTileExist(new Coordinate(x + 1, y)) && maze[y][x + 1].getLeft()){
                validDirections.add(Direction.right);
            }
        }
        if(this.isInBounds(new Coordinate(x, y - 1))){
            if(this.doesTileExist(new Coordinate(x, y - 1)) && maze[y - 1][x].getDown()){
                validDirections.add(Direction.up);
            }
        }
        return validDirections;
    }
    
    @Override
    public String toString(){
        String temp = "";
        for (MazeTile[] maze1 : maze) {
            for (int i = 0; i < 3; i++) {
                for (MazeTile maze11 : maze1) {
                    if (i == 0) {
                        temp += (maze11.getTopRow());
                    } else if (i == 1) {
                        temp += (maze11.getMiddleRow());
                    } else if (i == 2) {
                        temp += (maze11.getBottomRow());
                    }
                } 
                temp += "\n";
            }
        }
        return temp;
    }
    
    private boolean isAtBounds(Coordinate coord){
        return coord.getY() == 0 || coord.getY() == maze.length-1 || coord.getX() == 0 || coord.getX() == maze[0].length-1;
    }
    
    private MazeTile generateBoundedTile(Coordinate coord, boolean star){
        ArrayList<Direction> validDirections = new ArrayList<>();
        validDirections.add(Direction.left);
        validDirections.add(Direction.right);
        validDirections.add(Direction.down);
        validDirections.add(Direction.up);
        if(coord.getY() == 0)                validDirections.remove(Direction.up);
        if(coord.getY() == maze.length-1)    validDirections.remove(Direction.down);
        if(coord.getX() == 0)                validDirections.remove(Direction.left);
        if(coord.getX() == maze[0].length-1) validDirections.remove(Direction.right);
        return new MazeTile(validDirections, star);
    }
    
    private int getRandomInt(int max){
        return (int) Math.floor(Math.random()*max);
    }
    
    private boolean isInBounds(Coordinate coord){
        boolean inYBounds = coord.getY() >= 0 && coord.getY() <= maze.length-1;
        boolean inXBounds = coord.getX() >= 0 && coord.getX() <= maze[0].length-1;
        return inYBounds && inXBounds;
    }
    
    private boolean doesTileExist(Coordinate coord){
        boolean inBounds = this.isInBounds(coord);
        if(!inBounds) return false;
        boolean notNull = maze[coord.getY()][coord.getX()] != null;
        return notNull;
    }
    
    public double getPathAverage(){
        return this.pathAverage;
    }
    
    public int getTilesCreated(){
        return this.tilesCreated;
    }
    
    private Direction getRandomDirectionFromRatios(int left, int right, int down, int up){
        int[] pathArray = new int[]{left, right, down, up};
        Arrays.sort(pathArray);
        int leftIndex = -1;
        int rightIndex = -1;
        int downIndex = -1;
        int upIndex = -1;
        for(int i = 0;leftIndex < 0 || rightIndex < 0 || downIndex < 0 || upIndex < 0;i++){
            if(pathArray[i] == left && leftIndex == -1){
                leftIndex = i;
            }else if(pathArray[i] == right && rightIndex == -1){
                rightIndex = i;
            }else if(pathArray[i] == down && downIndex == -1){
                downIndex = i;
            }else if(pathArray[i] == up && upIndex == -1){
                upIndex = i;
            }
        }
        int randomDirection = this.getRandomIntFromRatios(left, right, down, up);
        if(randomDirection == leftIndex){
            return Direction.left;
        }else if(randomDirection == rightIndex){
            return Direction.right;
        }else if(randomDirection == downIndex){
            return Direction.down;
        }else{
            return Direction.up;
        }
    }
    
    private int getRandomIntFromRatios(int left, int right, int down, int up){
        int sum = left + right + down + up;
        int[] pathArray = new int[]{left, right, down, up};
        Arrays.sort(pathArray);
        int leftIndex = -1;
        int rightIndex = -1;
        int downIndex = -1;
        int upIndex = -1;
        for(int i = 0;leftIndex < 0 || rightIndex < 0 || downIndex < 0 || upIndex < 0; i++){
            if(pathArray[i] == left && leftIndex == -1){
                leftIndex = i;
            }else if(pathArray[i] == right && rightIndex == -1){
                rightIndex = i;
            }else if(pathArray[i] == down && downIndex == -1){
                downIndex = i;
            }else if(pathArray[i] == up && upIndex == -1){
                upIndex = i;
            }
        }
        int tempRandomInt = this.getRandomInt(sum)+1;
        int lastBreakPoint = 0;
        int randomizedInteger = 0;
        for(int i = 0; i < pathArray.length; i++){
            int breakPoint;
            if(i == 0){
                breakPoint = lastBreakPoint + pathArray[i]*(pathArray.length-i);
            }else{
                breakPoint = lastBreakPoint + (pathArray[i]-pathArray[i-1])*(pathArray.length-i);
            }
            if(tempRandomInt <= breakPoint){
                randomizedInteger = ((tempRandomInt - lastBreakPoint + (pathArray.length - i - 1)) % (pathArray.length - i))  + i;
                return randomizedInteger;
            }
            lastBreakPoint = breakPoint;
        }
        return randomizedInteger;
    }
}
