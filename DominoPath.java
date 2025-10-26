import java.io.*;
import java.util.*;

public class DominoPath {
    static final int N = 128;

    static int[][] adj = new int[7][7];
    static int[] avail = new int[7];
    static int[] sumE = new int[N];
    static int[] actV = new int[N];
    static ArrayList<Integer>[] comps = new ArrayList[N];

    static long[][] F = new long[N][N];
    static long[][] Cdp = new long[N][N];
    static long[] F_nonzero = new long[N];
    static long[] Cdp_nonzero = new long[N];

    static int[][] EVEN_SUBSETS = new int[128][];
    static int[][] SUBMASKS = new int[128][];

    static int[][] precomputed_sign = new int[N][N];
    static int[][] xor_table = new int[N][N];
    static int[] popcount_table = new int[N];
    static int[][] intersection_popcount = new int[N][N];
    static int[][] bit_positions = new int[N][7];
    static int[] bit_count = new int[N];
    static long[] power_of_2 = new long[65];

    static int[] lowbit_cache = new int[N];
    static int[][] and_table = new int[N][N];

    static HashMap<Long, int[]> COMPONENT_PRODUCT_CACHE = new HashMap<>(1 << 12);
    static HashMap<Long, Long> CACHE64 = new HashMap<>(1 << 12);

    static boolean[] bufA = new boolean[128];
    static boolean[] bufB = new boolean[128];

    static long makeKey(List<Integer> key) {
        long res = 0;
        for (int x : key) {
            res = (res << 3) | (x & 7L);
        }
        return res;
    }

    static long packComponentsKey(List<Integer> cs) {
        int m = cs.size();
        int[] arr = new int[m];
        for (int i = 0; i < m; i++) arr[i] = cs.get(i);
        Arrays.sort(arr);
        long key = ((long) m) << 56;
        for (int i = 0; i < m; i++)
            key |= ((long) (arr[i] & 127)) << (i * 7);
        return key;
    }

