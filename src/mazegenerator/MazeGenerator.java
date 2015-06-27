package mazegenerator;

import java.util.ArrayList;
import java.util.Arrays;
import util.MathUtil;

/**
 *
 * @author mallory
 */
public class MazeGenerator {

    public static void main(String[] args) throws Exception {
        Maze maze = new Maze(47, 11, 1,1);
        maze.generate();
        System.out.print(maze.toString());
    }

}
