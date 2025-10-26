import java.io.*;
import java.util.*;

public class NewCasinoGame {
    static final long MOD = 998244353L;
    static long modPow(long a, long e){
        long r = 1 % MOD;
        a %= MOD;
        while(e>0){
            if((e&1)==1) r = (r*a)%MOD;
            a = (a*a)%MOD;
            e >>= 1;
        }
        return r;
    }
    public static void main(String[] args) throws Exception{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int T = Integer.parseInt(br.readLine().trim());
        StringBuilder out = new StringBuilder();
        for(int t=0;t<T;t++){
            long N = Long.parseLong(br.readLine().trim());
            if((N&1)==0) out.append(0).append('\n');
            else out.append(modPow(N, MOD-2)).append('\n');
        }
        System.out.print(out.toString());
    }
}