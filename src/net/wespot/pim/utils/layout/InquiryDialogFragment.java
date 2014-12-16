package net.wespot.pim.utils.layout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import net.wespot.pim.R;
import net.wespot.pim.view.PimInquiriesFragment;

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

public class InquiryDialogFragment extends DialogFragment {

    public static final String DESCRIPTION = "description";
    public static final String TITLE = "title";
    public static final String TAGS = "tags";

    protected static InquiryDialogFragment instance;

    public static DialogFragment newInstance() {
        DialogFragment frag = new InquiryDialogFragment();
        Bundle args = new Bundle();
//        args.putInt("title", title);
        frag.setArguments(args);
        return frag;
    }

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

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        return new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.ic_launcher)
                .setTitle("New inquiry")
                .setPositiveButton(R.string.new_question_dialog_ok,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ((PimInquiriesFragment)getActivity()).doPositiveClick();
                            }
                        }
                )
                .setNegativeButton(R.string.new_question_dialog_cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ((PimInquiriesFragment)getActivity()).doNegativeClick();
                            }
                        }
                ).create();

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