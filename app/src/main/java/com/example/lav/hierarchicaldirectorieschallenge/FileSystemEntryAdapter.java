package com.example.lav.hierarchicaldirectorieschallenge;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lav.hierarchicaldirectorieschallenge.model.FileSystem;
import com.example.lav.hierarchicaldirectorieschallenge.model.FileSystemEntry;

public class FileSystemEntryAdapter extends RecyclerView.Adapter<FileSystemEntryAdapter.FileSystemEntryViewHolder>{

    private FileSystem mFileSystem;
    private EntryClickListener mClickListener;

    public FileSystemEntryAdapter(EntryClickListener clickListener) {
        mClickListener = clickListener;
    }

    public void setFileSystem(FileSystem fileSystem) {
        mFileSystem = fileSystem;
        notifyDataSetChanged();
    }

    public interface EntryClickListener {
        void onClickHandler(FileSystemEntry fileSystemEntry);
    }

    @Override
    public FileSystemEntryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.entry_item, parent, false);
        FileSystemEntryViewHolder vh = new FileSystemEntryViewHolder(mView);
        return vh;
    }

    @Override
    public void onBindViewHolder(FileSystemEntryViewHolder holder, int position) {
        holder.bind(mFileSystem.getEntries().get(position));
    }

    @Override
    public int getItemCount() {
        return mFileSystem == null ? 0 : mFileSystem.getEntries().size();
    }

    public class FileSystemEntryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mFileSystemItem;
        ImageView mImageViewEntry;
        private FileSystemEntry mFileSystemEntry;

        public FileSystemEntryViewHolder(View itemView) {
            super(itemView);
            mFileSystemItem = itemView.findViewById(R.id.tv_entry_item);
            mImageViewEntry = itemView.findViewById(R.id.iv_entry_image);
            itemView.setOnClickListener(this);
        }

        public void bind(FileSystemEntry fileSystemEntry) {
            mFileSystemEntry = fileSystemEntry;
            if("directory".equals(fileSystemEntry.getType())) {
                mImageViewEntry.setImageResource(R.drawable.folder);
            } else {
                mImageViewEntry.setImageResource(R.drawable.file);
            }
            mFileSystemItem.setText(fileSystemEntry.getName());
        }

        @Override
        public void onClick(View view) {
            mClickListener.onClickHandler(mFileSystemEntry);
        }
    }
}
