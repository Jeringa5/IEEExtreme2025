import sys

def main():
    datos = list(map(int, sys.stdin.read().strip().split()))
    if not datos:
        return

    it = iter(datos)
    casos = next(it)
    salida = []

    for _ in range(casos):
        s_val = next(it)
        n_val = next(it)
        salida.append(str(calcular_parametro(s_val, n_val)))

    print("\n".join(salida))
    

def calcular_parametro(valor_s: int, valor_n: int) -> int:
    inicio = (valor_n + 1) // 2
    momento2 = 0
    momento4 = 0

    for idx in range(inicio, valor_n + 1):
        coef = comb[idx][valor_n - idx]
        momento2 += coef << (valor_n - idx)
        momento4 += coef << (2 * valor_n - 2 * idx)

    return redondear_par(19 * valor_s * momento4, momento2 * momento2)
    
def redondear_par(numerador: int, denominador: int) -> int:
    cociente, resto = divmod(numerador, denominador)
    doble_resto = resto << 1

    if doble_resto < denominador:
        return cociente
    if doble_resto > denominador:
        return cociente + 1
    return cociente if (cociente & 1) == 0 else cociente + 1

MAX_SIZE = 100

comb = [[0] * (MAX_SIZE + 1) for _ in range(MAX_SIZE + 1)]
for f in range(MAX_SIZE + 1):
    comb[f][0] = 1
    for c in range(1, f + 1):
        comb[f][c] = comb[f - 1][c - 1] + comb[f - 1][c] if c < f else 1


if __name__ == "__main__":
    main()