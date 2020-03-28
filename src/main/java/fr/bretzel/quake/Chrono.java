/**
 * Copyright 2015 Lo�c Nussbaumer
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License. See accompanying
 * LICENSE file.
 */
package fr.bretzel.quake;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by MrBretzel on 21/06/2015.
 */

public class Chrono
{

    private long begin, end;

    private final NumberFormat format = new DecimalFormat("#");

    public void start()
    {
        begin = System.currentTimeMillis();
    }

    public void stop()
    {
        end = System.currentTimeMillis();
    }

    public long getTime()
    {
        return end - begin;
    }

    public long getMilliseconds()
    {
        return end - begin;
    }

    public int getSeconds()
    {
        return Integer.parseInt(format.format((end - begin) / 1000.0));
    }

    public int getMinutes()
    {
        return Integer.parseInt(format.format((end - begin) / 60000.0));
    }

    public int getHours()
    {
        return Integer.parseInt(format.format((end - begin) / 3600000.0));
    }
}
