package com.example.zumoappname;

/**
 * Popola la lista con il dato appena salvato sul DB nella tabella DevicesLightPointsTemp
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;



public class ToDoItemAdapter extends ArrayAdapter<DevicesLightPointsTemp> {

    /**
     * Adapter context
     */
    Context mContext;

    /**
     * Adapter View layout
     */
    int mLayoutResourceId;

    public ToDoItemAdapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);

        mContext = context;
        mLayoutResourceId = layoutResourceId;
    }




    /**
     * Returns the view for a specific item on the list
     */

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;


        //final ToDoItem currentItem = getItem(position);
        final DevicesLightPointsTemp currentItem = getItem(position);


        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(mLayoutResourceId, parent, false);
        }

        row.setTag(currentItem);
        final CheckBox checkBox = (CheckBox) row.findViewById(R.id.checkToDoItem);
        checkBox.setText(ToDoActivity.mTextNewToDo.getText());

        checkBox.setChecked(false);
        checkBox.setEnabled(true);

        // TODO forse è meglio pulire la lista premendo un tasto "ok"

        checkBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (checkBox.isChecked()) {
                    checkBox.setText("OPERAZIONE COMPLETATA");
                    checkBox.setTextColor(Color.parseColor("#14ad11"));
                    checkBox.setEnabled(false);
                    if (mContext instanceof ToDoActivity) {
                        ToDoActivity activity = (ToDoActivity) mContext;
                        activity.checkItem(currentItem);
                    }
//                    Intent intent = new Intent(mContext,com.example.zumoappname.ConnectionClass.class);
//                    mContext.startActivity(intent);

                }
            }
        });

        return row;


    }

}