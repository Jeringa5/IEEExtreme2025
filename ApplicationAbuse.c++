#include <iostream>
#include <string>
#include <sstream>
#include <vector>
#include <unordered_map>
#include <unordered_set>
#include <algorithm>
#include <cctype>
#include <climits>
using namespace std;

// -------- Tipos y utilidades --------
struct Reglas { int agente=-1, ip=-1, pdf=-1, sesion=-1, rastreo=-1; };

enum Campo { HOST, CLIENT_IP, IDF, FECHA, SOLICITUD, CODIGO_HTTP, USER_AGENT, COOKIE_SESION };

static inline string recortar(const string& s){
    size_t i=0, j=s.size();
    while (i<j && isspace((unsigned char)s[i])) ++i;
    while (j>i && isspace((unsigned char)s[j-1])) --j;
    return s.substr(i, j-i);
}

static inline string leer_comillas(const string& s, size_t& i){
    size_t j = i+1;
    while (j<s.size() && s[j]!='"') ++j;
    string out = s.substr(i+1, j-(i+1));
    i = (j<s.size() ? j+1 : j);
    return out;
}
static inline string leer_corchetes(const string& s, size_t& i){
    size_t j = i+1;
    while (j<s.size() && s[j]!=']') ++j;
    string out = s.substr(i+1, j-(i+1));
    i = (j<s.size() ? j+1 : j);
    return out;
}
static inline string leer_token(const string& s, size_t& i){
    size_t j = i;
    while (j<s.size() && !isspace((unsigned char)s[j])) ++j;
    string out = s.substr(i, j-i);
    i = j;
    return out;
}

// -------- Parseadores --------
Reglas parsear_reglas(string s){
    Reglas r; string tok; stringstream ss(s);
    while (getline(ss, tok, ',')){
        tok = recortar(tok);
        if (tok.empty()) continue;
        auto p = tok.find('=');
        if (p==string::npos) continue;
        string k = recortar(tok.substr(0,p));
        string v = recortar(tok.substr(p+1));
        if (!v.empty() && all_of(v.begin(), v.end(), ::isdigit)){
            int x = stoi(v);
            if (k=="agent") r.agente=x;
            else if (k=="ip") r.ip=x;
            else if (k=="pdf") r.pdf=x;
            else if (k=="session") r.sesion=x;
            else if (k=="crawl") r.rastreo=x;
        }
    }
    return r;
}

vector<Campo> parsear_descriptor(string s){
    vector<Campo> orden; string tok; stringstream ss(s);
    while (getline(ss, tok, ',')){
        tok = recortar(tok);
        if (tok=="Host") orden.push_back(HOST);
        else if (tok=="Client IP") orden.push_back(CLIENT_IP);
        else if (tok=="Id") orden.push_back(IDF);
        else if (tok=="Date") orden.push_back(FECHA);
        else if (tok=="Request") orden.push_back(SOLICITUD);
        else if (tok=="HTTP Status") orden.push_back(CODIGO_HTTP);
        else if (tok=="User Agent") orden.push_back(USER_AGENT);
        else if (tok=="Session Cookie") orden.push_back(COOKIE_SESION);
    }
    return orden;
}

// -------- Lógica de negocio --------
struct DiaStats {
    unordered_set<string> agentes, ips, cookies;
    int conteo_pdf = 0;
    int ultimo_pdf = INT_MIN;
    int racha_actual = 0;
    int mejor_racha = 0;
};

static inline string dia_de_fecha(const string& d){
    size_t p = d.find(':'); // dd/Mmm/YYYY:HH:mm:ss
    return (p==string::npos ? d : d.substr(0, p));
}

static inline bool extraer_pdf_num(const string& req, int& num){
    auto p = req.find("/document/");
    if (p==string::npos) return false;
    p += 10;
    auto q = req.find(".pdf", p);
    if (q==string::npos) return false;
    string mid = req.substr(p, q-p);
    if (mid.empty() || !all_of(mid.begin(), mid.end(), ::isdigit)) return false;
    num = stoi(mid);
    return true;
}

