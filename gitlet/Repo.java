package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;


public class Repo implements Serializable {

    private Commit _head;
    private Tree _tree;
    private Staging _stage;

    Repo() {
        // TODO find tree and head pointer
        if (new File(".gitlet").exists()) {
            _tree = Utils.readObject(new File (".gitlet", "tree.txt"), Tree.class);
        }
        if (new File(".gitlet", "staging.txt").exists()) {
            _stage = Utils.readObject(new File (".gitlet", "staging.txt"), Staging.class);
        }
        if (new File(".gitlet", "head.txt").exists()) {
            _head = Utils.readObject(new File(".gitlet", "head.txt"), Commit.class);
        }
        // find current stage
    }


    public void init() throws IOException, ParseException {

        Tree repository = new Tree(null);
        repository._head = null;
        Commit firstCommit =  new Commit(new HashMap<String, String>(), "01/01/1970 00:00:00", "initial commit", null);
        repository._head = firstCommit;
        Staging newStage = new Staging();



        File repo = new File(".gitlet");
        if (repo.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
        } else {
            repo.mkdir();
            File commits = new File(".gitlet/commits");
            commits.mkdir();
            new File(".gitlet/globalLog").createNewFile();
            new File(".gitlet/branches").mkdir();
            new File(".gitlet/blobs").mkdir();
            new File(".gitlet/branches/master").mkdir();
            File stage = new File(".gitlet/staging.txt");
            stage.createNewFile();
            File head = new File(".gitlet/head.txt");
            head.createNewFile();
            File tree = new File(".gitlet/tree.txt");
            tree.createNewFile();
          //  Utils.writeObject(tree, repository);
          //  Utils.writeObject(head, firstCommit);
            Utils.writeObject(stage, newStage);
            Utils.writeObject(new File(".gitlet/commits/" + firstCommit.getCommitHash()), firstCommit);
            setTree(repository);
            setHead(firstCommit);
        }

        // create repo tree
        // create master branch
        // add first commit to master branch
        // commit has message "initial commit" and timestamp "00:00:00 UTC, Thursday, 1 January 1970"
        // if .gitlet already exists, abort and print "A Gitlet version-control system already exists in the current directory."
        // creates a log file for this branch master
        // creates a global-log file for entire repo
        // creates a file that represents the tree of the repo
        // determine how to store tree in a fast enough way
        // save repo tree
    }

    public void setTree(Tree tree) {
        File treeFile = new File(".gitlet/tree.txt");
        Utils.writeObject(treeFile, tree);
        _tree = tree;
    }

    public void setHead(Commit head){
        File headFile = new File(".gitlet/head.txt");
        Utils.writeObject(headFile, head);
        _head = head;
    }


    public Tree getTree() {
        return _tree;
    }

    public Commit getHead() {
        return _head;
    }


    /*
    Adds the new tree to the tree of trees;
     */
    public void commit(String message) throws ParseException {
        if (_stage.getStagedAdd().isEmpty() && _stage.getStagedToRemove().isEmpty()) {
            System.out.println("No changes added to the commit.");
            return;
        } else if (message.equals("")) {
            System.out.println("Please enter a commit message.");
            return;
        }
        Commit head = getHead();
        HashMap<String, String> prevCommitContents = (HashMap<String, String>) head.getFileBlobs().clone();
        for (String file : _stage.getAddFiles()) {
            prevCommitContents.put(file, _stage.getStagedAdd().get(file));
        }
        for (String file : _stage.getRemoveFiles()) {
            prevCommitContents.remove(file);
        }
        Commit newCommit = new Commit(prevCommitContents, null, message, getHead().getCommitHash());
        Utils.writeObject(new File(".gitlet/commits/" + newCommit.getCommitHash()), newCommit);
        _stage.reset();
        Utils.writeObject(new File(".gitlet", "staging"), _stage);
        setHead(newCommit);
        setTree(_tree);


        // if file in staging area, save new version
        // copy everything else from last commit
        // remove any files that were staged
        // update date and message
        // add commit to repo
        // clear staging
        // move head pointer to current commit
        // the commit is identifies by the SHA-1 ID containing the file blob references of its files, parent reference, log message, commit time
        // can only add num of files added
        // cannot store redundant information from parent, can only contain references
        // if no files staged, abort, print "No changes added to the commit."
        // commit must have non-blank message, else print "Please enter a commit message."
    }

