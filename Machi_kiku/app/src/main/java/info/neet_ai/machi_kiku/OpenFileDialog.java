package info.neet_ai.machi_kiku;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.util.Log;

import java.io.File;

public class OpenFileDialog {
    public OpenFileDialog() {
        try {
            this.init("/");
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        path = "/";
        //MenuWords = new Translate(new String[] {"Open", "Open ...", "Move", "Cancel"},
        //        new String[] {"open", "open_title", "move", "cancel"});
    }
    public OpenFileDialog(String openDir) {
        try {
            this.init(openDir);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        if (openDir.startsWith("//")) {
            path = openDir.substring(1);
        } else {
            path = openDir;
        }
        //MenuWords = new Translate(new String[] {"Open", "Open ...", "Move", "Cancel"},
        //        new String[] {"open", "open_title", "move", "cancel"});
    }
    public void init(String dirname) throws java.io.IOException{
        java.io.File file= new java.io.File(dirname);
        int counter = 0;

        if (!file.getCanonicalPath().equals("/")) {
            int size = file.list()!=null ? file.list().length:0;
            items = new String[size + 1];
            items[0] = "../";
            if (file.list()!=null) {
                java.lang.System.arraycopy(file.list(),0,items,1,size);
                counter = 1;
            }
        } else {
            items = file.list()!=null?file.list():new String[] {""};
            counter = 0;
        }
        for (;counter<items.length;counter++) {
            java.io.File iItem = new java.io.File(dirname + "/" + items[counter]);
            if (iItem.isDirectory()) {
                items[counter] += "/";
            }
        }
        if (items[0].equals("..//")) {
            items[0] = "../";
        }
    }

    public int move(android.content.Context context,OpenMode mode, String path) {
        OpenFileDialog childopenfiledialog = new OpenFileDialog(path);
        childopenfiledialog.openFileAction = openFileAction;
        childopenfiledialog.MenuWords = MenuWords;
        try {
            childopenfiledialog.createOpenFileDialog(context, mode).show();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public interface OpenFileAction {
        java.io.File write(java.io.File file);
        java.io.File append(java.io.File file);
        void read(java.io.File file);
        void getpath(String filepath);
        String toString();
    }

    public enum OpenMode {
        Write,Append,Read,Getpath
    }
    public OpenFileAction openFileAction = new OpenFileAction() {
        @Override
        public File write(File file) {
            return null;
        }

        @Override
        public File append(File file) {
            return null;
        }

        @Override
        public void read(File file) {
        }

        @Override
        public void getpath(String filepath) {
        }
    };
    private String path;
    public String[] items;
    public String[] MenuWords = {"開く", "音楽ファイルの選択", "移動", "閉じる"};

    public android.app.AlertDialog.Builder createOpenFileDialog(final android.content.Context context, final OpenFileDialog.OpenMode mode)
            throws java.io.IOException {
        final java.util.ArrayList<Integer> checkedItems = new java.util.ArrayList<>();
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle(MenuWords[1])
                .setMultiChoiceItems(items, null, new android.content.DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) checkedItems.add(which);
                        else checkedItems.remove((Integer) which);
                    }
                })
                .setPositiveButton(MenuWords[0], new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialog, int which) {

                        for (Integer i : checkedItems) {
                            // item_i checked
                            // note: i is undefined when not checked. use switch to do work.
                            // insert / as double / is allowed and no / is error.
                            java.io.File file = new java.io.File(path + "/" + items[i]);
                            switch (mode) {
                                case Write:
                                    openFileAction.write(file);
                                    break;
                                case Append:
                                    openFileAction.append(file);
                                    break;
                                case Read:
                                    openFileAction.read(file);
                                    break;
                                case Getpath:
                                    //Log.v("clear", file.getPath());
                                    openFileAction.getpath(path + items[i]);
                                    break;
                                default:
                                    android.util.Log.d("FileState", openFileAction.toString());
                            }
                        }
                    }
                })
                .setNegativeButton(MenuWords[2], new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        if (checkedItems.size()==1) {
                            for (Integer i: checkedItems
                                    ) {
                                if (items[i].endsWith("../")) {
                                    String nextPath = "/";
                                    char[] pArray = path.toCharArray();
                                    int slash = 0;
                                    for (int j=0;j<pArray.length-1;j++) {
                                        if (pArray[pArray.length-1-j] == '/') {
                                            slash++;
                                        }
                                        if (slash==3) {
                                            nextPath = path.substring(0,pArray.length-j);
                                            break;
                                        }
                                    }
                                    move(context, mode, nextPath);
                                } else {
                                    if (items[i].endsWith("/")) {
                                        if (path.endsWith("/")) {
                                            move(context, mode, path + items[i]);
                                        } else {
                                            move(context, mode, path + "/" +items[i]);
                                        }
                                    }
                                }
                            }
                        }
                    }

                })
                .setNeutralButton(MenuWords[3], null);
        return builder;
    }
}