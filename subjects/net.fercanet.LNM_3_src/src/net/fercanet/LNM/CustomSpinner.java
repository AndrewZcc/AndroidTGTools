//   -----------------------------------------------------------------------------
//    Copyright 2010 Ferran Caellas Puig

//    This file is part of Learn Music Notes.
//
//    Learn Music Notes is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.

//    Learn Music Notes is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.

//    You should have received a copy of the GNU General Public License
//    along with Learn Music Notes.  If not, see <http://www.gnu.org/licenses/>.
//   -----------------------------------------------------------------------------


package net.fercanet.LNM;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;


public class CustomSpinner extends Spinner {
	
	public CustomSpinner(Context context, AttributeSet attrs) {
	    super(context, attrs);
	}   
	
	public CustomSpinner(Context context) {
	    super(context);
	}
//	public void setSelectionByItemId(AdapterView<?> parent, long id){
	public void setSelectionByItemId(long id){
		for (int i = 0; i < this.getCount(); i++) {              
	          long itemIdAtPosition = this.getItemIdAtPosition(i);
	          if (itemIdAtPosition == id) {
	        	  this.setSelection(i);
	        	  break;
	          }
		}
	}
	
	
}