package mazegenerator;

import java.io.FileWriter;

/**
 *
 * @author mallory
 */
public class MazeGenerator {

    private static FileWriter fw = null;
    public static void main(String[] args) throws Exception {
        int width = 47;
        int height = 11;
        int numberOfFiles = 20;
        for(int i = 0; i < numberOfFiles; i++){
            Maze maze = new Maze(width, height, 0,0);
            fw = new FileWriter("Maze " + i + ".md");
            fw.write("```");
            maze.generate();
            while(maze.getTilesCreated() < (width * height / 2)){
                System.out.println("Regenerating");
                maze.regenerate();
            }
            fw.write(maze.toString());
            fw.flush();
            fw.write("```");
            fw.close();
            System.out.print(maze.toString()); 
            System.out.println(maze.getPathAverage());
        }

    }

}
