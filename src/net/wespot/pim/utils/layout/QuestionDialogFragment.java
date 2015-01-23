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
import android.widget.EditText;
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

public class QuestionDialogFragment extends DialogFragment {

    public static final String DESCRIPTION = "description";
    public static final String TITLE = "title";
    public static final String TAGS = "tags";

    public interface QuestionDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    QuestionDialogListener mListener;

    private EditText dialog_title;
    private EditText dialog_description;
    private EditText dialog_tags;

    private String title;
    private String description;
    private String tags;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_create_question, null);

        builder.setView(view);

        dialog_title = (EditText)view.findViewById(R.id.new_question_title_dialog);
        dialog_description = (EditText)view.findViewById(R.id.new_question_description_dialog);
        dialog_tags = (EditText)view.findViewById(R.id.new_question_tags_dialog);

//        dialog_title.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//                if (countWords(dialog_title.getText().toString()) < 5){
//                    Toast.makeText(getActivity(), "Question should be longer", Toast.LENGTH_SHORT).show();
//                }
//                dialog_title.setFocusable(true);
//                dialog_title.setFocusableInTouchMode(true);
//                dialog_title.requestFocus();
//            }
//        });

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setPositiveButton(R.string.new_question_dialog_ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent a = getActivity().getIntent();
                        a.putExtra(TITLE, dialog_title.getText());
                        a.putExtra(DESCRIPTION, dialog_description.getText());
                        a.putExtra(TAGS, (dialog_tags.getText().toString() != "" ?  dialog_tags.getText() : "-"));

                        setTitle(dialog_title.getText().toString());
                        setDescription(dialog_description.getText().toString());

                        setTags(dialog_tags.getText().toString());

                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, a);
                    }
                }
        )
                .setNegativeButton(R.string.new_question_dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, getActivity().getIntent());
                    }
                });
        return builder.create();
    }


    public static int countWords(String s){

        int wordCount = 0;

        boolean word = false;
        int endOfLine = s.length() - 1;

        for (int i = 0; i < s.length(); i++) {
            // if the char is a letter, word = true.
            if (Character.isLetter(s.charAt(i)) && i != endOfLine) {
                word = true;
                // if char isn't a letter and there have been letters before,
                // counter goes up.
            } else if (!Character.isLetter(s.charAt(i)) && word) {
                wordCount++;
                word = false;
                // last word of String; if it doesn't end with a non letter, it
                // wouldn't count without this.
            } else if (Character.isLetter(s.charAt(i)) && i == endOfLine) {
                wordCount++;
            }
        }
        return wordCount;
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

    public void setDescription(String description) {
        this.description = description;
    }
    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}