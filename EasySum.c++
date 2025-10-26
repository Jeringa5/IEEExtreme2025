#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    
    int N, K;
    if (!(cin >> N >> K)) return 0;
    vector<int> A(N+1);
    int mx = 0;
    for (int i = 1; i <= N; ++i) {
        cin >> A[i];
        mx = max(mx, A[i]);
    }
    int maxB = (mx > 0) ? (31 - __builtin_clz(mx)) : 0;

    vector<long long> sufIdx(N + 2, 0);
    for (int i = N; i >= 1; --i) sufIdx[i] = sufIdx[i+1] + i;

    vector<long long> ans(K + 1, 0);
    vector<long long> prevF(K + 1, 0);
    vector<unsigned char> B(N + 1, 0);

    for (int b = maxB; b >= 0; --b) {
        int T = 1 << b;
        for (int i = 1; i <= N; ++i) B[i] = (A[i] >= T);

        for (int k = 1; k <= K; ++k) {
            long long sumLen = 0;
            int r = 1, cnt = 0;
            for (int l = 1; l <= N; ++l) {
                while (r <= N && cnt < k) { cnt += B[r]; ++r; }
                if (cnt < k) break;
                int r0 = r - 1;
                long long ends = (long long)N - r0 + 1;
                sumLen += sufIdx[r0] - (long long)(l - 1) * ends;
                cnt -= B[l];
            }
            long long currF = sumLen;
            ans[k] += (long long)b * (currF - prevF[k]);
            prevF[k] = currF;
        }
    }

    for (int k = 1; k <= K; ++k) {
        if (k > 1) cout << ' ';
        cout << ans[k];
    }
    cout << '\n';
    return 0;
}