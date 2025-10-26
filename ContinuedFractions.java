import java.io.*;
import java.util.*;

public class ContinuedFractions {

    static final int MOD = 998244353;
    static final int G = 3;

    static final class FastScanner {
        private final InputStream in;
        private final byte[] buffer = new byte[1 << 16];
        private int ptr = 0, len = 0;
        FastScanner(InputStream is){ in = is; }
        private int read() throws IOException {
            if (ptr >= len) { len = in.read(buffer); ptr = 0; if (len <= 0) return -1; }
            return buffer[ptr++];
        }
        int nextInt() throws IOException {
            int c, s = 1, x = 0;
            do c = read(); while (c <= 32);
            if (c == '-') { s = -1; c = read(); }
            while (c > 32) { x = x * 10 + (c - '0'); c = read(); }
            return x * s;
        }
    }

    static final class Mint {
        int v;
        Mint(int _v){ v = _v % MOD; if (v < 0) v += MOD; }
        Mint(){ this(0); }
        Mint add(Mint o){ int x = v + o.v; if (x >= MOD) x -= MOD; return new Mint(x); }
        Mint sub(Mint o){ int x = v - o.v; if (x < 0) x += MOD; return new Mint(x); }
        Mint mul(Mint o){ long x = (long)v * o.v % MOD; return new Mint((int)x); }
        static Mint addi(int a, int b){ int x = a + b; if (x >= MOD) x -= MOD; return new Mint(x); }
        static int addiInt(int a, int b){ int x = a + b; if (x >= MOD) x -= MOD; return x; }
        static int subiInt(int a, int b){ int x = a - b; if (x < 0) x += MOD; return x; }
        static int muliInt(int a, int b){ return (int)((long)a * b % MOD); }
        static int powi(int a, long e){
            long r = 1, x = a;
            while (e > 0) {
                if ((e & 1) == 1) r = r * x % MOD;
                x = x * x % MOD;
                e >>= 1;
            }
            return (int)r;
        }
        static int invi(int a){ return powi(a, MOD - 2L); }
        static Mint pow(Mint a, long e){ return new Mint(powi(a.v, e)); }
        static Mint inv(Mint a){ return new Mint(invi(a.v)); }
    }

    static final class NTT {
        int[] rev = new int[0];
        int[] roots = new int[]{0,1};

        void ensureBase(int nbase){
            int sz = roots.length;
            if (sz >= (1<<nbase)) return;
            int cur = Integer.numberOfTrailingZeros(sz);
            roots = Arrays.copyOf(roots, 1<<nbase);
            while (cur < nbase){
                int z = Mint.powi(G, (MOD-1)>>(cur+1));
                for (int i = 1<<(cur-1); i < (1<<cur); ++i){
                    roots[2*i] = roots[i];
                    roots[2*i+1] = (int)((long)roots[i]*z%MOD);
                }
                ++cur;
            }
        }
        void bitReverse(int n){
            if (rev.length == n) return;
            rev = new int[n];
            int lg = Integer.numberOfTrailingZeros(n);
            for (int i=1;i<n;i++) rev[i] = (rev[i>>1]>>1) | ((i&1)<<(lg-1));
        }
        void ntt(int[] a, boolean invert){
            int n = a.length;
            bitReverse(n);
            for (int i=0;i<n;i++) if (i < rev[i]) { int t=a[i]; a[i]=a[rev[i]]; a[rev[i]]=t; }
            for (int len=1, lvl=0; (len<<1) <= n; len<<=1, ++lvl){
                ensureBase(lvl+1);
                for (int i=0;i<n;i+=(len<<1)){
                    for (int j=0;j<len;j++){
                        int u = a[i+j];
                        int v = (int)((long)a[i+j+len] * roots[j+len] % MOD);
                        int x = u + v; if (x>=MOD) x-=MOD;
                        int y = u - v; if (y<0) y+=MOD;
                        a[i+j] = x;
                        a[i+j+len] = y;
                    }
                }
            }
            if (invert){
                for (int i=1, j=n-1; i<j; i++, j--){ int t=a[i]; a[i]=a[j]; a[j]=t; }
                int inv_n = Mint.invi(n);
                for (int i=0;i<n;i++) a[i] = (int)((long)a[i]*inv_n%MOD);
            }
        }
        int[] multiply(int[] A, int[] B){
            if (A.length==0 || B.length==0) return new int[0];
            int n1=A.length, n2=B.length;
            if (Math.min(n1,n2) < 32){
                int[] C = new int[n1+n2-1];
                for (int i=0;i<n1;i++){
                    long ai = A[i];
                    for (int j=0;j<n2;j++){
                        C[i+j] = (int)((C[i+j] + ai*B[j])%MOD);
                    }
                }
                return C;
            }
            int n=1; while (n < n1+n2-1) n<<=1;
            int[] fa = Arrays.copyOf(A, n);
            int[] fb = Arrays.copyOf(B, n);
            ntt(fa,false); ntt(fb,false);
            for (int i=0;i<n;i++) fa[i] = (int)((long)fa[i]*fb[i]%MOD);
            ntt(fa,true);
            return Arrays.copyOf(fa, n1+n2-1);
        }
    }
    static final NTT ntt = new NTT();

