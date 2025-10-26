import sys
from decimal import Decimal, getcontext, ROUND_HALF_UP

data = list(map(int, sys.stdin.buffer.read().split()))
it = iter(data)
N = next(it)
K = next(it)
A = [next(it) for _ in range(N)]

basis = [0]*10
for x in A:
    v = x
    for b in range(9, -1, -1):
        if (v >> b) & 1:
            if basis[b]:
                v ^= basis[b]
            else:
                basis[b] = v
                break

vecs = [v for v in basis if v]
r = len(vecs)

xors = [0]
for v in vecs:
    xors += [x ^ v for x in xors]

if K == 0:
    s = len(xors)
else:
    s = 0
    for x in xors:
        s += x ** K

getcontext().prec = 50
val = (Decimal(s) / Decimal(1 << r)).quantize(Decimal("0.01"), rounding=ROUND_HALF_UP)
print(f"{val:.2f}")