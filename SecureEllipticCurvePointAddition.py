import sys

def suma_puntos(a: int, b: int, p: int, P, Q):
    if P is None:
        return Q
    if Q is None:
        return P

    x1, y1 = P
    x2, y2 = Q

    if x1==x2 and (y1+y2)%p==0:
        return None

    if x1==x2 and y1==y2:
        if y1%p==0:
            return None
        numerador = (3*x1*x1+a)%p
        denominador = (2*y1) % p
        lamda = (numerador*inversoMod(denominador, p))%p 
    else:
        numerador = (y2-y1)%p
        denominador = (x2-x1)%p
        lamda = (numerador*inversoMod(denominador, p))%p
    x3=(lamda*lamda-x1-x2)%p
    y3=(lamda*(x1-x3)-y1)%p
    return (x3,y3)
    
def inversoMod(a,p):
    return pow(a, p-2, p)
    
def main():
    data = list(map(int, sys.stdin.read().strip().split()))
    it = iter(data)
    T = next(it)
        
    out = []
    for _ in range(T):
        a = next(it); b = next(it); p = next(it)
        x1 = next(it); y1 = next(it)
        x2 = next(it); y2 = next(it)

        R = suma_puntos(a, b, p, (x1, y1), (x2, y2))
        if R is None:
            out.append("POINT_AT_INFINITY")
        else:
            out.append(f"{R[0]} {R[1]}")

    print("\n".join(out))

if __name__ == "__main__":
    main()