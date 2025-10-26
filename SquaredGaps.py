import sys

i64 = int
NEG_INF = -4000000000000000000

class Line:
    def __init__(self, m: i64, b: i64):
        self.m = m
        self.b = b
    def eval(self, x: i64) -> i64:
        return self.m * x + self.b

class MonoCHT:
    def __init__(self):
        self.st = []
        self.head = 0
    def clear(self):
        self.st.clear()
        self.head = 0
    def add(self, m: i64, b: i64):
        self.st.append(Line(m, b))
    def query(self, x: i64) -> i64:
        while self.head + 1 < len(self.st) and self.st[self.head + 1].eval(x) >= self.st[self.head].eval(x):
            self.head += 1
        return self.st[self.head].eval(x)
    def empty(self) -> bool:
        return len(self.st) == 0

data = sys.stdin.buffer.read().split()
it = iter(data)

n = int(next(it))
a = next(it).decode()
m = int(next(it))
b = next(it).decode()

MATCH = int(next(it))
MISMATCH = int(next(it))
GAP = int(next(it))

def score(x, y):
    return MATCH if x == y else MISMATCH
def sq(x):
    return x * x

A_prev = [NEG_INF] * (m + 1)
V_prev = [NEG_INF] * (m + 1)
H_prev = [NEG_INF] * (m + 1)
A_cur  = [NEG_INF] * (m + 1)
V_cur  = [NEG_INF] * (m + 1)
H_cur  = [NEG_INF] * (m + 1)

A_prev[0] = 0
for j in range(1, m + 1):
    H_prev[j] = sq(j) * GAP

col = [MonoCHT() for _ in range(m + 1)]
for j in range(0, m + 1):
    base = max(A_prev[j], H_prev[j])
    col[j].add(0, base)

GAP_neg = (GAP < 0)

for i in range(1, n + 1):
    for arr in (A_cur, V_cur, H_cur):
        for t in range(len(arr)):
            arr[t] = NEG_INF

    rowHull = MonoCHT()
    rowHull.clear()

    V_cur[0] = col[0].query(i) + sq(i) * GAP
    base = max(A_cur[0], V_cur[0])
    rowHull.add(0, base)

    if not GAP_neg:
        for j in range(1, m + 1):
            prevBest = max(A_prev[j-1], V_prev[j-1], H_prev[j-1])
            if prevBest != NEG_INF:
                A_cur[j] = prevBest + score(a[i-1], b[j-1])

            V_cur[j] = col[j].query(i) + sq(i) * GAP
            H_cur[j] = rowHull.query(j) + sq(j) * GAP

            baseH = max(A_cur[j], V_cur[j])
            slope_row = -2 * GAP * j
            inter_row = baseH + sq(j) * GAP
            rowHull.add(slope_row, inter_row)
    else:
        for jj in range(m, 0, -1):
            j = jj
            prevBest = max(A_prev[j-1], V_prev[j-1], H_prev[j-1])
            if prevBest != NEG_INF:
                A_cur[j] = prevBest + score(a[i-1], b[j-1])

            V_cur[j] = col[j].query(i) + sq(i) * GAP
            H_cur[j] = rowHull.query(j) + sq(j) * GAP

            baseH = max(A_cur[j], V_cur[j])
            slope_row = -2 * GAP * j
            inter_row = baseH + sq(j) * GAP
            rowHull.add(slope_row, inter_row)

    slope_col = -2 * GAP * i
    add_const = sq(i) * GAP
    if not GAP_neg:
        for j in range(0, m + 1):
            baseV = max(A_cur[j], H_cur[j])
            col[j].add(slope_col, baseV + add_const)
    else:
        for j in range(m, -1, -1):
            baseV = max(A_cur[j], H_cur[j])
            col[j].add(slope_col, baseV + add_const)

    A_prev, A_cur = A_cur, A_prev
    V_prev, V_cur = V_cur, V_prev
    H_prev, H_cur = H_cur, H_prev

ans = max(A_prev[m], V_prev[m], H_prev[m])
print(ans)