package it.unina.android.ripper.constants;

import android.view.View;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;

/**
 * Detect SimpleType of a Widget
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class RipperSimpleType extends it.unina.android.ripper.constants.SimpleType {

	/**
	 * Detect SimpleType of a Widget
	 * 
	 * @param v Widget
	 * @return
	 */
	public static String getSimpleType(View v)
	{
		String type = v.getClass().getName();
		if (type.endsWith("null")) return NULL;
		if (type.endsWith("RadioButton")) return RADIO;
		if (type.endsWith("RadioGroup")) return RADIO_GROUP;
		if (type.endsWith("CheckBox") || type.endsWith("CheckedTextView")) return CHECKBOX;
		if (type.endsWith("ToggleButton")) return TOGGLE_BUTTON;
		if (type.endsWith("IconMenuView") || type.endsWith("ActionMenuView")) return MENU_VIEW;
		if (type.endsWith("IconMenuItemView") || type.endsWith("ActionMenuItemView")) return MENU_ITEM;
		if (type.endsWith("DatePicker")) return DATE_PICKER;
		if (type.endsWith("TimePicker")) return TIME_PICKER;
		if (type.endsWith("DialogTitle")) return DIALOG_VIEW;
		if (type.endsWith("Button")) return BUTTON;
		if (type.endsWith("EditText")) return EDIT_TEXT;
		if (type.endsWith("SearchAutoComplete")) return SEARCH_BAR;
		if (type.endsWith("Spinner")) {
			Spinner s = (Spinner)v;
			if (s.getCount() == 0) return EMPTY_SPINNER;
			return SPINNER;
		}
		if (type.endsWith("SeekBar")) return SEEK_BAR;
		if (v instanceof RatingBar && (!((RatingBar)v).isIndicator())) return RATING_BAR;
		if (type.endsWith("TabHost")) return TAB_HOST;
		//if (type.endsWith("ExpandedMenuView") || type.endsWith("AlertController$RecycleListView")) { return EXPAND_MENU; }
		if (type.endsWith("ListView") || type.endsWith("ExpandedMenuView")) {
			ListView l = (ListView)v;
			if (l.getCount() == 0) return EMPTY_LIST;
			
			if (l.getAdapter().getClass().getName().endsWith("PreferenceGroupAdapter")) {
				return PREFERENCE_LIST;
			}
			
			switch (l.getChoiceMode()) {
				case ListView.CHOICE_MODE_NONE: return LIST_VIEW;
				case ListView.CHOICE_MODE_SINGLE: return SINGLE_CHOICE_LIST;
				case ListView.CHOICE_MODE_MULTIPLE: return MULTI_CHOICE_LIST;
			}
		}
		
		if (type.endsWith("AutoCompleteTextView")) return AUTOCOMPLETE_TEXTVIEW;
		if (type.endsWith("TextView")) return TEXT_VIEW;
		
		if (type.endsWith("ImageView")) return IMAGE_VIEW;
		if (type.endsWith("LinearLayout")) return LINEAR_LAYOUT;
		if (type.endsWith("RelativeLayout")) return RELATIVE_LAYOUT;
		if (type.endsWith("SlidingDrawer")) return SLIDING_DRAWER;
		if ((v instanceof WebView) || type.endsWith("WebView")) return WEB_VIEW;
		if (type.endsWith("TwoLineListItem")) return LIST_ITEM;
		if (type.endsWith("NumberPicker")) return NUMBER_PICKER;
		if (type.endsWith("NumberPickerButton")) return NUMBER_PICKER_BUTTON;
		
		return "";
	}
}
