import sys

def is_power_of_two(x: int) -> bool:
    return x > 0 and (x & (x - 1)) == 0

def solve_case(N: int) -> str:
    if N & 1 or is_power_of_two(N):
        return "-1"
    low = N & -N
    k = low // 2
    A = N + k
    B = N // 2
    C = B - k
    return f"{A} {B} {C}"

def main():
    data = list(map(int, sys.stdin.read().strip().split()))
    it = iter(data)
    T = next(it)
    out = []
    for _ in range(T):
        N = next(it)
        out.append(solve_case(N))
    print("\n".join(out))

if __name__ == "__main__":
    main()