    static void precomputeTables() {
        power_of_2[0] = 1;
        for (int i = 1; i <= 64; i++) power_of_2[i] = power_of_2[i - 1] << 1;

        for (int i = 0; i < N; i++) lowbit_cache[i] = i & -i;

        for (int mask = 0; mask < 128; mask++) {
            int bits = Integer.bitCount(mask);
            int[] all = new int[1 << bits];
            int p = 0;
            int sub = mask;
            while (true) {
                all[p++] = sub;
                if (sub == 0) break;
                sub = (sub - 1) & mask;
            }
            SUBMASKS[mask] = Arrays.copyOf(all, p);

            int[] tmpEven = new int[p];
            int pe = 0;
            for (int i = 0; i < p; i++)
                if ((Integer.bitCount(all[i]) & 1) == 0)
                    tmpEven[pe++] = all[i];
            EVEN_SUBSETS[mask] = Arrays.copyOf(tmpEven, pe);
        }

        for (int S = 0; S < N; S++)
            for (int U = 0; U < N; U++)
                if ((U & S) == U)
                    precomputed_sign[S][U] = 1 - 2 * ((Integer.bitCount(S) - Integer.bitCount(U)) & 1);

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                xor_table[i][j] = i ^ j;
                and_table[i][j] = i & j;
            }
        }

        for (int i = 0; i < N; i++) popcount_table[i] = Integer.bitCount(i);

        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                intersection_popcount[i][j] = popcount_table[and_table[i][j]];

        for (int mask = 0; mask < N; mask++) {
            int cnt = 0;
            for (int i = 0; i < 7; i++)
                if ((mask & (1 << i)) != 0)
                    bit_positions[mask][cnt++] = i;
            bit_count[mask] = cnt;
        }

        for (int i = 0; i < N; i++) comps[i] = new ArrayList<>();
    }

    static int[] getComponentProduct(List<Integer> cs) {
        long key = packComponentsKey(cs);
        int[] ok = COMPONENT_PRODUCT_CACHE.get(key);
        if (ok != null) return ok;

        Arrays.fill(bufA, false);
        Arrays.fill(bufB, false);
        bufA[0] = true;

        boolean[] cur = bufA, nxt = bufB;

        for (int comp : cs) {
            int[] choices = EVEN_SUBSETS[comp];
            Arrays.fill(nxt, false);
            for (int mask = 0; mask < 128; mask++) {
                if (!cur[mask]) continue;
                for (int s : choices)
                    nxt[mask | s] = true;
            }
            boolean[] t = cur;
            cur = nxt;
            nxt = t;
        }

        int cnt = 0;
        for (int i = 0; i < 128; i++) if (cur[i]) cnt++;
        int[] res = new int[cnt];
        int p = 0;
        for (int i = 0; i < 128; i++) if (cur[i]) res[p++] = i;

        COMPONENT_PRODUCT_CACHE.put(key, res);
        return res;
    }

    public static void main(String[] args) throws Exception {
        FastScanner fs = new FastScanner(System.in);
        precomputeTables();

        int T = fs.nextInt();
        while (T-- > 0) {
            int M = fs.nextInt();
            if (M == 0) { System.out.println(0); continue; }

            for (int i = 0; i < 7; i++)
                Arrays.fill(adj[i], 0);

            for (int i = 0; i < M; i++) {
                int a = fs.nextInt() - 1;
                int b = fs.nextInt() - 1;
                if (a > b) { int t = a; a = b; b = t; }
                adj[a][b]++;
            }

            ArrayList<Integer> key = new ArrayList<>(21);
            for (int i = 0; i < 7; i++)
                for (int j = i + 1; j < 7; j++)
                    key.add(adj[i][j]);

            long key64 = makeKey(key);
            Long cached = CACHE64.get(key64);
            if (cached != null) {
                System.out.println(cached);
                continue;
            }

            Arrays.fill(avail, 0);
            for (int i = 0; i < 7; i++)
                for (int j = i + 1; j < 7; j++)
                    if (adj[i][j] != 0) {
                        avail[i] |= 1 << j;
                        avail[j] |= 1 << i;
                    }

            for (int U = 0; U < N; U++) {
                int s = 0, cnt = bit_count[U];
                for (int a = 0; a < cnt; a++) {
                    int i = bit_positions[U][a];
                    for (int b = a + 1; b < cnt; b++) {
                        int j = bit_positions[U][b];
                        s += adj[i][j];
                    }
                }
                sumE[U] = s;

                int av = 0;
                for (int a = 0; a < cnt; a++) {
                    int v = bit_positions[U][a];
                    if ((avail[v] & U) != 0) av |= 1 << v;
                }
                actV[U] = av;

                comps[U].clear();
                int rem = av;
                while (rem != 0) {
                    int start = rem & -rem;
                    int idx = Integer.numberOfTrailingZeros(start);
                    rem ^= start;
                    int comp = start;
                    int[] stk = new int[7];
                    int top = 0;
                    stk[top++] = idx;
                    while (top > 0) {
                        int u = stk[--top];
                        int nbr = avail[u] & av & ~comp;
                        while (nbr != 0) {
                            int bit = nbr & -nbr;
                            int w = Integer.numberOfTrailingZeros(bit);
                            nbr ^= bit;
                            if ((rem & (1 << w)) != 0) rem ^= 1 << w;
                            comp |= 1 << w;
                            stk[top++] = w;
                        }
                    }
                    comps[U].add(comp);
                }
            }

            for (int i = 0; i < N; i++) {
                Arrays.fill(F[i], 0);
                Arrays.fill(Cdp[i], 0);
                F_nonzero[i] = 0;
                Cdp_nonzero[i] = 0;
            }

            for (int S = 1; S < N; S++) {
                int[] subsS = SUBMASKS[S];
                for (int a = 0; a < subsS.length; a++) {
                    int U = subsS[a];
                    int sign = precomputed_sign[S][U];
                    int au = actV[U];
                    int rU = popcount_table[au] - comps[U].size();
                    long base = power_of_2[sumE[U] - rU];

                    int[] subsets = getComponentProduct(comps[U]);
                    for (int mask : subsets) {
                        long nv = F[S][mask] + sign * base;
                        F[S][mask] = nv;
                        if (nv != 0) F_nonzero[S] |= (1L << mask);
                    }
                }
            }

            for (int S = 1; S < N; S++) {
                long maskSrc = F_nonzero[S];
                System.arraycopy(F[S], 0, Cdp[S], 0, N);
                Cdp_nonzero[S] = maskSrc;

                int root = lowbit_cache[S];

                for (long Amsk = S; Amsk != 0; Amsk = (Amsk - 1) & S) {
                    int A = (int) Amsk;
                    if (A == S || A == 0) continue;
                    if ((A & root) == 0) continue;

                    int B = S ^ A;
                    long maskA = Cdp_nonzero[A];
                    long maskB = F_nonzero[B];
                    if ((maskA == 0) || (maskB == 0)) continue;

                    long mA = maskA;
                    while (mA != 0) {
                        long lbA = mA & -mA;
                        mA ^= lbA;
                        int pA = Long.numberOfTrailingZeros(lbA);
                        long vA = Cdp[A][pA];
                        if (vA == 0) continue;

                        long mB = maskB;
                        while (mB != 0) {
                            long lbB = mB & -mB;
                            mB ^= lbB;
                            int pB = Long.numberOfTrailingZeros(lbB);
                            long vB = F[B][pB];
                            if (vB == 0) continue;

                            int p = pA ^ pB;
                            long nv = Cdp[S][p] - vA * vB;
                            Cdp[S][p] = nv;
                            if (nv != 0) Cdp_nonzero[S] |= (1L << p);
                            else Cdp_nonzero[S] &= ~(1L << p);
                        }
                    }
                }
            }

            long ans = 0;
            for (int S = 1; S < N; S++) {
                long mask = Cdp_nonzero[S];
                while (mask != 0) {
                    long lb = mask & -mask;
                    mask ^= lb;
                    int P = Long.numberOfTrailingZeros(lb);
                    long val = Cdp[S][P];
                    if (val == 0) continue;
                    int k = intersection_popcount[P][S];
                    if (k == 0 || k == 2) ans += val;
                }
            }

            CACHE64.put(key64, ans);
            System.out.println(ans);
        }
    }

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
            int c;
            while ((c = read()) <= ' ');
            int s = 1;
            if (c == '-') { s = -1; c = read(); }
            int x = c - '0';
            while ((c = read()) > ' ') x = x * 10 + (c - '0');
            return x * s;
        }
    }
}
