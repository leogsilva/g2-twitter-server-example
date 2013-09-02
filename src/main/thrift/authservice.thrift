
namespace java br.com.soundwhere.services

struct AuthInfo {
    1:string key,
    2:string targetHost,
    3:bool hasError,
    4:string message
}

service AuthService {
    
    list<AuthInfo> login(1:string provider,2:string email, 3:string password) 
    
}