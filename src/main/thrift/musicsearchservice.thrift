
namespace java br.com.soundwhere.services

exception MusicSearchException { 
	1: i32 errorcode,
	2: string message
}

struct MusicEntry {
 1:string name,
 2:string genre,
 3:string url
}

struct AuthorEntry {
 1:string name,
 2:string composer
}

struct MusicSearchEntry {
 1:MusicEntry music,
 2:AuthorEntry author
}

struct MusicList {
 1:list<MusicSearchEntry> entries,
 2:string provider
}

service MusicSearchService {
	
	MusicList search(1: string query, 2:i32 start, 3:i32 end, 4:string username, 5:string token) throws (1: MusicSearchException ex)
}