package mazegenerator;

/**
 *
 * @author mallory
 */
public class Coordinate {

    private int x = 0;
    private int y = 0;
    
    public Coordinate(int x, int y){
        this.x = x;
        this.y = y;
    }
    
    public int getX(){
        return x;
    }
    
    public int getY(){
        return y;
    }
    
    public String toString(){
        return ("(" + x + " , " + y + ")");
    }
}
