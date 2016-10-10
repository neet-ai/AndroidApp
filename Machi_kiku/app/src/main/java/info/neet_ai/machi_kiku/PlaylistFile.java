package info.neet_ai.machi_kiku;

import android.util.Log;

/**
 * Created by User_h on 2016/09/29.
 */

public class PlaylistFile {
    String name;
    String creator;
    String path;
    long elements;
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
    public void setCreator(String creator){
        this.creator = creator;
    }
    /*public void setNameAndCreator(String name, String creator){
        this.name = name;
        this.creator = creator;
    }*/
    public String getPath(){
        return this.path;
    }
    public void setPath(String path){
        this.path = path;
    }
    public long getLength(){
        return this.elements;
    }
    public void setLength(long elements){
        this.elements = elements;
    }
    public long getId(){
        return this.id;
    }
    public void setId(long id){
        this.id = id;
    }
    public String makeFileName(){
        return this.name + " - made by " + creator + ".M3U";
    }
    public void extractFileName(String element1){
        int creator_pos = element1.indexOf(" - made by ") + 11;
        int last_pos = element1.indexOf(".M3U", creator_pos);
        this.name = element1.substring(0, creator_pos - 11);
        this.creator = element1.substring(creator_pos, last_pos);
    }

}
