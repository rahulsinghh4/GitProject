package gitlet;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import java.io.File;
import java.util.ArrayList;



// represents a single commit
/*
a commit contains a map of the files in the directory,
defaults to the exact same snapshot as the parent
only adds or changes files that have been staged for commit
so first pulls from parent node
add/remove files from staging
commit has a tree with the root node having a label for message, time, and hash that represents that commit
clear staging area after commit
a commit moves the head pointer to the new commit
each commit is identified by its SHA-1 id, which must include the file references of its files, parent reference, log message, and commit time
 */



class Tree<T> implements Iterable<Tree<T>>, Serializable {

    public T hash;
    public Tree<T> parent;
    public List<Tree<T>> children;
    public Commit _head;
    public String message;
    public String time;

    public boolean isRoot() {
        return parent == null;
    }

    public boolean isLeaf() {
        return children.size() == 0;
    }

    private List<Tree<T>> elementsIndex;

    public Tree(T hash) {
        this.hash = hash;
     //   this.message = message;
     //   this.time = time;
        this.children = new LinkedList<Tree<T>>();
        this.elementsIndex = new LinkedList<Tree<T>>();
        this.elementsIndex.add(this);
    }

    public Tree<T> addChild(T child) {
        Tree<T> childNode = new Tree<T>(child);
        childNode.parent = this;
        this.children.add(childNode);
        this.registerChildForSearch(childNode);
        return childNode;
    }

    public int getLevel() {
        if (this.isRoot())
            return 0;
        else
            return parent.getLevel() + 1;
    }

    private void registerChildForSearch(Tree<T> node) {
        elementsIndex.add(node);
        if (parent != null)
            parent.registerChildForSearch(node);
    }

    public Tree<T> findTreeNode(Comparable<T> cmp) {
        for (Tree<T> element : this.elementsIndex) {
            T elData = element.hash;
            if (cmp.compareTo(elData) == 0)
                return element;
        }

        return null;
    }

    @Override
    public String toString() {
        return hash != null ? hash.toString() : "[data null]";
    }


    @Override
    public Iterator<Tree<T>> iterator() {
        return null;
    }
}

    /*
    Data Structures:
       - Tree of trees, represent repository, each node contains a tree
       - Tree represents one tree of files in a commit, each node contains a blob
    1. init creates a .git directory, creates a tree with root the current directory, adds first commit
    2. add will clone the current tree and add the new file as a new leaf node on that tree
    3. commit will add a tree to the tree of trees that represent the repository
    4. checking out — will move the head to a different node of the tree of trees
    5. log string version of the tree of trees for current branch (returns contents of doc for branch)
    6. merge combines two branches in the tree of trees
    7. rm stage to remove a node from the current tree
    8. global-log string version of entire tree of trees commit history (returns contents of doc)
    9. find finds commits with commit message, searches tree of trees
    10. status tells you if there is any change in a document that hasn't been committed by comparing the current state of the file tree to the last commit in the tree of trees
    10. tells you which nodes don't match
    11. rm-branch removes a branch in the tree of trees
   12. Error messages
   13.
     */




