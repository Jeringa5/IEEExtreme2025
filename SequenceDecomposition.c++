#include <bits/stdc++.h>
using namespace std;

struct FastOut {
    static const int B=1<<20;
    int i; char buf[B];
    FastOut():i(0){} ~FastOut(){ flush(); }
    inline void pc(char c){ if(i>=B) flush(); buf[i++]=c; }
    inline void pn(int x){
        if(x==0){ pc('0'); return; }
        char s[16]; int n=0; while(x){ s[n++]=char('0'+x%10); x/=10; }
        while(n--) pc(s[n]);
    }
    inline void psix(const array<int,6>& a){
        pn(a[0]); pc(' '); pn(a[1]); pc(' '); pn(a[2]); pc(' ');
        pn(a[3]); pc(' '); pn(a[4]); pc(' '); pn(a[5]); pc('\n');
    }
    inline void flush(){ if(i){ fwrite(buf,1,i,stdout); i=0; } }
} OUT;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int T; 
    if(!(cin>>T)) return 0;
    const int P[6]={1,1,2,0,1,2};

    while(T--){
        string s; cin>>s;
        int n=(int)s.size(), cap=n/6+5;

        deque<int> wait[6];
        vector<array<int,6>> seq; seq.reserve(cap);
        vector<int> nxt; nxt.reserve(cap);

        auto start_seq = [&](int pos){
            int id=(int)seq.size();
            seq.push_back({0,0,0,0,0,0});
            nxt.push_back(0);
            seq[id][nxt[id]++]=pos;
            wait[1].push_back(id);
        };

        for(int i=0;i<n;i++){
            int v=s[i]-'0';
            int ksel=-1;
            for(int k=5;k>=0;k--){
                if(P[k]==v && !wait[k].empty()){ ksel=k; break; }
            }
            if(ksel==-1){
                if(v!=1){ return 0; }
                start_seq(i+1);
            }else{
                int id=wait[ksel].front(); wait[ksel].pop_front();
                seq[id][nxt[id]++]=i+1;
                if(ksel+1<6) wait[ksel+1].push_back(id);
                else OUT.psix(seq[id]);
            }
        }
    }
    OUT.flush();
    return 0;
}