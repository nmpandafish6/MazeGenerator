package mazegenerator;

import java.util.ArrayList;

/**
 *
 * @author mallory
 */
public class MazeTile {

    private final byte rightMask = 1 << 0;
    private final byte leftMask = 1 << 1;
    private final byte downMask = 1 << 2;
    private final byte upMask = 1 << 3;
    private final byte starMask = 1 << 4;
    private byte orientation = 0;
    public static final MazeTile[] fourWayPath = new MazeTile[] { new MazeTile((byte)15),
                                                                   new MazeTile((byte)31),
                                                                   };
    public static final MazeTile[] threeWayPath = new MazeTile[] { new MazeTile((byte)7),
                                                                   new MazeTile((byte)11),
                                                                   new MazeTile((byte)13),
                                                                   new MazeTile((byte)14),
                                                                   new MazeTile((byte)23),
                                                                   new MazeTile((byte)27),
                                                                   new MazeTile((byte)29),
                                                                   new MazeTile((byte)30)};
    public static final MazeTile[] twoWayPath = new MazeTile[] { new MazeTile((byte)3),
                                                                   new MazeTile((byte)5),
                                                                   new MazeTile((byte)6),
                                                                   new MazeTile((byte)9),
                                                                   new MazeTile((byte)10),
                                                                   new MazeTile((byte)12),
                                                                   new MazeTile((byte)19),
                                                                   new MazeTile((byte)21),
                                                                   new MazeTile((byte)22),
                                                                   new MazeTile((byte)25),
                                                                   new MazeTile((byte)26),
                                                                   new MazeTile((byte)28)
                                                                   };
    public static final MazeTile[] oneWayPath = new MazeTile[] { new MazeTile((byte)1),
                                                                   new MazeTile((byte)2),
                                                                   new MazeTile((byte)4),
                                                                   new MazeTile((byte)8),
                                                                   new MazeTile((byte)17),
                                                                   new MazeTile((byte)18),
                                                                   new MazeTile((byte)20),
                                                                   new MazeTile((byte)24)};
/*    
   _ _ _ _ _
   * u d l r
*/ 
    public MazeTile(boolean left, boolean right, boolean up, boolean down){
        orientation += left ? leftMask : 0;
        orientation += right ? rightMask : 0;
        orientation += up ? upMask : 0;
        orientation += down ? downMask : 0;
    }
    
    public MazeTile(boolean left, boolean right, boolean up, boolean down, boolean star){
        orientation += left ? leftMask : 0;
        orientation += right ? rightMask : 0;
        orientation += up ? upMask : 0;
        orientation += down ? downMask : 0;
        orientation += star ? starMask : 0;
    }
    
    public MazeTile(ArrayList<Direction> directions){
        boolean leftMatched = false;
        boolean rightMatched = false;
        boolean downMatched = false;
        boolean upMatched = false;
        for(int i = 0; i < directions.size(); i++){
            if(directions.get(i) == Direction.left && !leftMatched){
                orientation += leftMask;
                leftMatched = true;
            }else if(directions.get(i) == Direction.right && !rightMatched){
                orientation += rightMask;
                rightMatched = true;
            }else if(directions.get(i) == Direction.up && !upMatched){
                orientation += upMask;
                upMatched = true;
            }else if(directions.get(i) == Direction.down && !downMatched){
                orientation += downMask;
                downMatched = true;
            }
        }
    }
    
    public MazeTile(ArrayList<Direction> directions, boolean star){
        boolean leftMatched = false;
        boolean rightMatched = false;
        boolean downMatched = false;
        boolean upMatched = false;
        for(int i = 0; i < directions.size(); i++){
            if(directions.get(i) == Direction.left && !leftMatched){
                orientation += leftMask;
                leftMatched = true;
            }else if(directions.get(i) == Direction.right && !rightMatched){
                orientation += rightMask;
                rightMatched = true;
            }else if(directions.get(i) == Direction.up && !upMatched){
                orientation += upMask;
                upMatched = true;
            }else if(directions.get(i) == Direction.down && !downMatched){
                orientation += downMask;
                downMatched = true;
            }
        }
        orientation += star ? starMask : 0;
    }
    
    public MazeTile(byte value){
        orientation = (byte) (value & 0x1f);
    }
    
    public String getTopRow(){
        boolean up = (orientation & upMask) != 0;
        String topRow = "";
        if(up){
            topRow = "\u2593 \u2593";
        }else{
            topRow = "\u2593\u2593\u2593";
        }
        return topRow;
    }
    
    public String getMiddleRow(){
        boolean left = (orientation & leftMask) != 0;
        boolean right = (orientation & rightMask) != 0;
        boolean star = (orientation & starMask) != 0;
        String middleRow = "";
        if(left){
            middleRow += " ";
        }else{
            middleRow += "\u2593";
        }
        if(star){
            middleRow += "*";
        }else{
            middleRow += " ";
        }
        if(right){
            middleRow += " ";
        }else{
            middleRow += "\u2593";
        }
        return middleRow;
    }
    
    public String getBottomRow(){
        boolean down = (orientation & downMask) != 0;
        String bottomRow = "";
        if(down){
            bottomRow = "\u2593 \u2593";
        }else{
            bottomRow = "\u2593\u2593\u2593";
        }
        return bottomRow;
    }
    
    @Override
    public String toString(){
        boolean left = (orientation & leftMask) != 0;
        boolean right = (orientation & rightMask) != 0;
        boolean up = (orientation & upMask) != 0;
        boolean down = (orientation & downMask) != 0;
//        String strOrientation = (left ? "Left," : "") +
//                                (right ? "Right," : "") +
//                                (up ? "Up," : "") +
//                                (down ? "Down," : "") +
//                                (!left & !right & !up & !down ? "Null," : "");
//        return strOrientation;
        String symbol = this.getTopRow() + "\n" + this.getMiddleRow() + "\n" + this.getBottomRow();
        return symbol;
    }
    
    public boolean getUp(){
        boolean up = (orientation & upMask) != 0;
        return up;
    }
    
    public boolean getDown(){
        boolean down = (orientation & downMask) != 0;
        return down;
    }

    public boolean getLeft(){
        boolean left = (orientation & leftMask) != 0;
        return left;
    }
    
    public boolean getRight(){
        boolean right = (orientation & rightMask) != 0;
        return right;
    }
    
}
