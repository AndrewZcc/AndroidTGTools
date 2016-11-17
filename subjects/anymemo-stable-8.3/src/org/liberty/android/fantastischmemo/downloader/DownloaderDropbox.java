/*
Copyright (C) 2010 Haowen Ning

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

*/
package org.liberty.android.fantastischmemo.downloader;

import org.liberty.android.fantastischmemo.*;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;
import java.net.URLEncoder;


import android.os.Bundle;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.util.Log;
import android.os.Environment;
import android.view.View;
import android.os.Handler;
import android.text.Html;
import android.text.method.LinkMovementMethod;

import org.json.JSONArray;
import org.json.JSONObject;

/*
 * Download from Dropbox using its web api
 * Pass in dropbox_token and dropbox_secret
 * Pass in initial_path as extra to set the initial dropbox path.
 */
public class DownloaderDropbox extends DownloaderBase {
    private static final String TAG = "org.liberty.android.fantastischmemo.downloader.DownloaderDropbox";
    private DownloadListAdapter dlAdapter;

    private ListView listView;
    private Handler mHandler;
    private ProgressDialog mProgressDialog;
    private String initialPath = "";
    private String dropboxToken = null;
    private String dropboxSecret = null;

    @Override
    protected void initialRetrieve(){
        mHandler = new Handler();
        dlAdapter = new DownloadListAdapter(this, R.layout.filebrowser_item);
        listView = (ListView)findViewById(R.id.file_list);
        listView.setAdapter(dlAdapter);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras  != null){
            initialPath = extras.getString("initial_path");
            dropboxToken = extras.getString("dropbox_token");
            dropboxSecret = extras.getString("dropbox_secret");
        }
        AMGUIUtility.doProgressTask(this, R.string.loading_please_wait, R.string.loading_connect_net, new AMGUIUtility.ProgressTask(){
            private List<DownloadItem> dil;
            public void doHeavyTask() throws Exception{
                dil = DropboxUtils.listFiles(dropboxToken, dropboxSecret, initialPath);
            }
            public void doUITask(){
                dlAdapter.addList(dil);
            }

        });
    }

    @Override
    protected void openCategory(DownloadItem di){
        Intent myIntent = new Intent(this, DownloaderDropbox.class);
        myIntent.putExtra("dropbox_token", dropboxToken);
        myIntent.putExtra("dropbox_secret", dropboxSecret);
        myIntent.putExtra("initial_path", di.getAddress());
        startActivity(myIntent);
    }

    @Override
    protected DownloadItem getDownloadItem(int position){
        return dlAdapter.getItem(position);
    }
    
    @Override
    protected void goBack(){
        finish();
    }

    @Override
    protected void fetchDatabase(final DownloadItem di){
        View alertView = View.inflate(this, R.layout.link_alert, null);
        TextView textView = (TextView)alertView.findViewById(R.id.link_alert_message);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(Html.fromHtml(getString(R.string.downloader_download_alert_message) + di.getDescription()));

        new AlertDialog.Builder(this)
            .setView(alertView)
            .setTitle(getString(R.string.downloader_download_alert) + di.getTitle())
            .setPositiveButton(getString(R.string.yes_text), new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface arg0, int arg1){
                    mProgressDialog = ProgressDialog.show(DownloaderDropbox.this, getString(R.string.loading_please_wait), getString(R.string.loading_downloading));
                    new Thread(){
                        public void run(){
                            try{
                                String sdpath = Environment.getExternalStorageDirectory().getAbsolutePath() + getString(R.string.default_dir) + "/";
                                DropboxUtils.downloadFile(dropboxToken, dropboxSecret, di, sdpath);
                                mHandler.post(new Runnable(){
                                    public void run(){
                                        mProgressDialog.dismiss();
                                        String dbpath = Environment.getExternalStorageDirectory().getAbsolutePath() + getString(R.string.default_dir); 
                                        new AlertDialog.Builder(DownloaderDropbox.this)
                                            .setTitle(R.string.downloader_download_success)
                                            .setMessage(getString(R.string.downloader_download_success_message) + dbpath + di.getTitle() + ".db")
                                            .setPositiveButton(R.string.ok_text, null)
                                            .create()
                                            .show();
                                    }
                                });

                            }
                            catch(final Exception e){
                                Log.e(TAG, "Error downloading", e);
                                mHandler.post(new Runnable(){
                                    public void run(){
                                        mProgressDialog.dismiss();
                                        new AlertDialog.Builder(DownloaderDropbox.this)
                                            .setTitle(R.string.downloader_download_fail)
                                            .setMessage(getString(R.string.downloader_download_fail_message) + " " + e.toString())
                                            .setPositiveButton(R.string.ok_text, null)
                                            .create()
                                            .show();
                                    }
                                });
                            }
                        }
                    }.start();
                }
            })
            .setNegativeButton(getString(R.string.no_text), null)
            .show();
    }

    private void downloadDatabase(DownloadItem di) throws Exception{
        String address = di.getAddress();
        String dbJsonString = DownloaderUtils.downloadJSONString(address);
        Log.v(TAG, "Download url: " + address);
        JSONObject rootObject = new JSONObject(dbJsonString);
        String status = rootObject.getString("response_type");
        if(!status.equals("ok")){
            Log.e(TAG, "Content: " + dbJsonString);
            throw new IOException("Status is not OK. Status: " + status);
        }
        JSONArray flashcardsArray = rootObject.getJSONArray("sets").getJSONObject(0).getJSONArray("terms");
        List<Item> itemList = new ArrayList<Item>();
        for(int i = 0; i < flashcardsArray.length(); i++){
            JSONArray jsonItem = flashcardsArray.getJSONArray(i);
            String question = jsonItem.getString(0);
            String answer = jsonItem.getString(1);
            Item newItem = new Item.Builder()
                .setQuestion(question)
                .setAnswer(answer)
                .setId(i + 1)
                .build();
            itemList.add(newItem);
        }
        
        /* Make a valid dbname from the title */
        String dbname = DownloaderUtils.validateDBName(di.getTitle()) + ".db";
        String dbpath = Environment.getExternalStorageDirectory().getAbsolutePath() + getString(R.string.default_dir);
        DatabaseHelper.createEmptyDatabase(dbpath, dbname);
        DatabaseHelper dbHelper = new DatabaseHelper(this, dbpath, dbname);
        dbHelper.insertListItems(itemList);
        dbHelper.close();
        RecentListUtil.addToRecentList(this, dbpath, dbname);
    }

}
