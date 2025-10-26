#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int N, M;
    if (!(cin >> N >> M)) return 0;

    vector<long long> a((size_t)N * M);
    for (int i = 0; i < N; ++i) {
        for (int j = 0; j < M; ++j) {
            cin >> a[(size_t)i * M + j];
        }
    }

    vector<tuple<long long,int,int>> cells;
    cells.reserve((size_t)N * M);
    for (int i = 0; i < N; ++i)
        for (int j = 0; j < M; ++j)
            cells.emplace_back(a[(size_t)i * M + j], i, j);

    sort(cells.begin(), cells.end(),
         [](const auto& x, const auto& y){
             if (get<0>(x) != get<0>(y)) return get<0>(x) < get<0>(y);
             if (get<1>(x) != get<1>(y)) return get<1>(x) < get<1>(y);
             return get<2>(x) < get<2>(y);
         });

    vector<int> dp((size_t)N * M, 1);
    int ans = 1;
    const int di[4] = {-1, 1, 0, 0};
    const int dj[4] = {0, 0, -1, 1};

    for (const auto& t : cells) {
        long long val; int i, j;
        tie(val, i, j) = t;
        int idx = i * M + j;

        int best = 1;
        for (int d = 0; d < 4; ++d) {
            int ni = i + di[d], nj = j + dj[d];
            if (ni < 0 || ni >= N || nj < 0 || nj >= M) continue;
            int nidx = ni * M + nj;
            if (a[nidx] < val) best = max(best, dp[nidx] + 1);
        }
        dp[idx] = best;
        if (best > ans) ans = best;
    }

    cout << ans << '\n';
    return 0;
}