import sys
from math import gcd
from array import array

buf = sys.stdin.buffer.read()
nb = len(buf)
i = 0
def nxt():
    global i
    b = buf; n = nb; j = i
    while j < n and b[j] <= 32: j += 1
    if j >= n:
        i = j
        return None
    s = 1
    if b[j] == 45:  # '-'
        s = -1; j += 1
    x = 0
    while j < n:
        c = b[j]
        if 48 <= c <= 57:
            x = x*10 + (c - 48); j += 1
        else:
            break
    i = j
    return s*x

N = nxt(); k = nxt()

C = array('I', [0]) * N

g = 0
minC = 1 << 62
for idx in range(1, N):
    ci = nxt()
    C[idx] = ci
    g = gcd(g, ci)
    if ci < minC:
        minC = ci
g *= 2

Q = nxt() or 0

YES = b"Yes\n"; NO = b"No\n"
out = bytearray()
append = out.extend
flush_threshold = 1 << 20  # 1 MiB

if N == 1:
    for _ in range(Q):
        a = nxt(); b = nxt()
        ok = (k == 0 and a == 0 and b == 0)
        append(YES if ok else NO)
        if len(out) >= flush_threshold:
            sys.stdout.buffer.write(out); out.clear()
    if out: sys.stdout.buffer.write(out)
    raise SystemExit

for _ in range(Q):
    a = nxt(); b = nxt()
    if a == 0 and b == 0:
        base = 2 * minC
    elif a == 0:
        base = C[b]
    elif b == 0:
        base = C[a]
    elif a == b:
        base = 2 * C[a]
    else:
        base = C[a] + C[b]

    ok = (k >= base) and ((k - base) % g == 0)
    append(YES if ok else NO)

    if len(out) >= flush_threshold:
        sys.stdout.buffer.write(out); out.clear()

if out:
    sys.stdout.buffer.write(out)