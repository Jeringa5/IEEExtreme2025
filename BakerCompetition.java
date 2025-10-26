import java.io.*;
import java.util.*;

public class BakerCompetition {

    static final int N = 1_000_000;

    static class FastScanner {
        private final InputStream in;
        private final byte[] buffer = new byte[1 << 16];
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
            do { c = read(); } while (c <= 32);
            if (c == '-') { sgn = -1; c = read(); }
            while (c > 32) {
                x = x * 10 + (c - '0');
                c = read();
            }
            return x * sgn;
        }
    }

    static class Query implements Comparable<Query> {
        int l, r, idx, block;
        public int compareTo(Query o) {
            if (block != o.block) return block < o.block ? -1 : 1;
            if ((block & 1) == 1) return r < o.r ? -1 : (r > o.r ? 1 : 0);
            return r > o.r ? -1 : (r < o.r ? 1 : 0);
        }
    }

    static int[][] store;  // para cada x: hasta dos veces el id del primo p (ip) tal que x = p*q
    static int[] scnt;     // cuantos ids tiene x (0..2)
    static int[] freq;     // frecuencia por id de primo en la ventana
    static long cur;       // respuesta acumulada

    static void add(int x) {
        int c = scnt[x];
        if (c == 0) return;
        int p = store[x][0];
        cur += freq[p];
        freq[p]++;
        if (c == 2) {
            int p2 = store[x][1];
            cur += freq[p2];
            freq[p2]++;
        }
    }

    static void removeOne(int x) {
        int c = scnt[x];
        if (c == 0) return;
        int p = store[x][0];
        freq[p]--;
        cur -= freq[p];
        if (c == 2) {
            int p2 = store[x][1];
            freq[p2]--;
            cur -= freq[p2];
        }
    }

    public static void main(String[] args) throws Exception {
        FastScanner fs = new FastScanner(System.in);
        StringBuilder sb = new StringBuilder(1 << 20);

        boolean[] isPrime = new boolean[N + 1];
        Arrays.fill(isPrime, true);
        isPrime[0] = isPrime[1] = false;
        for (int i = 4; i <= N; i += 2) isPrime[i] = false;
        int lim = (int)Math.sqrt(N);
        for (int i = 3; i <= lim; i += 2) {
            if (isPrime[i]) {
                int step = i << 1;
                for (int j = i * i; j <= N; j += step) isPrime[j] = false;
            }
        }

        int P = 0;
        for (int i = 2; i <= N; ++i) if (isPrime[i]) P++;
        int[] primes = new int[P];
        for (int i = 2, k = 0; i <= N; ++i) if (isPrime[i]) primes[k++] = i;

        store = new int[N + 1][2];
        scnt  = new int[N + 1];
        for (int i = 0; i <= N; ++i) { store[i][0] = -1; store[i][1] = -1; }

        // MISMA LÓGICA QUE TU C++:
        // for ip over primes p:
        //   for iq over primes q:
        //     x = p*q <= N
        //     if scnt[x] < 2: store[x][scnt[x]++] = ip
        for (int ip = 0; ip < P; ++ip) {
            int p = primes[ip];
            for (int iq = 0; iq < P; ++iq) {
                int q = primes[iq];
                long n = 1L * p * q;
                if (n > N) break;
                int x = (int) n;
                if (scnt[x] < 2) {
                    store[x][scnt[x]] = ip;
                    scnt[x]++;
                }
            }
        }

        int T = fs.nextInt();
        Query[] Qs = new Query[T];

        int B = Math.max(1, (int)(N / Math.max(1.0, Math.sqrt((double)T))));

        for (int i = 0; i < T; ++i) {
            int L = fs.nextInt();
            int R = fs.nextInt();
            Query q = new Query();
            q.l = L; q.r = R; q.idx = i; q.block = L / B;
            Qs[i] = q;
        }

        Arrays.sort(Qs);

        long[] ans = new long[T];
        freq = new int[P];
        cur = 0;

        int L = 1, R = 0;
        for (Query q : Qs) {
            while (R < q.r) add(++R);
            while (R > q.r) removeOne(R--);
            while (L < q.l) removeOne(L++);
            while (L > q.l) add(--L);
            ans[q.idx] = cur;
        }

        for (int i = 0; i < T; ++i) sb.append(ans[i]).append('\n');
        System.out.print(sb.toString());
    }
}