    // moves the head pointer to the commitHash that represents a tree in the tree of trees

    public void add(String file) {
        // TODO get current stage
        _stage.stageAdd(file);
        // call add in staging
    }
    public void rm(String file) {
        // TODO get current stage
        _stage.stageRemove(file);
        // call rm in staging
    }

    public void checkoutFile(String file, String commit) throws IOException {
        File commitFile = new File(".gitlet/commits", commit);
        if (!commit.equals("head") && !commitFile.exists()) {
            System.out.println("No commit with that id exists.");
            return;
        }
        if (commit.equals("head")) {
            if (_head.getFileBlobs().get(file) == null) {
                System.out.println("File does not exist in that commit.");
                return;
            }
            String wantedBlob = _head.getFileBlobs().get(file);
            File wantedFile = new File(".gitlet/blobs/" + wantedBlob + ".txt");
            byte[] contents = Utils.readContents(wantedFile);
            File curr = new File(file);
            if (!curr.exists()) {
                curr.createNewFile();
            }
            Utils.writeContents(curr, contents);
        } else {
            Commit currCommit = Utils.readObject(new File(".gitlet/commits", commit), Commit.class);
            if (currCommit.getFileBlobs().get(file) == null) {
                System.out.println("File does not exist in that commit.");
                return;
            }
            String wantedBlob = currCommit.getFileBlobs().get(file);
            File wantedFile = new File(".gitlet/blobs/" + wantedBlob + ".txt");
            byte[] contents = Utils.readContents(wantedFile);
            File curr = new File(file);
            if (!curr.exists()) {
                curr.createNewFile();
            }
            Utils.writeContents(curr, contents);
        }
        /*
        checkout -- [filename]
        - take file version in head commit and replace current version, if not there, adds
        checkout [commit id] -- [filename]
        - take file version from given commit and replace current version, if not there, adds
        checkout [branchname]
        - take all files in commit at head of branch and put in working dir, given branch is now current branch
        - clear staging area, unless checked out branch is current branch
        if file does not exist in prev commit print "File does not exist in that commit."
        "No commit with that id exists."
        "No such branch exists."
        "No need to checkout the current branch."
         If a working file is untracked in the current branch and would be overwritten by the checkout,
         print There is an untracked file in the way; delete it, or add and commit it first. and exit;
         perform this check before doing anything else.
         */

    }
    public void checkoutBranch(String branch) {

    }

    // adds a commit to the Log text file for this branch
    private void addToLog(Tree branch, String newCommit) {

    }

    // merges two branches of the tree of trees
    private void merge(Tree branch1, Tree branch2) {

    }

    // adds a new commit to the global log
    private void addToGlobalLog(Tree branch, String newCommit) {

    }

    // returns the commits with message, message
    private String findCommit(String message) {

        return null;
    }

    // returns the status of the current directory, any uncommitted changes
    private String getStatus(Tree last, Tree current) {

        return null;
    }

    // returns a tree representation of the current state of the directory
    private Commit getCurrentCommit() {

        return _head;
    }

    // removes a branch of the tree
    private void removeBranch() {

    }

    // return the current log
    public void log() {
        Commit head = getHead();
        while (head != null) {
            System.out.println("===\ncommit " + head.getCommitHash());
            if (head.getMessage().equals("initial commit")) {
                System.out.println("Date: "+ head.getTime());
                System.out.println(head.getMessage() + "\n");
            } else {
                System.out.println("Date: "+ head.getTime() + head.getMessage() + "\n");
            }

            if (head.getParent() != null) {
                head = Utils.readObject(new File(".gitlet/commits/"
                        + head.getParent()), Commit.class);
            } else {
                break;
            }
        }
    }

    // return the global log
    private String getGlobalLog() {

        return null;
    }


}
