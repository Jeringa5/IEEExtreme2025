import java.io.*;
import java.util.*;

public class EDPS {
    static int N, maxD;
    static int[] cnt;
    static String ans = null;
    static ArrayDeque<Integer> st = new ArrayDeque<>();
    static StringBuilder cur = new StringBuilder();

    static boolean existsInRange(int L, int R){
        L = Math.max(L, 0);
        R = Math.min(R, cnt.length - 1);
        if (R < L) return false;
        for (int d = L; d <= R; ++d) if (cnt[d] > 0) return true;
        return false;
    }

    static boolean dfs(int pos, int opensUsed){
        if (pos == 2 * N){
            if (st.isEmpty()){
                for (int x : cnt) if (x != 0) return false;
                ans = cur.toString();
                return true;
            }
            return false;
        }
        int rem = 2 * N - pos;
        int openCnt = st.size();

        for (int p : st){
            int minD = pos - p - 1;
            int maxDh = 2 * N - p - 2;
            if (!existsInRange(minD, maxDh)) return false;
        }

        if (!st.isEmpty()){
            int p = st.peekLast();
            int d = pos - p - 1;
            if (0 <= d && d <= maxD && cnt[d] > 0){
                cur.append(')');
                st.pollLast();
                --cnt[d];
                if (dfs(pos + 1, opensUsed)) return true;
                ++cnt[d];
                st.addLast(p);
                cur.setLength(cur.length() - 1);
            }
        }

        if (opensUsed < N && rem >= openCnt + 2){
            if (existsInRange(0, 2 * N - pos - 2)){
                cur.append('(');
                st.addLast(pos);
                if (dfs(pos + 1, opensUsed + 1)) return true;
                st.pollLast();
                cur.setLength(cur.length() - 1);
            }
        }
        return false;
    }

    public static void main(String[] args) throws Exception{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer stt = new StringTokenizer(br.readLine());
        N = Integer.parseInt(stt.nextToken());
        int[] A = new int[N];
        stt = new StringTokenizer(br.readLine());
        for (int i = 0; i < N; ++i) A[i] = Integer.parseInt(stt.nextToken());
        maxD = 2 * N - 2;
        cnt = new int[Math.max(1, maxD + 1)];
        for (int a : A){
            if (a < 0 || a > maxD){
                System.out.println("No");
                return;
            }
            ++cnt[a];
        }
        if (dfs(0, 0)){
            System.out.println("Yes");
            System.out.println(ans);
        } else {
            System.out.println("No");
        }
    }
}