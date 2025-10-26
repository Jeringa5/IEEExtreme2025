import java.io.*;
import java.util.*;

public class VisitEgypt {
    static final int MOD = 1_000_000_007;

    static class Query {
        long N, M, A;
        Query(long N, long M, long A) {
            this.N = N;
            this.M = M;
            this.A = A;
        }
    }

    static String winner(long N, int M, int A, int[] dp) {
        int realv = dp[(int)(212L * N)];

        if (M == realv && A == realv) {
            return "TIE";
        }
        long dM = Math.abs((long)realv - M);
        long dA = Math.abs((long)realv - A);

        if (dM == dA) {
            return "NONE";
        }
        return (dM < dA) ? "Mikel" : "Andrew";
    }

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)));

        int T = Integer.parseInt(br.readLine());
        Query[] qs = new Query[T];

        long maxN = 0;
        for (int i = 0; i < T; i++) {
            String[] parts = br.readLine().split(" ");
            long N = Long.parseLong(parts[0]);
            long M = Long.parseLong(parts[1]) % MOD;
            long A = Long.parseLong(parts[2]) % MOD;
            qs[i] = new Query(N, M, A);
            if (N > maxN) maxN = N;
        }

        int Smax = (int)(212L * maxN);

        int[] dp = new int[Smax + 1];
        dp[0] = 1;

        for (int s = 1; s <= Smax; s++) {
            long ways = 0;
            if (s >= 1)   ways += dp[s - 1];
            if (s >= 2)   ways += dp[s - 2];
            if (s >= 4)   ways += dp[s - 4];
            if (s >= 20)  ways += dp[s - 20];
            if (s >= 40)  ways += dp[s - 40];
            if (s >= 80)  ways += dp[s - 80];
            if (s >= 200) ways += dp[s - 200];
            if (s >= 400) ways += dp[s - 400];
            if (s >= 800) ways += dp[s - 800];
            dp[s] = (int)(ways % MOD);
        }

        for (Query q : qs) {
            out.println(winner(q.N, (int)q.M, (int)q.A, dp));
        }

        out.flush();
    }
}
