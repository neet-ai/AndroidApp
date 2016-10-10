package info.neet_ai.machi_kiku;

/**
 * Created by User_h on 2016/09/29.
 */

public class MusicFile {
    String title;
    String artist;
    String path;
    long length;
    long id;

    public String getTitle(){
        return this.title;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public String getArtist(){
        return this.artist;
    }
    public void setArtist(String artist){
        this.artist = artist;
    }
    public String getPath(){
        return this.path;
    }
    public void setPath(String path){
        this.path = path;
    }
    public long getLength(){
        return this.length;
    }
    public void setLength(long length){
        this.length = length;
    }
    public long getId(){
        return this.id;
    }
    public void setId(long id){
        this.id = id;
    }
    public String makePlayList(){
        String a = "#EXTINF:" + Long.toString(length) + ", " + artist + " - " + title + "\n"+ path;
        return "\n\n" + a;
    }
    public void extractPlayList(String element1, String element2){
        int length_pos = element1.indexOf("#EXTINF") + 8;
        int artist_pos = element1.indexOf(", ", length_pos) + 2;
        int title_pos = element1.indexOf(" - ", artist_pos) + 3;
        this.title = element1.substring(title_pos);
        this.artist = element1.substring(artist_pos, title_pos - 3);
        this.path = element2;
        this.length = Long.parseLong( element1.substring(length_pos, artist_pos - 2) );
    }

}
