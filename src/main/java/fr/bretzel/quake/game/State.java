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
package fr.bretzel.quake.game;

import org.bukkit.ChatColor;

/**
 * Created by MrBretzel on 20/06/2015.
 */
public enum State
{

    STARTED(ChatColor.RED + "Started"),
    ENDING(ChatColor.DARK_BLUE + "Started"),
    WAITING(ChatColor.GOLD + "Waiting");

    private final String name;

    State(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
}
