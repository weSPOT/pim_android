package net.wespot.pim.controller.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import net.wespot.pim.R;
import net.wespot.pim.utils.images.ImageFetcher;
import net.wespot.pim.utils.layout.RecyclingImageView;
import org.celstec.arlearn2.android.listadapter.AbstractResponsesLazyListAdapter;
import org.celstec.dao.gen.GeneralItemLocalObject;
import org.celstec.dao.gen.ResponseLocalObject;

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
public class ResponsesLazyListAdapter extends AbstractResponsesLazyListAdapter {

    private static final String TAG = "ResponsesLazyListAdapter";
    private GridView.LayoutParams mImageViewLayoutParams;
    private int mItemHeight = 240;
    private int mNumColumns = 0;
    private ImageFetcher mImageFetcher;
    private Context mContext;
    private Bitmap mLoadingBitmap;


    public ResponsesLazyListAdapter(Context context) {
        super(context);
    }

    public ResponsesLazyListAdapter(Context context, ImageFetcher imageFetcher, GeneralItemLocalObject giLocalObject) {
        super(context, giLocalObject.getId());

        mContext = context;

        mImageFetcher = imageFetcher;
        mImageViewLayoutParams = new GridView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public View newView(Context context, ResponseLocalObject item, ViewGroup parent) {

        if (item == null) return null;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mImageViewLayoutParams = new GridView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return inflater.inflate(R.layout.entry_data_collection_response, null);
    }

    @Override
    public void bindView(View convertView, Context mContext, ResponseLocalObject responseLocalObject) {
        ImageView imageView;

        if (convertView == null){
            imageView = new RecyclingImageView(mContext);
        }else{
            imageView = (ImageView) convertView;
        }

        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, mItemHeight));

        if (responseLocalObject.isAudio()) {
            imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_task_record));
        } else if (responseLocalObject.isPicture()) {
            mImageFetcher.loadImage(responseLocalObject.getUri(), imageView);
        } else if (responseLocalObject.isVideo()) {
            imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_task_video));
        } else if (responseLocalObject.getValue() != null ) {
            imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_description));
        } else {
            imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.empty_photo));
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    /**
     * Sets the item height. Useful for when we know the column width so the height can be set
     * to match.
     *
     * @param height
     */
    public void setItemHeight(int height) {
        if (height == mItemHeight) {
            return;
        }
        mItemHeight = height;
        mImageViewLayoutParams =
                new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mItemHeight);
        mImageFetcher.setImageSize(height);
        notifyDataSetChanged();
    }

    public void setNumColumns(int numColumns) {
        mNumColumns = numColumns;
    }

    public int getNumColumns() {
        return mNumColumns;
    }
}
