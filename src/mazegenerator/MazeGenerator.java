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
//        int total = 1000;
//        int left = 0;
//        int right = 0;
//        int up = 0;
//        int down = 0;
//        for(int i = 0;i < total; i++){
//            int random = maze.getRandomIntFromRatios(2, 3, 4, 5);
//            if(random == 0){
//                left++;
//            }else if(random == 1){
//                right++;
//            }else if(random == 2){
//                down++;
//            }else{
//                up++;
//            }
//        }
//        System.out.println(left + "\t" + right + "\t" + down + "\t" + up);
//        System.out.println((2./14.*1000.) + "\t" + (3./14.*1000.) + "\t" + (4./14.*1000.) + "\t" + (5./14.*1000.));
//        
    }

}
