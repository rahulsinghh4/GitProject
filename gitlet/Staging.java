package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Staging implements Serializable {

    private HashMap<String, String> _stagedToAdd;
    private HashMap<String, String> _stagedToRemove;
    private Staging _stage;
    private Commit _head;


    /*
    when a file is added or removed, it is saved in a hash at that moment, to preserve the exact version at the time
     */
    public Staging() {
        _stagedToAdd = new HashMap<>();
        _stagedToRemove = new HashMap<>();
        File staged = new File(".gitlet/staging.txt");
        if (staged.exists()) {
            _stage = Utils.readObject(staged, Staging.class);
        }
    }


    public void stageAdd(String file) {

        File stagedAdd = new File(file);

        if (stagedAdd.exists()) {
            byte[] contents = Utils.readContents(stagedAdd);
            String contentsHash = Utils.sha1(contents);
            Commit head = getHead();
            if (getHead().getFileBlobs()!= null && getHead().getFileBlobs().get(file) != null && getHead().getFileBlobs().get(file).equals(contentsHash) && contents != null) {
                if (_stagedToRemove.containsKey(file)) {
                    _stagedToRemove.remove(file);
                }
                return;
            }
            if (_stagedToAdd.containsKey(file)) {
                _stagedToAdd.remove(file);
            }
            Utils.writeContents(new File(".gitlet/blobs/" + contentsHash + ".txt"), contents);
            _stagedToAdd.put(file, contentsHash);
            Utils.writeObject(new File(".gitlet/staging.txt"), this);

        } else {
            System.out.println("File does not exist.");
        }


        // adds a copy of file as it currently exists to staging area
        /*
        overwrite an already staged file with new entry
        if current version of file is identical to the version in the current commit, do not stage for adding or removal
        if file does not exist print "File does not exist." and exit

         */
    }

    public void stageRemove(String filename) {


    }

    public void reset() {
        _stagedToAdd = new HashMap<>();
        _stagedToRemove = new HashMap<>();
    }

    public HashMap<String, String> getStagedAdd() {
        return _stagedToAdd;
    }

    public HashMap<String, String> getStagedToRemove() {
        return _stagedToRemove;
    }
    public ArrayList<String> getAddFiles() {
        ArrayList<String> files = new ArrayList<>(_stagedToAdd.keySet());
        return files;
    }
    public ArrayList<String> getRemoveFiles() {
        ArrayList<String> files = new ArrayList<>(_stagedToRemove.keySet());
        return files;
    }
    private Commit getHead() {
        File head = new File(".gitlet/head.txt");
        if (head.exists()) {
            return Utils.readObject(head, Commit.class);
        }
        return null;
    }


}


// 1. write methods for existing gitlet functions
// implement git init
// implement git add, commit, rm,
// implement git log, global-log, etc
