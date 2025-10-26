#include <bits/stdc++.h>
using namespace std;

static const long long MOD = 1'000'000'007LL;

struct Vec4 { unsigned long long x[4]; };

long long addmod(long long a, long long b){ a+=b; if(a>=MOD) a-=MOD; return a; }
long long mulmod(long long a, long long b){ return (a*b)%MOD; }

long long F(Vec4 X){
    for(int i=0;i<4;++i) if((long long)X.x[i] < 0) return 0;
    int maxb = 60;
    long long dp[16]={0}, ndp[16]={0};
    dp[15]=1;
    for(int b=maxb;b>=0;--b){
        memset(ndp,0,sizeof(ndp));
        int xb[4];
        for(int i=0;i<4;++i) xb[i] = (int)((X.x[i]>>b)&1ULL);
        for(int t=0;t<16;++t){
            long long ways = dp[t];
            if(!ways) continue;
            int ub[4];
            for(int i=0;i<4;++i) ub[i] = ((t>>i)&1) ? xb[i] : 1;
            for(int mask=0; mask<16; ++mask){
                bool ok=true;
                int ones=0;
                for(int i=0;i<4;++i){
                    int bi = (mask>>i)&1;
                    if(bi>ub[i]){ ok=false; break; }
                    ones += bi;
                }
                if(!ok) continue;
                if((ones&1)!=0) continue; // xor del bit debe ser 0
                int nt=0;
                for(int i=0;i<4;++i){
                    int tight_i = (t>>i)&1;
                    int bi = (mask>>i)&1;
                    nt |= ( tight_i && (bi==xb[i]) ) ? (1<<i) : 0;
                }
                ndp[nt] = (ndp[nt] + ways) % MOD;
            }
        }
        memcpy(dp, ndp, sizeof(dp));
    }
    long long res=0;
    for(int t=0;t<16;++t) res = addmod(res, dp[t]);
    return res;
}

long long count_zero(long long A1,long long B1,long long A2,long long B2,long long A3,long long B3,long long A4,long long B4){
    auto pref = [&](unsigned long long X1,unsigned long long X2,unsigned long long X3,unsigned long long X4){
        Vec4 X{{X1,X2,X3,X4}};
        return F(X);
    };
    auto IE = [&](unsigned long long L1,unsigned long long R1,
                   unsigned long long L2,unsigned long long R2,
                   unsigned long long L3,unsigned long long R3,
                   unsigned long long L4,unsigned long long R4){
        long long ans=0;
        unsigned long long L[4]={L1,L2,L3,L4}, R[4]={R1,R2,R3,R4};
        for(int m=0;m<16;++m){
            unsigned long long X[4];
            bool bad=false;
            for(int i=0;i<4;++i){
                if(m&(1<<i)){
                    if(L[i]==0){ bad=true; break; }
                    X[i]=L[i]-1;
                }else{
                    X[i]=R[i];
                }
            }
            if(bad) continue;
            long long val = pref(X[0],X[1],X[2],X[3]);
            if(__builtin_popcount((unsigned)m)&1) ans = (ans - val + MOD) % MOD;
            else ans = (ans + val) % MOD;
        }
        return ans;
    };
    return IE(A1,B1,A2,B2,A3,B3,A4,B4);
}

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int Q; 
    if(!(cin>>Q)) return 0;
    while(Q--){
        unsigned long long A1,B1,A2,B2,A3,B3,A4,B4;
        cin>>A1>>B1>>A2>>B2>>A3>>B3>>A4>>B4;
        long long total = 1;
        total = mulmod(total, ( (B1%MOD - A1%MOD + MOD + 1)%MOD ));
        total = mulmod(total, ( (B2%MOD - A2%MOD + MOD + 1)%MOD ));
        total = mulmod(total, ( (B3%MOD - A3%MOD + MOD + 1)%MOD ));
        total = mulmod(total, ( (B4%MOD - A4%MOD + MOD + 1)%MOD ));
        long long zero = count_zero((long long)A1,(long long)B1,(long long)A2,(long long)B2,(long long)A3,(long long)B3,(long long)A4,(long long)B4);
        long long ans = (total - zero + MOD) % MOD;
        cout << ans << '\n';
    }
    return 0;
}