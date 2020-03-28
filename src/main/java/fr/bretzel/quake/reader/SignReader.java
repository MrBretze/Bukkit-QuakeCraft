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
package fr.bretzel.quake.reader;

import fr.bretzel.nbt.NBTTagCompound;
import fr.bretzel.quake.Util;
import org.bukkit.Location;
import org.bukkit.block.Sign;

/**
 * Created by MrBretzel on 19/06/2015.
 */

public class SignReader
{

    public static Sign read(NBTTagCompound compound)
    {
        Sign sign = null;
        try
        {
            Location location = Util.toLocationString(compound.getString("location"));
            sign = (Sign) location.getBlock().getState();

            sign.setLine(0, compound.getString("line1"));
            sign.setLine(1, compound.getString("line2"));
            sign.setLine(2, compound.getString("line3"));
            sign.setLine(3, compound.getString("line4"));

            sign.update();
        } catch (Exception e)
        {
            e.fillInStackTrace();
        }
        return sign;
    }

    public static NBTTagCompound write(Sign sign)
    {
        NBTTagCompound compound = new NBTTagCompound();
        try
        {
            sign = (Sign) sign.getLocation().getWorld().getBlockAt(sign.getLocation()).getState();

            compound.setString("line1", sign.getLine(0));
            compound.setString("line2", sign.getLine(1));
            compound.setString("line3", sign.getLine(2));
            compound.setString("line4", sign.getLine(3));

            compound.setString("location", Util.toStringLocation(sign.getLocation()));
        } catch (Exception e)
        {
            e.fillInStackTrace();
        }
        return compound;
    }
}
