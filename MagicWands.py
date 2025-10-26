import sys

def min_flips(N, K ,S):
    S = list(S)
    flip_effect = [0] * (N+1)
    flip_count = 0
    flips = 0
    for i in range(N):
        flip_count += flip_effect[i]
        current = S[i]
        if flip_count % 2 == 1:
            current = 'H' if current == 'S' else 'S'

        if current == 'S':
            if i + K > N:
                return -1
            flips += 1
            flip_count += 1
            flip_effect[i + K] -= 1

    return flips




def main():
    data = sys.stdin.read().strip().split()
    it = iter(data)
    T = int(next(it))
    results = []

    for _ in range(T):
        N = int(next(it))
        K = int(next(it))
        S = next(it)
        res = min_flips(N, K, S)
        results.append(str(res))

    sys.stdout.write("\n".join(results))


if __name__ == "__main__":
    main()