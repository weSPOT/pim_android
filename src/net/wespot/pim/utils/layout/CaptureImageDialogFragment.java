package net.wespot.pim.utils.layout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.widget.EditText;
import net.wespot.pim.R;
import org.celstec.arlearn.delegators.INQ;
import org.celstec.arlearn2.android.dataCollection.PictureManager;
import org.celstec.arlearn2.android.util.MediaFolders;
import org.celstec.dao.gen.GeneralItemLocalObject;

import java.io.File;

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

public class CaptureImageDialogFragment extends DialogFragment {

    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    private PictureManager pictureManager;
    private GeneralItemLocalObject generalItemLocalObject;
    public static final int PICTURE_RESULT = 1;


    public CaptureImageDialogFragment(Context con, PictureManager man_pic, GeneralItemLocalObject genObject) {
        pictureManager = man_pic;
        generalItemLocalObject = genObject;
    }


    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    NoticeDialogListener mListener;

    private EditText dialog_title;
    private EditText dialog_description;

    private String title;
    private String description;
    private String imgPath;

    private File bitmapFile;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setTitle(R.string.data_collection_dialog_choose_action)
                .setItems(R.array.capturing_picture, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                pictureManager.setRunId(INQ.inquiry.getCurrentInquiry().getRunId());
                                pictureManager.setGeneralItem(generalItemLocalObject);
                                pictureManager.takeDataSample(null);
                                break;
                            case 1:
                                Intent cameraIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                bitmapFile = MediaFolders.createOutgoingJpgFile();
                                cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(bitmapFile));
                                startActivityForResult(cameraIntent, PICTURE_RESULT);
                                break;
                        }
                    }
                });
        return builder.create();
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

//        if (resultCode == Activity.RESULT_OK) {
//            Uri uri = null;
//            String filePath = null;
//            if (data != null) {
//                uri = data.getData();
//                filePath = data.getData().getPath();
//            } else {
//                uri = Uri.fromFile(bitmapFile);
//                filePath = bitmapFile.getAbsolutePath();
//            }
//            response.setUriAsString(uri.toString());
//
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = true;
//
//            BitmapFactory.decodeFile(filePath, options);
//            response.setContentType(options.outMimeType);
//            response.setWidth(options.outWidth);
//            response.setHeight(options.outHeight);
//
//            saveResponseForSyncing();
//        } else if (resultCode == Activity.RESULT_CANCELED) {
//            // User cancelled the image capture
//        } else {
//            // Image capture failed, advise user
//        }
    }
}