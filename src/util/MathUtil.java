package util;

/**
 *
 * @author mallory
 */
public class MathUtil {

    public static long gcd(long a, long b){
        long r = b;
        while(b > 0){
            r = b;
            b = a % b;
            a = r;
        }
        return r;
    }
    
    public static long gcd(long[] a){
        if(a.length == 1){
            return a[0];
        }else{
            long gcd = 1;
            for(int i = 0; i < a.length; i++){
                if(i == 0){
                    gcd = gcd(a[i], a[i+1]);
                }else{
                    gcd = gcd(a[i], gcd);
                }
            }
            return gcd;
        }
    }
    
    public static long lcm(long a, long b){
        return a * (b / gcd(a,b));
    }
    
    public static long lcm(long[] a){
        if(a.length == 1){
            return a[0];
        }else{
            long lcm = 1;
            for(int i = 0; i < a.length; i++){
                if(i == 0){
                    lcm = lcm(a[i], a[i+1]);
                }else{
                    lcm = lcm(a[i], lcm);
                }
            }
            return lcm;
        }
    }
    
}
