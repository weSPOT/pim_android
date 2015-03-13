package net.wespot.pim;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.celstec.arlearn.delegators.INQ;
import org.celstec.arlearn2.android.db.PropertiesAdapter;
import org.celstec.arlearn2.client.exception.ARLearnException;
import org.celstec.arlearn2.network.ConnectionFactory;
import org.celstec.arlearn2.network.HttpConnection;
import org.codehaus.jettison.json.JSONObject;

/**
 * ****************************************************************************
 * Copyright (C) 2015 Open Universiteit Nederland
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
 * Date: 02/03/15
 * ****************************************************************************
 */

public class TestingClass {

    public static String answers(String token, long inquiryId) {
        PropertiesAdapter pa = PropertiesAdapter.getInstance();
        HttpConnection conn = ConnectionFactory.getConnection();
        token = pa.getAuthToken();
        String url = getUrlPrefix();
        url += "&api_key="+ INQ.config.getProperty("elgg_api_key")+"&inquiryId="+inquiryId+"&method=inquiry.answers";
        HttpResponse response = conn.executeGET(url, token, "application/json");
        try {
//            return EntityUtils.toString(response.getEntity());
            JSONObject json = new JSONObject(EntityUtils.toString(response.getEntity()));
            return json.toString();
        } catch (Exception e) {
            if (e instanceof ARLearnException) throw (ARLearnException) e;

        }
        return "error";
    }

    public static String getUrlPrefix() {
        return INQ.config.getProperty("wespot_server");
    }
}
