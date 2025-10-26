import java.io.*;
import java.util.*;

public class DiameterProblemAgain {
    static final int LIM = 300000 + 5;
    static final int LIM2 = (LIM << 1);
    static final long MENOS_INF = (long)-4e18;

    static int nodos, consultas;

    // ---- Fast IO ----
    static final InputStream in = System.in;
    static final byte[] bufIn = new byte[1 << 20];
    static int pIn = 0, nIn = 0;
    static int leer() throws IOException {
        if (pIn >= nIn) {
            nIn = in.read(bufIn);
            pIn = 0;
            if (nIn <= 0) return -1;
        }
        return bufIn[pIn++];
    }
    static int leerInt() throws IOException {
        int c = leer(), s = 1, x = 0;
        while (c != '-' && (c < '0' || c > '9')) c = leer();
        if (c == '-') { s = -1; c = leer(); }
        while (c >= '0' && c <= '9') { x = x * 10 + (c - '0'); c = leer(); }
        return x * s;
    }
    static long leerLong() throws IOException {
        int c = leer(); long s = 1, x = 0;
        while (c != '-' && (c < '0' || c > '9')) c = leer();
        if (c == '-') { s = -1; c = leer(); }
        while (c >= '0' && c <= '9') { x = x * 10 + (c - '0'); c = leer(); }
        return x * s;
    }
    static StringBuilder sb = new StringBuilder(1 << 20);
    static void outLn(long x) { sb.append(x).append('\n'); }

    // ---- Grafo (CSR) ----
    static int[] head = new int[LIM];
    static int[] to = new int[LIM2];
    static int[] w = new int[LIM2];
    static int[] nx = new int[LIM2];
    static int ec;

    static void add(int u, int v, int ww) {
        to[ec] = v; w[ec] = ww; nx[ec] = head[u]; head[u] = ec++;
    }

    // ---- Euler + RMQ (LCA) ----
    static int[] first = new int[LIM];
    static int[] euler = new int[LIM2];     // nodos en el recorrido
    static int[] depthE = new int[LIM2];    // profundidad en cada pos de euler
    static long[] dist = new long[LIM];     // distancias desde la raíz
    static int[][] st;                      // sparse table sobre índices de euler
    static int[] lg2;
    static int et;                          // tamaño real del euler

    static void buildEulerIter() {
        Arrays.fill(first, -1);
        et = 0;

        int[] it = new int[LIM];                    // iterador de aristas por nodo
        int[] stU = new int[LIM];                   // pila: nodo
        int[] stP = new int[LIM];                   // pila: padre
        int[] stS = new int[LIM];                   // pila: estado 0=enter,1=scan,2=exit
        int top = 0;

        stU[top] = 1; stP[top] = -1; stS[top] = 0; top++;
        int curDepth = 0;
        dist[1] = 0;
        Arrays.fill(it, -1);

        while (top > 0) {
            int u = stU[top - 1];
            int p = stP[top - 1];
            int s = stS[top - 1];

            if (s == 0) { // enter
                if (first[u] == -1) first[u] = et;
                euler[et] = u; depthE[et] = curDepth; et++;
                stS[top - 1] = 1;
                it[u] = head[u];
            } else if (s == 1) { // scan children
                int e = it[u];
                while (e != -1 && to[e] == p) e = nx[e];
                if (e == -1) {
                    stS[top - 1] = 2;
                } else {
                    it[u] = nx[e];
                    int v = to[e];
                    dist[v] = dist[u] + w[e];
                    stU[top] = v; stP[top] = u; stS[top] = 0; top++;
                    curDepth++;
                }
            } else { // exit to parent
                if (p != -1) {
                    euler[et] = p; curDepth--; depthE[et] = curDepth; et++;
                }
                top--;
            }
        }

        lg2 = new int[et + 1];
        lg2[1] = 0;
        for (int i = 2; i <= et; i++) lg2[i] = lg2[i >> 1] + 1;

        int K = lg2[et] + 1;
        st = new int[K][et];
        for (int i = 0; i < et; i++) st[0][i] = i;
        for (int k = 1; (1 << k) <= et; k++) {
            int len = 1 << k, half = len >> 1;
            for (int i = 0; i + len <= et; i++) {
                int a = st[k - 1][i], b = st[k - 1][i + half];
                st[k][i] = (depthE[a] <= depthE[b]) ? a : b;
            }
        }
    }

