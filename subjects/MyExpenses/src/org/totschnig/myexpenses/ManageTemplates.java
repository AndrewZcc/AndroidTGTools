/*   This file is part of My Expenses.
 *   My Expenses is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   My Expenses is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with My Expenses.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.totschnig.myexpenses;



import com.ozdroid.adapter.SimpleCursorTreeAdapter2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;

public class ManageTemplates extends ExpandableListActivity {
  //private static final int DELETE_CONFIRM_DIALOG_ID = 1;
  private MyExpandableListAdapter mAdapter;
  private ExpensesDbAdapter mDbHelper;
  private Cursor mAccountsCursor;

  private static final int DELETE_TEMPLATE = Menu.FIRST;
  private static final int CREATE_INSTANCE_EDIT = Menu.FIRST +1;
  private static final int CREATE_INSTANCE_SAVE = Menu.FIRST +2;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.manage_templates);
      setTitle(R.string.pref_manage_templates_title);
      mDbHelper = MyApplication.db();
      
      ((TextView) findViewById(android.R.id.empty)).setText(R.string.no_templates);

      mAccountsCursor = mDbHelper.fetchAccountAll();
      startManagingCursor(mAccountsCursor);
      mAdapter = new MyExpandableListAdapter(mAccountsCursor,
          this,
          android.R.layout.simple_expandable_list_item_1,
          android.R.layout.simple_expandable_list_item_1,
          new String[]{"label"},
          new int[] {android.R.id.text1},
          new String[] {"title"},
          new int[] {android.R.id.text1});

  setListAdapter(mAdapter);
  registerForContextMenu(getExpandableListView());
  }
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) menuInfo;
    int type = ExpandableListView
            .getPackedPositionType(info.packedPosition);

    // Menu entries relevant only for the group
    if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
      menu.add(0,DELETE_TEMPLATE,0,R.string.menu_delete);
      menu.add(0,CREATE_INSTANCE_EDIT,0,R.string.menu_create_transaction_from_template_and_edit);
      menu.add(0,CREATE_INSTANCE_SAVE,0,R.string.menu_create_transaction_from_template_and_save);
    }
  }
  @Override
  public boolean onContextItemSelected(MenuItem item) {
      long id;
      ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item.getMenuInfo();
      int type = ExpandableListView.getPackedPositionType(info.packedPosition);
      if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {         
        Cursor childCursor = (Cursor) mAdapter.getChild(
            ExpandableListView.getPackedPositionGroup(info.packedPosition),
            ExpandableListView.getPackedPositionChild(info.packedPosition)
        );
        id =  childCursor.getLong(childCursor.getColumnIndexOrThrow("_id"));
        switch(item.getItemId()) {
          case DELETE_TEMPLATE:   
            mDbHelper.deleteTemplate(id);
            mAccountsCursor.requery();
            return true;
          case CREATE_INSTANCE_EDIT:
            Intent i = new Intent(this, ExpenseEdit.class);
            i.putExtra("template_id", id);
            startActivity(i);
            return true;
          case CREATE_INSTANCE_SAVE:
            if (Transaction.getInstanceFromTemplate(id).save() == -1)
              Toast.makeText(getBaseContext(),getString(R.string.save_transaction_error), Toast.LENGTH_LONG).show();
            else
              Toast.makeText(getBaseContext(),getString(R.string.save_transaction_success), Toast.LENGTH_LONG).show();
        }
      }
      return false;
    }

  
  /*
 @Override
  public boolean onChildClick (ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
    //Log.w("SelectCategory","group = " + groupPosition + "; childPosition:" + childPosition);
    mDeleteTemplateId = id;
    Cursor childCursor = (Cursor) mAdapter.getChild(groupPosition,childPosition);
    mDeleteTemplateTitle = childCursor.getString(childCursor.getColumnIndexOrThrow("title"));
    showDialog(0);
    return true;
  }
@Override
  protected Dialog onCreateDialog(final int id) {
    return new AlertDialog.Builder(this)
    .setMessage(getString(R.string.dialog_confirm_delete_template,mDeleteTemplateTitle))
    .setCancelable(false)
    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
          mDbHelper.deleteTemplate(mDeleteTemplateId);
          mAccountsCursor.requery();
        }
    })
    .setNegativeButton(android.R.string.no, null).create();
  }
 
  //safeguard for orientation change during dialog
  @Override
  protected void onSaveInstanceState(Bundle outState) {
   super.onSaveInstanceState(outState);
   outState.putLong("DeleteTemplateId", mDeleteTemplateId);
   outState.putString("DeleteTemplateTitle", mDeleteTemplateTitle);
  }
  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
   super.onRestoreInstanceState(savedInstanceState);
   mDeleteTemplateId = savedInstanceState.getLong("DeleteTemplateId");
   mDeleteTemplateTitle = savedInstanceState.getString("DeleteTemplateTitle");
  }*/

  public class MyExpandableListAdapter extends SimpleCursorTreeAdapter2 {
    
    public MyExpandableListAdapter(Cursor cursor, Context context, int groupLayout,
            int childLayout, String[] groupFrom, int[] groupTo, String[] childrenFrom,
            int[] childrenTo) {
        super(context, cursor, groupLayout, groupFrom, groupTo, childLayout, childrenFrom,
                childrenTo);
    }
    /* (non-Javadoc)
     * returns a cursor with the subcategories for the group
     * @see android.widget.CursorTreeAdapter#getChildrenCursor(android.database.Cursor)
     */
    @Override
    protected Cursor getChildrenCursor(Cursor groupCursor) {
        // Given the group, we return a cursor for all the children within that group
      long account_id = groupCursor.getLong(mAccountsCursor.getColumnIndexOrThrow(ExpensesDbAdapter.KEY_ROWID));
      Cursor itemsCursor = mDbHelper.fetchTemplates(account_id);
      startManagingCursor(itemsCursor);
      return itemsCursor;
    }
  }
}
