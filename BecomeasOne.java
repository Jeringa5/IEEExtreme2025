import java.io.*;
import java.util.*;

public class BecomeasOne {
    static final class FastScanner {
        private final InputStream in;
        private final byte[] buffer = new byte[1 << 20];
        private int ptr = 0, len = 0;
        FastScanner(InputStream is) { in = is; }
        private int read() throws IOException {
            if (ptr >= len) {
                len = in.read(buffer);
                ptr = 0;
                if (len <= 0) return -1;
            }
            return buffer[ptr++];
        }
        int nextInt() throws IOException {
            int c, sgn = 1, x = 0;
            do c = read(); while (c <= 32);
            if (c == '-') { sgn = -1; c = read(); }
            while (c > 32) { x = x * 10 + (c - '0'); c = read(); }
            return x * sgn;
        }
    }

    public static void main(String[] args) throws Exception {
        FastScanner fs = new FastScanner(System.in);
        int N = fs.nextInt();
        int Q = fs.nextInt();

        long[] pref = new long[N + 1];
        for (int i = 1; i <= N; i++) {
            int a = fs.nextInt();
            pref[i] = pref[i - 1] + (1L << a);
        }

        StringBuilder sb = new StringBuilder(Q * 4);
        for (int i = 0; i < Q; i++) {
            int L = fs.nextInt();
            int R = fs.nextInt();
            long S = pref[R] - pref[L - 1];
            boolean ok = S != 0 && ( (S & (S - 1)) == 0 );
            sb.append(ok ? "Yes\n" : "No\n");
        }
        System.out.print(sb.toString());
    }
}
