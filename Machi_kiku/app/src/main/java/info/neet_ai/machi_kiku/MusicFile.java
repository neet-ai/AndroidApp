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
        String a = "EXTINF:" + Long.toString(length) + ", " + artist + " - " + title + "\n";
        return "\n\n" + a + path;
    }

}
