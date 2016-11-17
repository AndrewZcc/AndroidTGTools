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
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.EditText;
import android.util.Log;
import android.os.Environment;
import android.view.View;
import android.os.Handler;
import android.text.Html;
import android.text.method.LinkMovementMethod;

import org.json.JSONArray;
import org.json.JSONObject;
import oauth.signpost.*;

import oauth.signpost.basic.DefaultOAuthConsumer;

/*
 * Download from FlashcardExchange using its web api
 */
public class DownloaderFE extends DownloaderBase{
    public static final String INTENT_ACTION_SEARCH_TAG = "am.fe.intent.search_tag";
    public static final String INTENT_ACTION_SEARCH_USER = "am.fe.intent.search_user";
    public static final String INTENT_ACTION_SEARCH_PRIVATE= "am.fe.intent.private";

    private static final String TAG = "org.liberty.android.fantastischmemo.downloader.DownloaderFE";
    private static final String FE_API_KEY = "anymemo_android";
    private static final String FE_API_TAG= "http://api.flashcardexchange.com/v1/get_tag?api_key=" + FE_API_KEY+ "&tag=";
    private static final String FE_API_USER = "http://api.flashcardexchange.com/v1/get_user?api_key=" + FE_API_KEY+ "&dataset=3&user_login=";
    private static final String FE_API_CARDSET = "http://api.flashcardexchange.com/v1/get_card_set?api_key=" + FE_API_KEY+ "&card_set_id=";
    private DownloadListAdapter dlAdapter;

    private ListView listView;
    private Handler mHandler;
    private ProgressDialog mProgressDialog;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private String action;
    private String searchCriterion = null;
    private String oauthToken = null;
    private String oauthTokenSecret = null;
    private OAuthConsumer oauthConsumer = null;

    @Override
    protected void initialRetrieve(){
        mHandler = new Handler();
        dlAdapter = new DownloadListAdapter(this, R.layout.filebrowser_item);
        listView = (ListView)findViewById(R.id.file_list);
        listView.setAdapter(dlAdapter);
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();

        final EditText input = new EditText(this);
        Intent intent = getIntent();
        action = intent.getAction();
        if(action.equals(INTENT_ACTION_SEARCH_TAG)){
        } 
        else if(action.equals(INTENT_ACTION_SEARCH_USER)){
        } 
        else if(action.equals(INTENT_ACTION_SEARCH_PRIVATE)){
        }
        else{
            Log.e(TAG, "Invalid intent to invoke this activity.");
            finish();
        }
        Bundle extras = intent.getExtras();
        if(extras  == null){
            Log.e(TAG, "Extras is null.");
            finish();
        }
        else{
            searchCriterion = extras.getString("search_criterion");
            oauthToken= extras.getString("oauth_token");
            oauthTokenSecret = extras.getString("oauth_token_secret");
            if(action.equals(INTENT_ACTION_SEARCH_PRIVATE)){
                if(oauthToken == null || oauthToken == null){
                    Log.e(TAG, "OAuth key and token are not passed.");
                    finish();
                }
                else{

                    oauthConsumer = new DefaultOAuthConsumer(FEOauth.CONSUMER_KEY, FEOauth.CONSUMER_SECRET);
                    oauthConsumer.setTokenWithSecret(oauthToken, oauthTokenSecret);
                }
            }
        }
        AMGUIUtility.doProgressTask(this, R.string.loading_please_wait, R.string.loading_connect_net, new AMGUIUtility.ProgressTask(){
            private List<DownloadItem> dil;
            public void doHeavyTask() throws Exception{
                dil = retrieveList();
            }
            public void doUITask(){
                dlAdapter.addList(dil);
            }
        });
    }

    @Override
    protected void openCategory(DownloadItem di){
        /* No category for FlashcardExchange */
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
                    mProgressDialog = ProgressDialog.show(DownloaderFE.this, getString(R.string.loading_please_wait), getString(R.string.loading_downloading));
                    new Thread(){
                        public void run(){
                            try{
                                downloadDatabase(di);
                                mHandler.post(new Runnable(){
                                    public void run(){
                                        mProgressDialog.dismiss();
                                        String dbpath = Environment.getExternalStorageDirectory().getAbsolutePath() + getString(R.string.default_dir);
                                        new AlertDialog.Builder(DownloaderFE.this)
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
                                        new AlertDialog.Builder(DownloaderFE.this)
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

    private List<DownloadItem> retrieveList() throws Exception{
        List<DownloadItem> diList = new ArrayList<DownloadItem>();
        String url = "";
        if(action.equals(INTENT_ACTION_SEARCH_TAG)){
            url = FE_API_TAG + URLEncoder.encode(searchCriterion);
        }
        else if(action.equals(INTENT_ACTION_SEARCH_USER)){
            url = FE_API_USER + URLEncoder.encode(searchCriterion);
        }
        else if(action.equals(INTENT_ACTION_SEARCH_PRIVATE)){
            url = FE_API_USER + URLEncoder.encode(searchCriterion) + "&private=true&oauth_token_secret=" + oauthTokenSecret+ "&oauth_token=" + oauthToken;
            Log.i(TAG, "Before signingUrl: " + url);
            url = oauthConsumer.sign(url);

        }
        else{
            throw new IOException("Incorrect criterion used for this call");
        }
        Log.i(TAG, "Url: " + url);

        String jsonString = DownloaderUtils.downloadJSONString(url);
        Log.v(TAG, "JSON String: " + jsonString);
        JSONObject jsonObject = new JSONObject(jsonString);
        String status =  jsonObject.getString("response_type");
        if(!status.equals("ok")){
            throw new IOException("Status is not OK. Status: " + status);
        }
        JSONArray jsonArray = jsonObject.getJSONObject("results").getJSONArray("sets");
        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject jsonItem = jsonArray.getJSONObject(i);
            int cardId;
            if(jsonItem.has("original_card_set_id") && !jsonItem.isNull("original_card_set_id")){
                cardId = jsonItem.getInt("original_card_set_id");
            }
            else{
                cardId = jsonItem.getInt("card_set_id");
            }


            String address = FE_API_CARDSET + cardId;
            if(action.equals(INTENT_ACTION_SEARCH_PRIVATE)){
                address = address + "&private=true&oauth_token_secret=" + oauthTokenSecret+ "&oauth_token=" + oauthToken;
                address = oauthConsumer.sign(address);

            }
            DownloadItem di = new DownloadItem(DownloadItem.TYPE_DATABASE,
                    jsonItem.getString("title"),
                    jsonItem.getString("description"),
                    address);
            diList.add(di);
        }
        return diList;
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
        JSONArray flashcardsArray = rootObject.getJSONObject("results").getJSONArray("flashcards");
        List<Item> itemList = new ArrayList<Item>();
        for(int i = 0; i < flashcardsArray.length(); i++){
            JSONObject jsonItem = flashcardsArray.getJSONObject(i);
            String question = jsonItem.getString("question");
            String answer = jsonItem.getString("answer");
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
