/**
 * Copyright 2015 Loïc Nussbaumer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License. See accompanying
 * LICENSE file.
 */
package fr.bretzel.quake.reader;

import fr.bretzel.nbt.NBTCompressedStreamTools;
import fr.bretzel.nbt.NBTTagCompound;
import fr.bretzel.quake.ParticleEffect;
import fr.bretzel.quake.PlayerInfo;

import java.io.FileInputStream;
import java.io.IOException;


/**
 * Created by MrBretzel on 25/06/2015.
 */
public class PlayerInfoReader {

    public static NBTTagCompound write (PlayerInfo playerInfo) {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("effect", playerInfo.getEffect().getName());
        compound.setDouble("reload", playerInfo.getReloadTime());
        compound.setInt("playerKill", playerInfo.getPlayerkill());
        compound.setInt("coins", playerInfo.getCoins());
        return compound;
    }

    public static void read (PlayerInfo player) {
        NBTTagCompound compound = new NBTTagCompound();
        try {
            compound = NBTCompressedStreamTools.read(new FileInputStream(player.getFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (compound.hasKey("effect")) {
            player.setEffect(ParticleEffect.fromName(compound.getString("effect")));
        } else {
            player.setEffect(ParticleEffect.FIREWORKS_SPARK);
        }
        if (compound.hasKey("reload")) {
            player.setReloadTime(compound.getDouble("reload"));
        }
        if (compound.hasKey("playerKill")) {
            player.setPlayerkill(compound.getInt("playerKill"));
        }
    }
}
