#include <bits/stdc++.h>
using namespace std;

static inline int norm(long long x, int n){ x%=n; if(x<0) x+=n; return (int)x; }

bool feasible(const vector<int>& pos, int n, int R, int orient) {
    vector<int> diff(n+1, 0);
    auto add_interval = [&](int L, int Rr){
        if (L <= Rr) {
            diff[L]++; diff[Rr+1]--;
        } else {
            diff[0]++; diff[Rr+1]--;
            diff[L]++; diff[n]--;
        }
    };
    for (int v = 0; v < n; ++v) {
        long long a = (long long)pos[v] - R + (long long)orient * v;
        long long b = (long long)pos[v] + R + (long long)orient * v;
        int L = norm(a, n);
        int Rr = norm(b, n);
        add_interval(L, Rr);
    }
    int cur = 0;
    for (int k = 0; k < n; ++k) {
        cur += diff[k];
        if (cur == n) return true;
    }
    return false;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int N; 
    if(!(cin >> N)) return 0;
    vector<int> p(N);
    for (int i = 0; i < N; ++i) cin >> p[i], --p[i];
    vector<int> pos(N);
    for (int i = 0; i < N; ++i) pos[p[i]] = i;

    int lo = 0, hi = N/2;
    while (lo < hi) {
        int mid = (lo + hi) >> 1;
        bool ok = feasible(pos, N, mid, -1) || feasible(pos, N, mid, +1);
        if (ok) hi = mid; else lo = mid + 1;
    }
    cout << lo << "\n";
    return 0;
}