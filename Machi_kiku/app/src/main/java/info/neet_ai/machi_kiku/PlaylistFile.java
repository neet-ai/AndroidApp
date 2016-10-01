package info.neet_ai.machi_kiku;

/**
 * Created by User_h on 2016/09/29.
 */

public class PlaylistFile {
    String name;
    String creator;
    String path;
    long length;
    long id;

    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getCreator(){
        return this.creator;
    }
    public void setArtist(String creator){
        this.creator = creator;
    }
    public void setNameAndCreator(String name, String creator){
        this.name = name;
        this.creator = creator;
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

}
