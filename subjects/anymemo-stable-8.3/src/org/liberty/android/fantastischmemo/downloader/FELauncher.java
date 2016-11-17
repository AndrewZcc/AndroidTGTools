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
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.view.View.OnClickListener;
import android.preference.PreferenceManager;
import android.content.Intent;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.app.AlertDialog;

public class FELauncher extends AMActivity implements OnClickListener{
    private Button directoryButton;
    private Button searchTagButton;
    private Button searchUserButton;
    private Button loginButton;
    private Button privateButton;
    private Button uploadButton;
    private static final String TAG = "org.liberty.android.fantastischmemo.downloader.FELauncher";
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    @Override
	public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fe_launcher);
        directoryButton = (Button)findViewById(R.id.fe_directory);
        searchTagButton = (Button)findViewById(R.id.fe_search_tag);
        searchUserButton = (Button)findViewById(R.id.fe_search_user);
        loginButton = (Button)findViewById(R.id.fe_login);
        privateButton = (Button)findViewById(R.id.fe_private_login);
        uploadButton = (Button)findViewById(R.id.fe_upload);
        directoryButton.setOnClickListener(this);
        searchTagButton.setOnClickListener(this);
        searchUserButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        privateButton.setOnClickListener(this);
        uploadButton.setOnClickListener(this);
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        editor = settings.edit();
    }

    @Override
    public void onResume(){
        super.onResume();
        String searchText = settings.getString("saved_username", "");
        String key = settings.getString("saved_oauth_token", "");
        String secret = settings.getString("saved_oauth_token_secret", "");
        if(!searchText.equals("") && !key.equals("") && !secret.equals("")){
            loginButton.setText(getString(R.string.fe_logged_in_text) + ": " + searchText);
        }
    }

    @Override
    public void onClick(View v){
        if(v == directoryButton){
            Intent myIntent = new Intent(this, FEDirectory.class);
            startActivity(myIntent);
        }
        if(v == searchTagButton){
            showSearchTagDialog();
        }
        if(v == searchUserButton){
            showSearchUserDialog();
        }
        if(v == loginButton){
            Intent myIntent = new Intent(this, FEOauth.class);
            startActivity(myIntent);
        }
        if(v == privateButton){
            String searchText = settings.getString("saved_username", "");
            String key = settings.getString("saved_oauth_token", "");
            String secret = settings.getString("saved_oauth_token_secret", "");
            if(!searchText.equals("") && !key.equals("") && !secret.equals("")){
                Intent myIntent = new Intent(FELauncher.this, DownloaderFE.class);
                myIntent.setAction(DownloaderFE.INTENT_ACTION_SEARCH_PRIVATE);
                myIntent.putExtra("search_criterion", searchText);
                myIntent.putExtra("oauth_token", key);
                myIntent.putExtra("oauth_token_secret", secret);
                startActivity(myIntent);
            }
            else{
                showNotAuthDialog();
            }
        }
        if(v == uploadButton){
            String searchText = settings.getString("saved_username", "");
            String key = settings.getString("saved_oauth_token", "");
            String secret = settings.getString("saved_oauth_token_secret", "");
            if(!searchText.equals("") && !key.equals("") && !secret.equals("")){
                Intent myIntent = new Intent(FELauncher.this, FEUpload.class);
                myIntent.putExtra("search_criterion", searchText);
                myIntent.putExtra("oauth_token", key);
                myIntent.putExtra("oauth_token_secret", secret);
                startActivity(myIntent);
            }
            else{
                showNotAuthDialog();
            }
        }

    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.fe_menu, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.fe_logout:
            editor.putString("saved_username", "");
            editor.putString("saved_oauth_token", "");
            editor.putString("saved_oauth_token_secret", "");
            editor.commit();
            restartActivity();
			return true;

	    }

	    return false;
	}


    private void showNotAuthDialog(){
        new AlertDialog.Builder(this)
            .setTitle(R.string.fe_not_login)
            .setMessage(R.string.fe_not_login_message)
            .setPositiveButton(R.string.ok_text, null)
            .show();
    }


    private void showSearchTagDialog(){
        final EditText et = new EditText(this);
        et.setText(settings.getString("fe_saved_search", ""));
        new AlertDialog.Builder(this)
            .setTitle(R.string.search_tag)
            .setMessage(R.string.fe_search_tag_message)
            .setView(et)
            .setPositiveButton(R.string.search_text, new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    String searchText = et.getText().toString();
                    editor.putString("fe_saved_search", searchText);
                    editor.commit();
                    Intent myIntent = new Intent(FELauncher.this, DownloaderFE.class);
                    myIntent.setAction(DownloaderFE.INTENT_ACTION_SEARCH_TAG);
                    myIntent.putExtra("search_criterion", searchText);
                    startActivity(myIntent);
                }
            })
            .setNegativeButton(R.string.cancel_text, null)
            .create()
            .show();
    }

    private void showSearchUserDialog(){
        final EditText et = new EditText(this);
        et.setText(settings.getString("fe_saved_user", ""));
        new AlertDialog.Builder(this)
            .setTitle(R.string.search_tag)
            .setMessage(R.string.fe_search_user_message)
            .setView(et)
            .setPositiveButton(R.string.search_text, new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    String searchText = et.getText().toString();
                    editor.putString("fe_saved_user", searchText);
                    editor.commit();
                    Intent myIntent = new Intent(FELauncher.this, DownloaderFE.class);
                    myIntent.setAction(DownloaderFE.INTENT_ACTION_SEARCH_USER);
                    myIntent.putExtra("search_criterion", searchText);
                    startActivity(myIntent);
                }
            })
            .setNegativeButton(R.string.cancel_text, null)
            .create()
            .show();
    }

}

