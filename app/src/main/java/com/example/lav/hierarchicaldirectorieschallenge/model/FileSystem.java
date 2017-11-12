package com.example.lav.hierarchicaldirectorieschallenge.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FileSystem {

    @SerializedName("file_system")
    @Expose
    private List<FileSystemEntry> mEntries = null;

    public FileSystem(List<FileSystemEntry> entries) {
        mEntries = entries;
        setParentForEntries();
    }


    public void setParentForEntries() {
        for (FileSystemEntry fileSystemEntry : mEntries) {
            fileSystemEntry.setParents();
        }
    }

    public List<FileSystemEntry> getEntries() {
        return mEntries;
    }

    public void setEntries(List<FileSystemEntry> entries) {
        mEntries = entries;
    }
}