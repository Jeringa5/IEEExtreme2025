#include <bits/stdc++.h>
using namespace std;

using u64 = uint64_t;
static inline u64 mix(u64 x){
    x += 0x9e3779b97f4a7c15ULL;
    x = (x ^ (x >> 30)) * 0xbf58476d1ce4e5b9ULL;
    x = (x ^ (x >> 27)) * 0x94d049bb133111ebULL;
    x ^= (x >> 31);
    return x;
}
static inline u64 combinar(u64 a, u64 b){
    return mix(a * 146527ULL + b * 19260817ULL + 3721ULL);
}

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    
    int N;
    if(!(cin >> N)) return 0;
    vector<vector<int>> grafo(N+1);
    for(int i=0;i<N-1;i++){
        int a,b; cin >> a >> b;
        grafo[a].push_back(b);
        grafo[b].push_back(a);
    }

    vector<int> padre(N+1,0), orden; orden.reserve(N);
    vector<int> pila; pila.reserve(N); 
    pila.push_back(1); padre[1] = -1;
    while(!pila.empty()){
        int u = pila.back(); pila.pop_back();
        orden.push_back(u);
        for(int v: grafo[u]) if(v!=padre[u]){
            padre[v]=u; pila.push_back(v);
        }
    }

    vector<u64> suma_hijos(N+1,0), codigo_abajo(N+1,0);
    for(int i=N-1;i>=0;--i){
        int u = orden[i];
        u64 s = 0;
        for(int v: grafo[u]) if(v!=padre[u]){
            s += mix(codigo_abajo[v]);
        }
        suma_hijos[u] = s;
        codigo_abajo[u] = combinar(0, s);
    }

    vector<u64> hash_arriba(N+1,0);
    hash_arriba[1] = mix(123456789ULL);

    unordered_map<u64,int> frecuencia;
    frecuencia.reserve(N*2);

    for(int u: orden){
        u64 firma_u = combinar(hash_arriba[u], suma_hijos[u]);
        ++frecuencia[firma_u];
        for(int v: grafo[u]) if(v!=padre[u]){
            u64 sin_v = suma_hijos[u] - mix(codigo_abajo[v]);
            hash_arriba[v] = combinar(hash_arriba[u], sin_v);
        }
    }

    long long respuesta = 0;
    for(int u=1; u<=N; ++u){
        u64 firma_u = combinar(hash_arriba[u], suma_hijos[u]);
        if(frecuencia[firma_u] == 1) ++respuesta;
    }
    cout << respuesta << '\n';
    return 0;
}