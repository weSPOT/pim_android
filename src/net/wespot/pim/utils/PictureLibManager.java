package org.celstec.arlearn2.android.dataCollection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;
import java.util.ArrayList;

/**
 * ****************************************************************************
 * Copyright (C) 2013 Open Universiteit Nederland
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
 * Contributors: Stefaan Ternier
 * ****************************************************************************
 */
public class PictureLibManager extends DataCollectionManager {

    private File bitmapFile;
    private Activity context;
    private ArrayList<Uri> imageUris;


    public PictureLibManager(Activity ctx) {

        super(ctx);
        response.setPictureType();
    }

    @Override
    public void takeDataSample(Class className) {

    }

    @Override
    public void takeDataSample(Class className, String message) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    public void uploadPicture(Uri data) {

        response.setUriAsString(data.toString());

        response.setContentType("image/jpeg");
        response.setWidth(1024);
        response.setHeight(720);

        saveResponseForSyncing();
    }
}
