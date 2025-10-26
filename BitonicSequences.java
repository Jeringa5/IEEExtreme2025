import java.io.*;
import java.util.*;

public class BitonicSequences {

    static final int MOD = 1_000_000_007;

    static void generarDatosTriangulares(int limite, List<Integer> numsTri, List<Integer> signosTri, List<Integer> coefTri) {
        numsTri.clear();
        signosTri.clear();
        coefTri.clear();

        for (int k = 1; ; k++) {
            long triang = (long) k * (k + 1) / 2;
            if (triang > limite) break;

            numsTri.add((int) triang);

            int signo = ((k & 1) == 1) ? 1 : -1;
            signosTri.add(signo);
            coefTri.add(signo * (2 * k + 1));
        }
    }

    static int[] generarCoefPentagonales(int limite) {
        int[] pentCoef = new int[limite + 1];

        for (int k = 1; ; k++) {
            long pNeg = (long) k * (3L * k - 1) / 2;
            long pPos = (long) k * (3L * k + 1) / 2;
            if (pNeg > limite && pPos > limite) break;

            int valor = ((k & 1) == 1) ? -1 : 1;
            if (pNeg <= limite) pentCoef[(int) pNeg] = valor;
            if (pPos <= limite) pentCoef[(int) pPos] = valor;
        }
        return pentCoef;
    }

    public static void main(String[] args) throws Exception {
        FastScanner fs = new FastScanner(System.in);
        int limite = fs.nextInt();
        if (limite == Integer.MIN_VALUE) return;

        ArrayList<Integer> numsTriG = new ArrayList<>();
        ArrayList<Integer> signosTriG = new ArrayList<>();
        ArrayList<Integer> coefTriG   = new ArrayList<>();
        ArrayList<Integer> numsTriH = new ArrayList<>();
        ArrayList<Integer> signosTriH = new ArrayList<>();
        ArrayList<Integer> coefTriH   = new ArrayList<>();

        generarDatosTriangulares(limite, numsTriG, signosTriG, coefTriG);
        generarDatosTriangulares(limite, numsTriH, signosTriH, coefTriH);

        int[] coefPent = generarCoefPentagonales(limite);

        int[] serieA = new int[limite + 1];
        serieA[0] = 1;

        for (int n = 1; n <= limite; n++) {
            long suma = coefPent[n];
            for (int idx = 0; idx < numsTriH.size() && numsTriH.get(idx) <= n; idx++) {
                int t = numsTriH.get(idx);
                long contrib = 1L * coefTriH.get(idx) * serieA[n - t];
                suma += contrib;
            }
            suma %= MOD;
            if (suma < 0) suma += MOD;
            serieA[n] = (int) suma;
        }

        int[] serieU = new int[limite + 1];
        for (int n = 1; n <= limite; n++) {
            long acumulado = 0;
            for (int d = 0; d < numsTriG.size() && numsTriG.get(d) <= n; d++) {
                int t = numsTriG.get(d);
                long contrib = 1L * signosTriG.get(d) * serieA[n - t];
                acumulado += contrib;
            }
            acumulado %= MOD;
            if (acumulado < 0) acumulado += MOD;
            serieU[n] = (int) acumulado;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= limite; i++) {
            if (i > 1) sb.append(' ');
            sb.append(serieU[i]);
        }
        sb.append('\n');
        System.out.print(sb.toString());
    }

    // ---- FastScanner ----
    static class FastScanner {
        private final InputStream in;
        private final byte[] buffer = new byte[1 << 16];
        private int ptr = 0, len = 0;

        FastScanner(InputStream is) { this.in = is; }

        private int read() throws IOException {
            if (ptr >= len) {
                len = in.read(buffer);
                ptr = 0;
                if (len <= 0) return -1;
            }
            return buffer[ptr++];
        }

        int nextInt() throws IOException {
            int c = read();
            while (c <= 32) {
                if (c == -1) return Integer.MIN_VALUE;
                c = read();
            }
            int sgn = 1;
            if (c == '-') { sgn = -1; c = read(); }
            int x = 0;
            while (c > 32) {
                x = x * 10 + (c - '0');
                c = read();
            }
            return x * sgn;
        }
    }
}