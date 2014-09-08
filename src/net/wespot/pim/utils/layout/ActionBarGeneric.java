package net.wespot.pim.utils.layout;

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
 * Date: 05/09/14
 * ****************************************************************************
 */

public interface ActionBarGeneric<T> {
    public void init();
    public void onDrawerClosed();
    public void onDrawerOpened();
    public void setTitle(CharSequence title);
    public void setSelectedNavigationItem(int pos);
    public void setNavigationMode(int a);
    public void addTab(T tab);
    public T newTab();
}