    static int[] trim(int[] a){
        int r = a.length;
        while (r>0 && a[r-1]==0) r--;
        return Arrays.copyOf(a, r);
    }
    static int[] deriv(int[] a){
        int n=a.length; if (n==0) return new int[0];
        int[] d = new int[Math.max(0,n-1)];
        for (int i=1;i<n;i++) d[i-1] = (int)((long)a[i]*i%MOD);
        return d;
    }

    static int[] polyInv(int[] A, int m){
        int[] B = new int[1];
        B[0] = Mint.invi(A[0]);
        int k=1;
        while (k<m){
            k<<=1;
            int take = Math.min(A.length, k);
            int[] a = Arrays.copyOf(A, take);
            int[] t = ntt.multiply(ntt.multiply(B,B), a);
            B = Arrays.copyOf(B, k);
            for (int i=0;i<k;i++){
                int val = (i<t.length? t[i] : 0);
                int twoB = B[i] + B[i]; if (twoB>=MOD) twoB-=MOD;
                B[i] = Mint.subiInt(twoB, val);
            }
            B = trim(B);
            if (B.length > k) B = Arrays.copyOf(B, k);
        }
        if (B.length>m) B = Arrays.copyOf(B, m);
        else if (B.length<m){ int[] nb=new int[m]; System.arraycopy(B,0,nb,0,B.length); B=nb; }
        return B;
    }

    static int[] polyMod(int[] a, int[] b){
        a = trim(a); b = trim(b);
        int n=a.length, m=b.length;
        if (n < m) return a;
        int[] ar = new int[n];
        for (int i=0;i<n;i++) ar[i]=a[n-1-i];
        int[] br = new int[m];
        for (int i=0;i<m;i++) br[i]=b[m-1-i];
        int k = n - m + 1;
        int[] brInv = polyInv(br, k);
        int[] q = ntt.multiply(ar, brInv);
        if (q.length > k) q = Arrays.copyOf(q, k);
        for (int i=0;i<q.length/2;i++){ int t=q[i]; q[i]=q[q.length-1-i]; q[q.length-1-i]=t; }
        int[] qb = ntt.multiply(q, b);
        if (qb.length < n){ qb = Arrays.copyOf(qb, n); }
        int[] r = new int[n];
        for (int i=0;i<n;i++) r[i] = Mint.subiInt(a[i], qb[i]);
        r = trim(r);
        if (r.length >= m) r = Arrays.copyOf(r, m-1);
        return r;
    }

    static final class MultiEval {
        ArrayList<int[]> tree = new ArrayList<>();
        int[] pts;
        int n;
        MultiEval(){}
        MultiEval(int[] F, int[] points, int[] out){ build(F, points, out); }
        void build(int[] F, int[] points, int[] out){
            pts = points; n = pts.length;
            if (n==0) return;
            tree.clear(); tree.ensureCapacity(4*n);
            for (int i=0;i<4*n;i++) tree.add(null);
            buildTree(1,0,n-1);
            dfs(1,0,n-1, F, out);
        }
        void buildTree(int v,int l,int r){
            if (l==r){
                tree.set(v, new int[]{ Mint.subiInt(0, pts[l]), 1 });
                return;
            }
            int m=(l+r)>>1;
            buildTree(v<<1,l,m);
            buildTree(v<<1|1,m+1,r);
            tree.set(v, ntt.multiply(tree.get(v<<1), tree.get(v<<1|1)));
        }
        void dfs(int v,int l,int r, int[] F, int[] out){
            F = trim(F);
            if (F.length==0){
                if (l==r) out[l]=0;
                return;
            }
            if (l==r){
                long val=0, x=pts[l];
                for (int i=F.length-1;i>=0;--i) val = (val*x + F[i])%MOD;
                out[l]=(int)val;
                return;
            }
            int[] L = polyMod(F, tree.get(v<<1));
            int[] R = polyMod(F, tree.get(v<<1|1));
            int m=(l+r)>>1;
            dfs(v<<1,l,m,L,out);
            dfs(v<<1|1,m+1,r,R,out);
        }
    }

