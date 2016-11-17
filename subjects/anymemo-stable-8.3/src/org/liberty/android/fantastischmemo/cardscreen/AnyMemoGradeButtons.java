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
package org.liberty.android.fantastischmemo.cardscreen;

import org.liberty.android.fantastischmemo.*;

import android.widget.Button;
import android.view.View;
import java.util.HashMap;
import java.util.Map;
import android.content.Context;
import android.view.LayoutInflater;

public class AnyMemoGradeButtons extends ControlButtons{
    private Context mContext;
    private View buttonView;
    private Button grade0, grade1, grade2, grade3, grade4, grade5;
    private Map<String, Button> buttonMap;
    public AnyMemoGradeButtons(Context context){
        mContext = context;
        LayoutInflater factory = LayoutInflater.from(mContext);
        buttonView = factory.inflate(R.layout.grade_buttons_anymemo, null);
        grade0 = (Button)buttonView.findViewById(R.id.grade_btn_anymemo_0);
        grade1 = (Button)buttonView.findViewById(R.id.grade_btn_anymemo_1);
        grade2 = (Button)buttonView.findViewById(R.id.grade_btn_anymemo_2);
        grade3 = (Button)buttonView.findViewById(R.id.grade_btn_anymemo_3);
        grade4 = (Button)buttonView.findViewById(R.id.grade_btn_anymemo_4);
        grade5 = (Button)buttonView.findViewById(R.id.grade_btn_anymemo_5);
        buttonMap = new HashMap<String, Button>();
        buttonMap.put("0", grade0);
        buttonMap.put("1", grade1);
        buttonMap.put("2", grade2);
        buttonMap.put("3", grade3);
        buttonMap.put("4", grade4);
        buttonMap.put("5", grade5);
    }
    public Map<String, Button> getButtons(){
        return buttonMap;
    }
    public View getView(){
        return buttonView;
    }
}

