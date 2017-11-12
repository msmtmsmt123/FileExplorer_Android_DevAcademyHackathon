package com.example.lav.hierarchicaldirectorieschallenge.model;

import java.util.ArrayList;

public class FileSystemEntry {

    private int id;
    private String type;
    private String name;
    private ArrayList<FileSystemEntry> contents;
    private FileSystemEntry parent;

    public FileSystemEntry getParent() {
        return parent;
    }

    public void setParent(FileSystemEntry parent) {
        this.parent = parent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<FileSystemEntry> getContents() {
        return contents;
    }

    public void setContents(ArrayList<FileSystemEntry> contents) {
        this.contents = contents;
    }

    public void setParents() {
        for(FileSystemEntry entry : contents) {
            entry.setParent(this);
            entry.setParents();
        }
    }
}
