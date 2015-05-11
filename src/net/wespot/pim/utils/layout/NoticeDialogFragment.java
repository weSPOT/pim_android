package net.wespot.pim.utils.layout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import net.wespot.pim.R;

/**
 * ****************************************************************************
 * Copyright (C) 2014 Open Universiteit Nederland
 * <p/>
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * <p/>
 * Contributors: Angel Suarez
 * ****************************************************************************
 */

public class NoticeDialogFragment extends DialogFragment implements AdapterView.OnItemSelectedListener {

    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";



    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    NoticeDialogListener mListener;

    private EditText dialog_title;
    private EditText dialog_description;
    private Spinner dialog_type_dc;

    private String title;
    private String description;

    private boolean audio;
    private boolean image;
    private boolean video;
    private boolean text;
    private boolean number;



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_create_data_collection_task, null);

        builder.setView(view);

        dialog_title = (EditText)view.findViewById(R.id.data_collection_dialog_title);
        dialog_description = (EditText)view.findViewById(R.id.data_collection_dialog_description);

        dialog_type_dc = (Spinner) view.findViewById(R.id.data_collection_dialog_type);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.data_collection_type, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        // Apply the adapter to the spinner
        dialog_type_dc.setAdapter(adapter);

        dialog_type_dc.setOnItemSelectedListener(this);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setPositiveButton(R.string.data_collection_dialog_ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent a = getActivity().getIntent();
                        a.putExtra(TITLE, dialog_title.getText());
                        a.putExtra(DESCRIPTION, dialog_description.getText());

                        setTitle(dialog_title.getText().toString());
                        setDescription(dialog_description.getText().toString());
                        setAudio(audio);
                        setVideo(video);
                        setImage(image);
                        setText(text);
                        setNumber(number);

                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, a);
                    }
                }
        )
                .setNegativeButton(R.string.data_collection_dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, getActivity().getIntent());
                    }
                });
        return builder.create();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        parent.getItemAtPosition(position);

        audio = false;
        video = false;
        image = false;
        text = false;
        number = false;


        if (parent.getSelectedItem().equals("Audio")){
            audio = true;
        }
        if (parent.getSelectedItem().equals("Video")){
            video = true;
        }
        if (parent.getSelectedItem().equals("Picture")){
            image = true;
        }
        if (parent.getSelectedItem().equals("Text")){
            text = true;
        }
        if (parent.getSelectedItem().equals("Number")){
            number = true;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }


    public boolean isNumber() {
        return number;
    }

    public void setNumber(boolean number) {
        this.number = number;
    }

    public boolean isAudio() {
        return audio;
    }

    public void setAudio(boolean audio) {
        this.audio = audio;
    }

    public boolean isImage() {
        return image;
    }

    public void setImage(boolean image) {
        this.image = image;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public boolean isText() {
        return text;
    }

    public void setText(boolean text) {
        this.text = text;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    //    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        // Verify that the host activity implements the callback interface
//        try {
//            // Instantiate the NoticeDialogListener so we can send events to the host
//            mListener = (NoticeDialogListener) activity;
//        } catch (ClassCastException e) {
//            // The activity doesn't implement the interface, throw exception
//            throw new ClassCastException(activity.toString()
//                    + " must implement NoticeDialogListener");
//        }
//    }
}