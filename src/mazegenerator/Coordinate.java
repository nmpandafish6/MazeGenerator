package mazegenerator;

/**
 *
 * @author mallory
 */
public class Coordinate {

    private int x = 0;
    private int y = 0;
    private int lastX = 0;
    private int lastY = 0;
    
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
    
    public void shiftUp(){
        y = y - 1;
        lastY = y;
        lastX = x;
    }
    
    public void shiftDown(){
        y = y + 1;
        lastY = y;
        lastX = x;
    }
    
    public void shiftLeft(){
        x = x - 1;
        lastY = y;
        lastX = x;
    }
    
    public void shiftRight(){
        x = x + 1;
        lastY = y;
        lastX = x;
    }
    
    public void reverseChange(){
        x = lastX;
        y = lastY;
    }
    
    public String toString(){
        return ("(" + x + " , " + y + ")");
    }
}