    static int lca(int a, int b) {
        int i = first[a], j = first[b];
        if (i > j) { int t = i; i = j; j = t; }
        int k = lg2[j - i + 1];
        int x = st[k][i], y = st[k][j - (1 << k) + 1];
        return (depthE[x] <= depthE[y]) ? euler[x] : euler[y];
    }
    static long distAB(int a, int b) {
        if (a == b) return 0;
        int c = lca(a, b);
        return dist[a] + dist[b] - 2L * dist[c];
    }

    // ---- Segment tree (diámetro del rango) ----
    static int[] arr = new int[LIM];

    static class Par { int a, b; Par(){} Par(int a,int b){this.a=a;this.b=b;} }
    static Par[] seg = new Par[4 * LIM];

    static long diamLen(Par p) {
        if (p == null || p.a == 0) return -1;
        return distAB(p.a, p.b);
    }
    static Par merge(Par L, Par R) {
        if (L == null || L.a == 0) return R;
        if (R == null || R.a == 0) return L;
        Par best = new Par(L.a, L.b);
        long mb = diamLen(best), mr = diamLen(R);
        if (mr > mb) { best = new Par(R.a, R.b); mb = mr; }
        int[] xs = {L.a, L.b}, ys = {R.a, R.b};
        for (int u : xs) for (int v : ys) {
            long d = distAB(u, v);
            if (d > mb) { mb = d; best.a = u; best.b = v; }
        }
        return best;
    }
    static void buildSeg(int idx, int l, int r) {
        if (seg[idx] == null) seg[idx] = new Par(0, 0);
        if (l == r) { seg[idx].a = 0; seg[idx].b = 0; return; }
        int m = (l + r) >> 1;
        buildSeg(idx << 1, l, m);
        buildSeg(idx << 1 | 1, m + 1, r);
        seg[idx] = merge(seg[idx << 1], seg[idx << 1 | 1]);
    }
    static void upd(int idx, int l, int r, int pos, int val) {
        if (l == r) { seg[idx].a = val; seg[idx].b = val; return; }
        int m = (l + r) >> 1;
        if (pos <= m) upd(idx << 1, l, m, pos, val);
        else upd(idx << 1 | 1, m + 1, r, pos, val);
        seg[idx] = merge(seg[idx << 1], seg[idx << 1 | 1]);
    }
    static Par query(int idx, int l, int r, int ql, int qr) {
        if (qr < l || r < ql) return new Par(0, 0);
        if (ql <= l && r <= qr) return seg[idx];
        int m = (l + r) >> 1;
        Par L = query(idx << 1, l, m, ql, qr);
        Par R = query(idx << 1 | 1, m + 1, r, ql, qr);
        return merge(L, R);
    }

    public static void main(String[] args) throws Exception {
        nodos = leerInt();
        for (int i = 1; i <= nodos; i++) head[i] = -1;
        ec = 0;
        for (int i = 0; i < nodos - 1; i++) {
            int u = leerInt(), v = leerInt(), ww = leerInt();
            add(u, v, ww);
            add(v, u, ww);
        }
        buildEulerIter();            // <-- LCA corregido

        buildSeg(1, 1, LIM - 1);

        consultas = leerInt();
        int sz = 0; long last = 0;

        while (consultas-- > 0) {
            int t = leerInt();
            if (t == 1) {
                long x = leerLong();
                x = ((x ^ Math.abs(last)) % nodos) + 1;
                arr[++sz] = (int)x;
                upd(1, 1, LIM - 1, sz, (int)x);
            } else if (t == 2) {
                if (sz > 0) {
                    upd(1, 1, LIM - 1, sz, 0);
                    --sz;
                }
            } else {
                long l = leerLong(), r = leerLong(), x = leerLong();
                if (sz == 0) { outLn(0); last = 0; continue; }
                x = ((x ^ Math.abs(last)) % nodos) + 1;
                l = ((l ^ Math.abs(last)) % sz) + 1;
                r = ((r ^ Math.abs(last)) % sz) + 1;
                if (l > r) { long tmp = l; l = r; r = tmp; }
                Par p = query(1, 1, LIM - 1, (int)l, (int)r);
                long ans = Math.max(distAB((int)x, p.a), distAB((int)x, p.b));
                last = ans; outLn(ans);
            }
        }
        System.out.print(sb.toString());
    }
}
