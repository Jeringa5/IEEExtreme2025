import sys
import heapq

class DSU:
    __slots__ = ("p", "sz")
    def __init__(self, n):
        self.p = list(range(n + 1))
        self.sz = [1] * (n + 1)
    def find(self, x):
        while self.p[x] != x:
            self.p[x] = self.p[self.p[x]]
            x = self.p[x]
        return x
    def union(self, a, b):
        pa, pb = self.find(a), self.find(b)
        if pa == pb:
            return
        if self.sz[pa] < self.sz[pb]:
            pa, pb = pb, pa
        self.p[pb] = pa
        self.sz[pa] += self.sz[pb]

def solve_case(N, M, edges):
    edges_by_risk = sorted(edges, key=lambda x: x[3])
    dsu = DSU(N)
    Rstar = None
    for u, v, w, r in edges_by_risk:
        dsu.union(u, v)
        if dsu.find(1) == dsu.find(N):
            Rstar = r
            break
    if Rstar is None:
        return "-1"

    adj = [[] for _ in range(N + 1)]
    for u, v, w, r in edges:
        if r <= Rstar:
            adj[u].append((v, w))
            adj[v].append((u, w))

    INF = 10**30
    dist = [INF] * (N + 1)
    dist[1] = 0
    pq = [(0, 1)]
    heappop = heapq.heappop
    heappush = heapq.heappush

    while pq:
        d, u = heappop(pq)
        if d != dist[u]:
            continue
        if u == N:
            return f"{Rstar} {d}"
        for v, w in adj[u]:
            nd = d + w
            if nd < dist[v]:
                dist[v] = nd
                heappush(pq, (nd, v))

    return "-1"

def main():
    data = list(map(int, sys.stdin.buffer.read().split()))
    it = iter(data)
    T = next(it)
    out = []
    for _ in range(T):
        N = next(it)
        M = next(it)
        edges = []
        for _ in range(M):
            u = next(it); v = next(it); w = next(it); r = next(it)
            edges.append((u, v, w, r))
        out.append(solve_case(N, M, edges))
    sys.stdout.write("\n".join(out))

if __name__ == "__main__":
    main()