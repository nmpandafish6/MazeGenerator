package mazegenerator;

import java.io.FileWriter;

/**
 *
 * @author mallory
 */
public class MazeGenerator {

    public static void main(String[] args) throws Exception {
        int width = 47;
        int height = 11;
        int startX = (int) Math.floor(Math.random() * width);
        int startY = (int) Math.floor(Math.random() * height);
        Maze maze = new Maze(width, height, startX,startY);
        maze.generate();
        while(maze.getTilesCreated() < (width * height / 2)){
            System.out.println("Regenerating");
            maze.regenerate();
        }
        System.out.print(maze.toString()); 
        System.out.println(maze.getPathAverage());

    }

}
