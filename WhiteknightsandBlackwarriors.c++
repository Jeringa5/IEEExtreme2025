#include <bits/stdc++.h>
using namespace std;

struct FastIn {
    static const int S = 1<<20;
    int idx, sz; char buf[S];
    FastIn(): idx(0), sz(0) {}
    inline char gc() {
        if (idx >= sz) { sz = (int)fread(buf,1,S,stdin); idx = 0; if (!sz) return 0; }
        return buf[idx++];
    }
    template<class T> inline bool readInt(T& out){
        char c; T sign=1, x=0; c=gc(); if(!c) return false;
        while(c!='-' && (c<'0'||c>'9')){ c=gc(); if(!c) return false; }
        if(c=='-'){ sign=-1; c=gc(); }
        for(; c>='0'&&c<='9'; c=gc()) x = x*10 + (c-'0');
        out = x*sign; return true;
    }
} In;

struct FastOut {
    static const int S = 1<<20; int idx; char buf[S];
    FastOut(): idx(0) {}
    ~FastOut(){ flush(); }
    inline void pc(char c){ if(idx==S) flush(); buf[idx++]=c; }
    inline void flush(){ if(idx) { fwrite(buf,1,idx,stdout); idx=0; } }
    inline void printInt(int x){ if(x==0){ pc('0'); pc('\n'); return; }
        if(x<0){ pc('-'); x=-x; }
        char s[20]; int n=0; while(x){ s[n++]=char('0'+x%10); x/=10; }
        while(n--) pc(s[n]); pc('\n');
    }
} Out;

const int MAXN = 1000000 + 5;
const int MAXE = 2000000 + 5;

int N, Q;
int head[MAXN], to[MAXE], nxt[MAXE], etot=0;

inline void addEdge(int u,int v){
    to[++etot]=v; nxt[etot]=head[u]; head[u]=etot;
    to[++etot]=u; nxt[etot]=head[v]; head[v]=etot;
}

int distW[MAXN];
int colorWhite[MAXN];

int parent_[MAXN], depth_[MAXN], heavy[MAXN], subSize[MAXN];
int headH[MAXN], pos[MAXN], rinv[MAXN], curPos=0;

struct SegTree {
    int n; vector<int> st;
    void init(int N){
        n=1; while(n<N) n<<=1;
        st.assign(2*n, INT_MAX);
    }
    inline void setPoint(int i, int v){ st[n+i]=v; }
    inline void build(){ for(int i=n-1;i>=1;--i) st[i]=min(st[i<<1], st[i<<1|1]); }
    inline int query(int l,int r){
        l += n; r += n; int res=INT_MAX;
        while(l<=r){
            if(l&1) res=min(res, st[l++]);
            if(!(r&1)) res=min(res, st[r--]);
            l>>=1; r>>=1;
        }
        return res;
    }
} seg;

void build_heavy_light(int root=1){
    vector<int> order; order.reserve(N);
    order.push_back(root);
    parent_[root]=0; depth_[root]=0;
    for(size_t i=0;i<order.size();++i){
        int u = order[i];
        for(int e=head[u]; e; e=nxt[e]){
            int v = to[e];
            if(v==parent_[u]) continue;
            parent_[v]=u; depth_[v]=depth_[u]+1;
            order.push_back(v);
        }
    }
    for(int i=(int)order.size()-1;i>=0;--i){
        int u=order[i];
        subSize[u]=1; heavy[u]=0;
        for(int e=head[u]; e; e=nxt[e]){
            int v=to[e]; if(v==parent_[u]) continue;
            subSize[u]+=subSize[v];
            if(!heavy[u] || subSize[v]>subSize[heavy[u]]) heavy[u]=v;
        }
    }
    curPos=0;
    vector<pair<int,int>> st2; st2.emplace_back(root, root);
    while(!st2.empty()){
        auto [u, h]=st2.back(); st2.pop_back();
        for(int v=u; v; v=heavy[v]){
            headH[v]=h; pos[v]=curPos; rinv[curPos]=v; ++curPos;
            for(int e=head[v]; e; e=nxt[e]){
                int w=to[e]; if(w==parent_[v] || w==heavy[v]) continue;
                st2.emplace_back(w, w);
            }
        }
    }
}

inline int query_path(int a,int b){
    int res = INT_MAX;
    while(headH[a]!=headH[b]){
        if(depth_[headH[a]] < depth_[headH[b]]) swap(a,b);
        int h = headH[a];
        res = min(res, seg.query(pos[h], pos[a]));
        a = parent_[h];
    }
    if(depth_[a] > depth_[b]) swap(a,b);
    res = min(res, seg.query(pos[a], pos[b]));
    return res;
}

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    if(!In.readInt(N)) return 0;
    In.readInt(Q);
    for(int i=1;i<=N;++i){
        int x; In.readInt(x); colorWhite[i]=x;
    }
    for(int i=0;i<N-1;++i){
        int a,b; In.readInt(a); In.readInt(b);
        addEdge(a,b);
    }

    const int INF = 1e9;
    for(int i=1;i<=N;++i) distW[i]=INF;
    deque<int> dq;
    for(int i=1;i<=N;++i){
        if(colorWhite[i]){
            distW[i]=0; dq.push_back(i);
        }
    }
    while(!dq.empty()){
        int u=dq.front(); dq.pop_front();
        for(int e=head[u]; e; e=nxt[e]){
            int v=to[e];
            if(distW[v] > distW[u]+1){
                distW[v] = distW[u]+1;
                dq.push_back(v);
            }
        }
    }

    build_heavy_light(1);

    seg.init(N);
    for(int i=0;i<N;++i){
        int u = rinv[i];
        seg.setPoint(i, distW[u]);
    }
    seg.build();

    for(int i=0;i<Q;++i){
        int u,v; In.readInt(u); In.readInt(v);
        int ans = query_path(u,v);
        Out.printInt(ans);
    }
    Out.flush();
    return 0;
}