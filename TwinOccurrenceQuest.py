import sys

def primOcur(A, x):
    izq, der = 0, len(A) - 1
    ans = -1
    while izq <= der:
        mid = (izq + der) // 2
        if A[mid] == x:
            ans = mid
            der = mid - 1
        elif A[mid] < x:
            izq = mid + 1
        else:
            der = mid - 1
    return ans

def ultOcur(A, x):
    izq, der = 0, len(A) - 1
    ans = -1
    while izq <= der:
        mid = (izq + der) // 2
        if A[mid] == x:
            ans = mid
            izq = mid + 1
        elif A[mid] < x:
            izq = mid + 1
        else:
            der = mid - 1
    return ans

def main():
    data = list(map(int, sys.stdin.read().strip().split()))
    it = iter(data)

    N = next(it); Q = next(it)
    A = [next(it) for _ in range(N)]

    outputs = []
    for _ in range(Q):
        x = next(it)
        first = primOcur(A, x)
        if first == -1:
            outputs.append("-1 -1")
        else:
            last = ultOcur(A, x)
            outputs.append(f"{first+1} {last+1}")

    print("\n".join(outputs))

if __name__ == "__main__":
    main()