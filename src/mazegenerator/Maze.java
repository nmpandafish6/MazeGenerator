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
    private int currentX = 0;
    private int currentY = 0;
    private MazeTile lastTile = null;
    private MazeTile[][] maze;
    private double pathAverage;
    private int tilesCreated = 0;
    private ArrayList<Coordinate> openPaths;
    private boolean mazeDone = false;
    
    public Maze(int size){
        width = size;
        height = size;
        maze = new MazeTile[height][width];
        openPaths = new ArrayList<>();
    }
    
    public Maze(int width, int height){
        this.width = width;
        this.height = height;
        maze = new MazeTile[height][width];
        openPaths = new ArrayList<>();
    }
    
    public Maze(int size, int startX, int startY){
        width = size;
        height = size;
        this.startX = startX;
        this.startY = startY;
        maze = new MazeTile[height][width];
        openPaths = new ArrayList<>();
    }

    public Maze(int width, int height,int startX, int startY){
        this.width = width;
        this.height = height;
        this.startX = startX;
        this.startY = startY;
        maze = new MazeTile[height][width];
        openPaths = new ArrayList<>();
    }    
    
    public void generate() throws Exception{
        boolean firstMove = true;
        boolean finalTileGenerated = false;
        
        while(!mazeDone){
            if(firstMove){
                lastTile = this.generateFirstTile();
                firstMove = false;
            }else{
                lastTile = this.generateTile();
                finalTileGenerated = tilesCreated == 5;
            }
            
        }
        this.nullifyMissing();
    }
    
    private MazeTile generateFirstTile() throws Exception{
            MazeTile tile = null;
            if(!this.isInBounds(new Coordinate(startX, startY))){
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
            return tile;
    }
    
    private MazeTile generateTile(){
        MazeTile tile = null;
        int randomInt = this.getRandomInt(openPaths.size()-1);
        if(randomInt == -1){
            mazeDone = true;
            return null;
        }
        
        Direction nextDirection = this.chooseNextDirection(openPaths.get(randomInt));
        Coordinate oldTile = openPaths.get(randomInt);
        Coordinate activeTile = null;
        ArrayList<Direction> openDirections = new ArrayList<>();
        openDirections.add(Direction.left);
        openDirections.add(Direction.right);
        openDirections.add(Direction.up);
        openDirections.add(Direction.down);
        int lastDirectionalSize = 0;
        while(activeTile == null){
            if(nextDirection == Direction.down && openDirections.contains(Direction.down)){
                activeTile = new Coordinate(oldTile.getX(), oldTile.getY() + 1);
                openDirections.remove(nextDirection);
            }else if(nextDirection == Direction.left && openDirections.contains(Direction.left)){
                activeTile = new Coordinate(oldTile.getX() - 1 , oldTile.getY());
                openDirections.remove(nextDirection);
            }else if(nextDirection == Direction.right && openDirections.contains(Direction.right)){
                activeTile = new Coordinate(oldTile.getX() + 1, oldTile.getY());
                openDirections.remove(nextDirection);
            }else if(nextDirection == Direction.up && openDirections.contains(Direction.up)){
                activeTile = new Coordinate(oldTile.getX(), oldTile.getY() - 1);
                openDirections.remove(nextDirection);
            }
            if(activeTile != null && this.doesTileExist(activeTile)){
                activeTile = null;
            }
            if(openDirections.isEmpty()){
                System.out.println("{HEY");
                openPaths.remove(randomInt);
                return null;
            }
            lastDirectionalSize = openDirections.size();
            int randomDirectionInt = this.getRandomInt(openDirections.size());
            System.out.println(openDirections.size() + "[" + randomDirectionInt);
            nextDirection = openDirections.get(randomDirectionInt);
        }
        if(!this.isInBounds(activeTile)){
            openPaths.remove(randomInt);
            return null;
        }
        ArrayList<Direction> validDirections = this.getOptionalDirections(activeTile);
        ArrayList<Direction> requiredDirections = this.getRequiredDirections(activeTile);
        ArrayList<Direction> activePath = new ArrayList<>();
        for (int i = 0; i < requiredDirections.size(); i++) {
            Direction requiredDirection = requiredDirections.get(i);
            activePath.add(requiredDirection);
        }
        int pathSumDecider = 0;
        if(this.pathAverage < 2){
            //more paths must be generated
            //1 way path - 10%
            //2 way path - 20%
            //3 way path - 30%
            //4 way path - 40%
            int pathSize = this.getRandomIntFromRatios(10, 20, 30, 40) + 1;
            if(requiredDirections.size() >= pathSize){
                tile = new MazeTile(requiredDirections);
                pathSumDecider = requiredDirections.size();
            }else{
                int neededPaths = pathSize - requiredDirections.size();
                for(; neededPaths > 0; neededPaths--){
                    int left = 0;
                    int right = 0;
                    int down = 0;
                    int up = 0;
                    for(int i = 0; i < validDirections.size();i++){
                        if(validDirections.get(i) == Direction.left){
                            left = 1;
                        }else if(validDirections.get(i) == Direction.right){
                            right = 1;
                        }else if(validDirections.get(i) == Direction.down){
                            down = 1;
                        }else if(validDirections.get(i) == Direction.up){
                            up = 1;
                        }
                    }
                    Direction randomDirection = this.getRandomDirectionFromRatios(left, right, down, up);
                    if(left != 0 || right != 0 || down != 0 || up != 0){
                        activePath.add(randomDirection);
                        validDirections.remove(randomDirection);
                    }
                }
                pathSumDecider = pathSize;
                tile = new MazeTile(activePath);
            }
        }else if(this.pathAverage > 2){
            if(this.pathAverage < 2.5){
                //there are a decent amount of paths
                //1 way path - 20%
                //2 way path - 30%
                //3 way path - 30%
                //4 way path - 20%
                int pathSize = this.getRandomIntFromRatios(20, 30, 30, 20) + 1;
                if(requiredDirections.size() >= pathSize){
                    tile = new MazeTile(requiredDirections);
                    pathSumDecider = requiredDirections.size();
                }else{
                    int neededPaths = pathSize - requiredDirections.size();
                    for(; neededPaths > 0; neededPaths--){
                        int left = 0;
                        int right = 0;
                        int down = 0;
                        int up = 0;
                        for(int i = 0; i < validDirections.size();i++){
                            if(validDirections.get(i) == Direction.left){
                                left = 1;
                            }else if(validDirections.get(i) == Direction.right){
                                right = 1;
                            }else if(validDirections.get(i) == Direction.down){
                                down = 1;
                            }else if(validDirections.get(i) == Direction.up){
                                up = 1;
                            }
                        }
                        Direction randomDirection = this.getRandomDirectionFromRatios(left, right, down, up);
                        if(left != 0 || right != 0 || down != 0 || up != 0){
                            activePath.add(randomDirection);
                            validDirections.remove(randomDirection);
                        }
                    }
                    pathSumDecider = pathSize;
                    tile = new MazeTile(activePath);
                }
            }else{
                //there are too many paths
                //1 way path - 40%
                //2 way path - 30%
                //3 way path - 20%
                //4 way path - 10%
                int pathSize = this.getRandomIntFromRatios(40, 30, 20, 10) + 1;
                if(requiredDirections.size() >= pathSize){
                    tile = new MazeTile(requiredDirections);
                    pathSumDecider = requiredDirections.size();
                }else{
                    int neededPaths = pathSize - requiredDirections.size();
                    for(; neededPaths > 0; neededPaths--){
                        int left = 0;
                        int right = 0;
                        int down = 0;
                        int up = 0;
                        for(int i = 0; i < validDirections.size();i++){
                            if(validDirections.get(i) == Direction.left){
                                left = 1;
                            }else if(validDirections.get(i) == Direction.right){
                                right = 1;
                            }else if(validDirections.get(i) == Direction.down){
                                down = 1;
                            }else if(validDirections.get(i) == Direction.up){
                                up = 1;
                            }
                        }
                        Direction randomDirection = this.getRandomDirectionFromRatios(left, right, down, up);
                        if(left != 0 || right != 0 || down != 0 || up != 0){
                            activePath.add(randomDirection);
                            validDirections.remove(randomDirection);
                        }
                    }
                    pathSumDecider = pathSize;
                    tile = new MazeTile(activePath);
                }
            }
        }else{
            //paths can be created or destroyed
            //1 way path - 25%
            //2 way path - 25%
            //3 way path - 25%
            //4 way path - 25%
            int pathSize = this.getRandomIntFromRatios(25, 25, 25, 25);
            if(requiredDirections.size() >= pathSize){
                tile = new MazeTile(requiredDirections);
                pathSumDecider = requiredDirections.size();
            }else{
                int neededPaths = pathSize - requiredDirections.size();
                for(; neededPaths > 0; neededPaths--){
                    int left = 0;
                    int right = 0;
                    int down = 0;
                    int up = 0;
                    for(int i = 0; i < validDirections.size();i++){
                        if(validDirections.get(i) == Direction.left){
                            left = 1;
                        }else if(validDirections.get(i) == Direction.right){
                            right = 1;
                        }else if(validDirections.get(i) == Direction.down){
                            down = 1;
                        }else if(validDirections.get(i) == Direction.up){
                            up = 1;
                        }
                    }
                    Direction randomDirection = this.getRandomDirectionFromRatios(left, right, down, up);
                    if(left != 0 || right != 0 || down != 0 || up != 0){
                        activePath.add(randomDirection);
                        validDirections.remove(randomDirection);
                    }
                }
                pathSumDecider = pathSize;
                tile = new MazeTile(activePath);
            }
        }
        this.tilesCreated++;
        tile = new MazeTile(activePath);
        pathSumDecider = activePath.size();
        this.pathAverage = ((this.pathAverage * (tilesCreated-1)) + pathSumDecider)/ tilesCreated;
        maze[activeTile.getY()][activeTile.getX()] = tile;
        openPaths.add(activeTile);
        return tile;
    }
    
    public void nullifyMissing(){
        for(int y = 0; y < maze.length; y++){
            for(int x = 0; x < maze[0].length;x++){
                if(this.doesTileExist(new Coordinate(x,y))){
                    
                }else{
                    maze[y][x] = new MazeTile((byte)0);
                }
            }
        }
    }
    
    private Direction chooseNextDirection(Coordinate coord){
        ArrayList<Direction> validDirections = new ArrayList<>();
        MazeTile openTile = maze[coord.getY()][coord.getX()];
        if(openTile.getLeft()){
            validDirections.add(Direction.left);
        }
        if(openTile.getDown()){
            validDirections.add(Direction.down);
        }
        if(openTile.getRight()){
            validDirections.add(Direction.right);
        }
        if(openTile.getUp()){
            validDirections.add(Direction.up);
        }
        int randomDirection = this.getRandomInt(validDirections.size()-1);
        if(randomDirection == -1){
            mazeDone = true;
            return null;
        }
        return validDirections.get(randomDirection);
    }
    
    private ArrayList<Direction> getOptionalDirections(Coordinate coord){
        ArrayList<Direction> validDirections = new ArrayList<>();
        int x = coord.getX();
        int y = coord.getY();
        if(this.isInBounds(new Coordinate(x - 1, y))){
            if(!this.doesTileExist(new Coordinate(x - 1, y))){
                validDirections.add(Direction.left);
            }else if(maze[y][x-1].getRight()){
//                validDirections.add(Direction.left);
            }
        }
        if(this.isInBounds(new Coordinate(x, y + 1))){
            if(!this.doesTileExist(new Coordinate(x, y + 1))){
                validDirections.add(Direction.down);
            }else if(maze[y + 1][x].getUp()){
//                validDirections.add(Direction.down);
            }
        }
        if(this.isInBounds(new Coordinate(x + 1, y))){
            if(!this.doesTileExist(new Coordinate(x + 1, y))){
                validDirections.add(Direction.right);
            }else if(maze[y][x + 1].getLeft()){
//                validDirections.add(Direction.right);
            }
        }
        if(this.isInBounds(new Coordinate(x, y - 1))){
            if(!this.doesTileExist(new Coordinate(x, y - 1))){
                validDirections.add(Direction.up);
            }else if(maze[y - 1][x].getDown()){
//                validDirections.add(Direction.up);
            }
        }
        return validDirections;
    }
    
    private ArrayList<Direction> getRequiredDirections(Coordinate coord){
        ArrayList<Direction> validDirections = new ArrayList<>();
        int x = coord.getX();
        int y = coord.getY();
        if(this.isInBounds(new Coordinate(x - 1, y))){
            if(!this.doesTileExist(new Coordinate(x - 1, y))){
//                validDirections.add(Direction.left);
            }else if(maze[y][x-1].getRight()){
                validDirections.add(Direction.left);
            }
        }
        if(this.isInBounds(new Coordinate(x, y + 1))){
            if(!this.doesTileExist(new Coordinate(x, y + 1))){
//                validDirections.add(Direction.down);
            }else if(maze[y + 1][x].getUp()){
                validDirections.add(Direction.down);
            }
        }
        if(this.isInBounds(new Coordinate(x + 1, y))){
            if(!this.doesTileExist(new Coordinate(x + 1, y))){
//                validDirections.add(Direction.right);
            }else if(maze[y][x + 1].getLeft()){
                validDirections.add(Direction.right);
            }
        }
        if(this.isInBounds(new Coordinate(x, y - 1))){
            if(!this.doesTileExist(new Coordinate(x, y - 1))){
//                validDirections.add(Direction.up);
            }else if(maze[y - 1][x].getDown()){
                validDirections.add(Direction.up);
            }
        }
        return validDirections;
    }
    
    public String toString(){
        String temp = "";
        for(int y = 0;y < maze.length;y++){
            for(int i = 0;i < 3; i++){
                for(int x = 0;x < maze[y].length; x++){
                    if(i == 0){
                        //System.out.print(maze[y][x].getTopRow());
                        temp += (maze[y][x].getTopRow());
                    }else if(i == 1){
                        //System.out.print(maze[y][x].getMiddleRow());
                        temp += (maze[y][x].getMiddleRow());
                    }else if(i == 2){
                        //System.out.print(maze[y][x].getBottomRow());
                        temp += (maze[y][x].getBottomRow());
                    }
                    
                } 
                //System.out.println();
                temp += "\n";
            }
        }
        return temp;
    }
    
    private boolean isAtBounds(Coordinate coord){
        if(coord.getY() == 0 || coord.getY() == maze.length-1 || coord.getX() == 0 || coord.getX() == maze[0].length-1){
            return true;
        }else{
            return false;
        }
    }
    
    private MazeTile generateBoundedTile(Coordinate coord, boolean star){
        boolean leftBounded = false;
        boolean rightBounded = false;
        boolean upBounded = false;
        boolean downBounded = false;
        
        if(coord.getY() == 0){
            upBounded = true;
        }
        if(coord.getY() == maze.length-1){
            downBounded = true;
        }
        if(coord.getX() == 0){
            leftBounded = true;
        }
        if(coord.getX() == maze[0].length-1){
            rightBounded = true;
        }
        
        boolean left = !leftBounded;
        boolean right = !rightBounded;
        boolean up = !upBounded;
        boolean down = !downBounded;
        
        if(leftBounded){
            int override = (int) Math.ceil(Math.random()*100);
            if(override > 98){
                //left = true;
            }
        }
        if(rightBounded){
            int override = (int) Math.ceil(Math.random()*100);
            if(override > 98){
                //right = true;
            }
        }
        if(upBounded){
            int override = (int) Math.ceil(Math.random()*100);
            if(override > 98){
                //up = true;
            }
        }
        if(downBounded){
            int override = (int) Math.ceil(Math.random()*100);
            if(override > 98){
                //down = true;
            }
        }
        return new MazeTile(left,right,up,down, star);
    }
    
    public int getRandomInt(int max){
        return (int) Math.floor(Math.random()*max);
    }
    
    private boolean isInBounds(Coordinate coord){
        boolean inYBounds = coord.getY() >= 0 && coord.getY() <= maze.length-1;
        boolean inXBounds = coord.getX() >= 0 && coord.getX() <= maze[0].length-1;
        if(!inYBounds){
            return false;
        }
        if(!inXBounds){
            return false;
        }
        return true;
    }
    
    private boolean doesTileExist(Coordinate coord){
        boolean inBounds = this.isInBounds(coord);
        if(!inBounds){
            return false;
        }
        boolean notNull = maze[coord.getY()][coord.getX()] != null;
        if(inBounds && notNull){
            return true;
        }else{
            return false;
        }
    }
    
    public double getPathAverage(){
        return this.pathAverage;
    }
    
    public Direction getRandomDirectionFromRatios(int left, int right, int down, int up){
        int sum = left + right + down + up;
        int[] pathArray = new int[]{left, right, down, up};
        Arrays.sort(pathArray);
        int leftIndex = -1;
        int rightIndex = -1;
        int downIndex = -1;
        int upIndex = -1;
        int i = 0;
        while(leftIndex < 0 || rightIndex < 0 || downIndex < 0 || upIndex < 0){
            if(pathArray[i] == left && leftIndex == -1){
                leftIndex = i;
            }else if(pathArray[i] == right && rightIndex == -1){
                rightIndex = i;
            }else if(pathArray[i] == down && downIndex == -1){
                downIndex = i;
            }else if(pathArray[i] == up && upIndex == -1){
                upIndex = i;
            }
            i++;
        }
        int randomInt = this.getRandomInt(sum)+1;
//        System.out.println(randomInt);
        int breakInt = pathArray[0]*4;
        int lastBreakInt = 0;
        int randomDirection = 0;
        if(randomInt <= breakInt){
//            System.out.println("Break 1");
//            System.out.println((randomInt - lastBreakInt + 3) % 4);
            randomDirection = (randomInt - lastBreakInt + 3) % 4;
        }else{
            lastBreakInt = breakInt;
            breakInt += (pathArray[1] - pathArray[0]) * 3;
            if(randomInt <= breakInt){
//                System.out.println("Break 2");
//                System.out.println(((randomInt - lastBreakInt + 2) % 3) + 1);
                randomDirection = ((randomInt - lastBreakInt + 2) % 3) + 1;
            }else{
                lastBreakInt = breakInt;
                breakInt += (pathArray[2] - pathArray[1]) * 2;
                if(randomInt <= breakInt){
//                    System.out.println("Break 3");
//                    System.out.println((randomInt - lastBreakInt + 1) % 2 + 2);
                    randomDirection = (randomInt - lastBreakInt + 1) % 2 + 2;
                }else{
                    lastBreakInt = breakInt;
//                    System.out.println("Break 4");
//                    System.out.println((randomInt - lastBreakInt) % 1 + 3);
                    randomDirection = (randomInt - lastBreakInt) % 1 + 3;
                }
            }
        }
//        System.out.println(leftIndex + "," + rightIndex + "," + downIndex + "," + upIndex);
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
    
    public int getRandomIntFromRatios(int left, int right, int down, int up){
        int sum = left + right + down + up;
        int[] pathArray = new int[]{left, right, down, up};
        Arrays.sort(pathArray);
        int leftIndex = -1;
        int rightIndex = -1;
        int downIndex = -1;
        int upIndex = -1;
        int i = 0;
        while(leftIndex < 0 || rightIndex < 0 || downIndex < 0 || upIndex < 0){
            if(pathArray[i] == left && leftIndex == -1){
                leftIndex = i;
            }else if(pathArray[i] == right && rightIndex == -1){
                rightIndex = i;
            }else if(pathArray[i] == down && downIndex == -1){
                downIndex = i;
            }else if(pathArray[i] == up && upIndex == -1){
                upIndex = i;
            }
            i++;
        }
        int randomInt = this.getRandomInt(sum)+1;
//        System.out.println(randomInt);
        int breakInt = pathArray[0]*4;
        int lastBreakInt = 0;
        int randomInt2 = 0;
        if(randomInt <= breakInt){
//            System.out.println("Break 1");
//            System.out.println((randomInt - lastBreakInt + 3) % 4);
            randomInt2 = (randomInt - lastBreakInt + 3) % 4;
        }else{
            lastBreakInt = breakInt;
            breakInt += (pathArray[1] - pathArray[0]) * 3;
            if(randomInt <= breakInt){
//                System.out.println("Break 2");
//                System.out.println(((randomInt - lastBreakInt + 2) % 3) + 1);
                randomInt2 = ((randomInt - lastBreakInt + 2) % 3) + 1;
            }else{
                lastBreakInt = breakInt;
                breakInt += (pathArray[2] - pathArray[1]) * 2;
                if(randomInt <= breakInt){
//                    System.out.println("Break 3");
//                    System.out.println((randomInt - lastBreakInt + 1) % 2 + 2);
                    randomInt2 = (randomInt - lastBreakInt + 1) % 2 + 2;
                }else{
                    lastBreakInt = breakInt;
//                    System.out.println("Break 4");
//                    System.out.println((randomInt - lastBreakInt) % 1 + 3);
                    randomInt2 = (randomInt - lastBreakInt) % 1 + 3;
                }
            }
        }
//        System.out.println(leftIndex + "," + rightIndex + "," + downIndex + "," + upIndex);
        return randomInt2;
        
    }
    
}
