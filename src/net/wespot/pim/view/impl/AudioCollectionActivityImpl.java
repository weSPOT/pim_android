package net.wespot.pim.view.impl;

import net.wespot.pim.R;
import org.celstec.arlearn2.android.dataCollection.activities.AudioCollectionActivity;

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
public class AudioCollectionActivityImpl extends AudioCollectionActivity{
    public  int getGameGeneralitemAudioInput() {
        return R.layout.game_general_item_dc_audio_input;
    }

    @Override
    public int getAudioFeedbackView() {
        return R.id.audioFeedbackView;
    }

    @Override
    public int getStartRecordingButton() {
        return R.id.startRecording;
    }

    @Override
    public int getStopRecordingButton() {
        return R.id.stopRecording;
    }
}