// -------- Programa principal --------
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    string linea;
    if (!getline(cin, linea)) return 0;
    Reglas reglas = parsear_reglas(linea);

    string descriptor;
    if (!getline(cin, descriptor)) return 0;
    vector<Campo> orden = parsear_descriptor(descriptor);
    if (orden.size()!=8) return 0;

    unordered_map<string, DiaStats> mapa; mapa.reserve(20000);

    auto crear_clave = [&](const string& id, const string& dia){
        string k; k.reserve(id.size()+1+dia.size());
        k += id; k += '\n'; k += dia;
        return k;
    };

    while (getline(cin, linea)){
        if (linea.empty()) continue;

        string host, ip, id, fecha, solicitud, agente, cookie;
        int codigo = -1;

        size_t i = 0; const size_t n = linea.size();
        auto saltar_blancos = [&](){ while (i<n && isspace((unsigned char)linea[i])) ++i; };

        for (Campo c : orden){
            saltar_blancos();
            if (i>=n) break;
            switch (c){
                case FECHA:           if (linea[i]=='[') fecha = leer_corchetes(linea, i); else fecha.clear(); break;
                case SOLICITUD:       if (linea[i]=='"') solicitud = leer_comillas(linea, i); else solicitud.clear(); break;
                case USER_AGENT:      if (linea[i]=='"') agente   = leer_comillas(linea, i); else agente.clear(); break;
                case COOKIE_SESION:   if (linea[i]=='"') cookie   = leer_comillas(linea, i); else cookie.clear(); break;
                case CODIGO_HTTP: {
                    string t = leer_token(linea, i);
                    codigo = (!t.empty() && all_of(t.begin(), t.end(), ::isdigit)) ? stoi(t) : -1;
                } break;
                case HOST:            host = leer_token(linea, i); break;
                case CLIENT_IP:       ip   = leer_token(linea, i); break;
                case IDF:             id   = leer_token(linea, i); break;
            }
        }

        if (id.empty() || id=="-") continue;
        if (codigo!=200) continue;

        string dia = dia_de_fecha(fecha);
        string clave = crear_clave(id, dia);
        DiaStats& info = mapa[clave];

        if (!agente.empty() && agente!="-") info.agentes.insert(agente);
        if (!ip.empty() && ip!="-")         info.ips.insert(ip);
        if (!cookie.empty() && cookie!="-") info.cookies.insert(cookie);

        int numero_pdf;
        if (extraer_pdf_num(solicitud, numero_pdf)){
            ++info.conteo_pdf;
            if (info.ultimo_pdf + 1 == numero_pdf) ++info.racha_actual;
            else info.racha_actual = 1;
            info.ultimo_pdf = numero_pdf;
            info.mejor_racha = max(info.mejor_racha, info.racha_actual);
        }
    }

    struct Registro { string id, tipo; int valor; };
    vector<Registro> salida; salida.reserve(2048);

    auto agregar_si = [&](const string& id, const string& tipo, int valor, int umbral){
        if (umbral>=0 && valor>=umbral) salida.push_back({id, tipo, valor});
    };

    for (const auto& par : mapa){
        const string& clave = par.first;
        const DiaStats& inf = par.second;
        auto p = clave.find('\n');
        string id = clave.substr(0, p);

        agregar_si(id, "agent",   (int)inf.agentes.size(), reglas.agente);
        agregar_si(id, "ip",      (int)inf.ips.size(),     reglas.ip);
        agregar_si(id, "pdf",     inf.conteo_pdf,          reglas.pdf);
        agregar_si(id, "session", (int)inf.cookies.size(), reglas.sesion);
        agregar_si(id, "crawl",   inf.mejor_racha,         reglas.rastreo);
    }

    if (salida.empty()){
        cout << "N/A\n";
        return 0;
    }

    sort(salida.begin(), salida.end(), [](const Registro& a, const Registro& b){
        if (a.id!=b.id) return a.id<b.id;
        return a.tipo<b.tipo;
    });
    salida.erase(unique(salida.begin(), salida.end(), [](const Registro& a, const Registro& b){
        return a.id==b.id && a.tipo==b.tipo;
    }), salida.end());

    for (const auto& r : salida){
        cout << r.id << ' ' << r.tipo << '=' << r.valor << '\n';
    }
    return 0;
}