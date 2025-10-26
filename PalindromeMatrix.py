import sys

sys.setrecursionlimit(10**7)

class DSU:
    def __init__(self, n):
        self.p = list(range(n))
        self.sz = [1]*n
    def find(self, a):
        while a != self.p[a]:
            self.p[a] = self.p[self.p[a]]
            a = self.p[a]
        return a
    def union(self, a, b):
        ra, rb = self.find(a), self.find(b)
        if ra == rb: return
        if self.sz[ra] < self.sz[rb]:
            ra, rb = rb, ra
        self.p[rb] = ra
        self.sz[ra] += self.sz[rb]

def solve():
    input = sys.stdin.readline
    n, m = map(int, input().split())
    grid = [list(input().strip()) for _ in range(n)]

    total = n * m
    dsu = DSU(total)
    is_digit = [False]*total
    value = [0]*total

    for i in range(n):
        for j in range(m):
            if grid[i][j] != '.':
                idx = i*m + j
                is_digit[idx] = True
                value[idx] = int(grid[i][j])

    # unir por filas
    for i in range(n):
        start = 0
        while start < m:
            while start < m and grid[i][start] == '.':
                start += 1
            if start >= m: break
            end = start
            while end < m and grid[i][end] != '.':
                end += 1
            L = end - start
            for k in range(L // 2):
                a = i*m + (start + k)
                b = i*m + (start + (L-1-k))
                dsu.union(a, b)
            start = end

    # unir por columnas
    for j in range(m):
        start = 0
        while start < n:
            while start < n and grid[start][j] == '.':
                start += 1
            if start >= n: break
            end = start
            while end < n and grid[end][j] != '.':
                end += 1
            L = end - start
            for k in range(L // 2):
                a = (start + k)*m + j
                b = (start + (L-1-k))*m + j
                dsu.union(a, b)
            start = end

    # agrupar por componente
    comps = {}
    for i in range(n):
        for j in range(m):
            idx = i*m + j
            if not is_digit[idx]:
                continue
            root = dsu.find(idx)
            comps.setdefault(root, []).append(idx)

    # asignar valores óptimos
    chosen = {}
    for root, cells in comps.items():
        digits = [value[c] for c in cells]
        best_v, best_cost = 0, float('inf')
        for v in range(10):
            cost = sum(abs(d - v) for d in digits)
            if cost < best_cost or (cost == best_cost and v < best_v):
                best_cost = cost
                best_v = v
        for c in cells:
            chosen[c] = best_v

    # construir salida
    for i in range(n):
        row = []
        for j in range(m):
            idx = i*m + j
            if grid[i][j] == '.':
                row.append('.')
            else:
                row.append(str(chosen[idx]))
        print("".join(row))

if __name__ == "__main__":
    solve()