    static int[] buildQ(int l,int r, int[] S){
        if (l==r) return new int[]{ S[l], 1 };
        int m=(l+r)>>1;
        int[] L = buildQ(l,m,S);
        int[] R = buildQ(m+1,r,S);
        return ntt.multiply(L,R);
    }

    public static void main(String[] args) throws Exception {
        FastScanner fs = new FastScanner(System.in);
        int N = fs.nextInt();
        int M = fs.nextInt();

        int[] S = new int[N];
        int sumS = 0;
        for (int i=0;i<N;i++){ int x = fs.nextInt(); x%=MOD; if(x<0)x+=MOD; S[i]=x; sumS = Mint.addiInt(sumS, S[i]); }

        int[] Q = buildQ(0,N-1,S);
        int[] dQ = deriv(Q);

        final class Op { int type; int X; Op(int t,int x){type=t;X=x;} }
        Op[] ops = new Op[M];
        for (int i=0;i<M;i++){
            int t = fs.nextInt();
            if (t==1){ int X = fs.nextInt()%MOD; if (X<0) X+=MOD; ops[i]=new Op(1,X); }
            else ops[i]=new Op(2,0);
        }

        int A=1,B=0,C=0,D=1;
        int[] storeA = new int[M], storeB = new int[M], storeC = new int[M], storeD = new int[M];
        int[] mode = new int[M];
        ArrayList<Integer> Ylist = new ArrayList<>();
        for (int i=0;i<M;i++){
            if (ops[i].type==1){
                int X = ops[i].X;
                A = Mint.addiInt(A, (int)((long)X*C%MOD));
                B = Mint.addiInt(B, (int)((long)X*D%MOD));
            }else{
                int tA=A; A=C; C=tA;
                int tB=B; B=D; D=tB;
            }
            storeA[i]=A; storeB[i]=B; storeC[i]=C; storeD[i]=D;
            if (C==0){
                mode[i]=0;
            }else{
                mode[i]=1;
                int y = (int)((long)D * Mint.invi(C) % MOD);
                Ylist.add(y);
            }
        }

        int K = Ylist.size();
        int[] Y = new int[K];
        for (int i=0;i<K;i++) Y[i]=Ylist.get(i);

        int[] QY = new int[K], dQY = new int[K];
        if (K>0){
            new MultiEval().build(Q, Y, QY);
            new MultiEval().build(dQ, Y, dQY);
        }

        int[] Rvals = new int[K];
        for (int i=0;i<K;i++){
            Rvals[i] = (int)((long)dQY[i] * Mint.invi(QY[i]) % MOD);
        }

        StringBuilder out = new StringBuilder();
        int ptr=0;
        int nN = N % MOD;
        for (int i=0;i<M;i++){
            int a=storeA[i], b=storeB[i], c=storeC[i], d=storeD[i];
            int res;
            if (c==0){
                int invD = Mint.invi(d);
                int term1 = (int)((long)a * invD % MOD * sumS % MOD);
                int term2 = (int)((long)b * invD % MOD * nN % MOD);
                res = term1 + term2; if (res>=MOD) res-=MOD;
            }else{
                int invC = Mint.invi(c);
                int H = (int)((long)invC * Rvals[ptr++] % MOD);
                int part = (int)((long)d * H % MOD);
                int inner = nN - part; if (inner<0) inner+=MOD;
                int t1 = (int)((long)a * invC % MOD * inner % MOD);
                int t2 = (int)((long)b * H % MOD);
                res = t1 + t2; if (res>=MOD) res-=MOD;
            }
            out.append(res).append('\n');
        }
        System.out.print(out.toString());
    }
}