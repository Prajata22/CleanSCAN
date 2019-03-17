package com.babanomania.pdfscanner.FLHandlers;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.babanomania.pdfscanner.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class FLAdapter extends RecyclerView.Adapter<FLViewHolder> {

    private final String baseDirectory;
    private File[] fileList;

    public boolean multiSelect = false;
    public List<File> selectedItems = new ArrayList<>();
    protected ActionMode mActionMode;

    private ActionMode.Callback actionModeCallbacks = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            multiSelect = true;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

            if( selectedItems.size() == 0 || selectedItems.size() == 1 ){
                MenuInflater inflater = mode.getMenuInflater();
                menu.clear();
                inflater.inflate(R.menu.single_select_menu, menu);
                mode.setTitle( "1 Selected" );
                return true;

            } else {
                MenuInflater inflater = mode.getMenuInflater();
                menu.clear();
                inflater.inflate(R.menu.multi_select_menu, menu);
                mode.setTitle( selectedItems.size() + " Selected" );
                return true;
            }

        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            switch (item.getItemId()) {
                case R.id.menu_delete:

                    for (File fileItem  : selectedItems) {
                        fileItem.delete();
                    }

                    mode.finish();

                    return true;

                case R.id.menu_edit:
                    //TODO rename file
                    return false;


                default:
                    return false;
            }

        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            multiSelect = false;
            selectedItems.clear();
            update();
        }
    };

    public FLAdapter( String pBaseDirectory ){
        this.baseDirectory = pBaseDirectory;
        updateFileList();

    }

    public void update(){
        updateFileList();
        notifyDataSetChanged();
    }

    private void updateFileList(){

        File sd = Environment.getExternalStorageDirectory();
        File dir = new File(sd, this.baseDirectory);

        if( dir.listFiles() != null ) {
            this.fileList = dir.listFiles();

            Arrays.sort(    this.fileList,
                            new Comparator<File>(){
                                public int compare(File f1, File f2){
                                    return Long.valueOf( f2.lastModified() ).compareTo( f1.lastModified() );
                                }
                            }
                );

        } else {
            this.fileList = new File[0];
        }
    }

    @NonNull
    @Override
    public FLViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View listItem = layoutInflater.inflate( R.layout.file_item_view, viewGroup, false );
        FLViewHolder viewHolder = new FLViewHolder(listItem, actionModeCallbacks, this );

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FLViewHolder viewHolder, int i) {
            viewHolder.setFile( this.fileList[i] );
    }

    @Override
    public int getItemCount() {
            return this.fileList.length;
    }

}