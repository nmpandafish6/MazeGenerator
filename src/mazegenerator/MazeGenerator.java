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
//        byte value = 0;
//        for(; value < 33; value++){
//            MazeTile tile = new MazeTile(value);
//            System.out.println(tile + " " + value);
//            System.out.println();
//        }
//        MazeTile[][] maze = new MazeTile[2][2];
//        maze[0][0] = new MazeTile(true,false,true,false);
//        maze[0][1] = new MazeTile(false,true,true,false);
//        maze[1][0] = new MazeTile(true,false,false,true);
//        maze[1][1] = new MazeTile(false,true,false,true);
        Maze maze = new Maze(47, 11, 1,1);
        //Test
        //Test 2
        maze.generate();
        
        System.out.print(maze.toString());
    }

}
