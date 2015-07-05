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
package fr.bretzel.quake;

/**
 * Created by MrBretzel on 21/06/2015.
 */

public class Chrono {

    private long tempsDepart = 0;
    private long tempsFin = 0;
    private long pauseDepart = 0;
    private long pauseFin = 0;
    private long duree = 0;

    public Chrono() {
        /*
         * Ses très le constructor de fout !
         */
    }

    public void start() {
        tempsDepart = System.currentTimeMillis();
        tempsFin = 0;
        pauseDepart = 0;
        pauseFin = 0;
        duree = 0;
    }

    public void pause() {
        if(tempsDepart == 0) {
            return;
        }
        pauseDepart = System.currentTimeMillis();
    }

    public void resume() {
        if(tempsDepart == 0) {
            return;
        }
        if(pauseDepart == 0) {
            return;
        }
        pauseFin = System.currentTimeMillis();
        tempsDepart = tempsDepart + pauseFin - pauseDepart;
        tempsFin = 0;
        pauseDepart = 0;
        pauseFin = 0;
        duree = 0;
    }

    public void stop()
    {
        if(tempsDepart == 0) {
            return;
        }
        tempsFin = System.currentTimeMillis();
        duree = (tempsFin-tempsDepart) - (pauseFin-pauseDepart);
        tempsDepart = 0;
        tempsFin = 0;
        pauseDepart = 0;
        pauseFin = 0;
    }

    public long getDureeSec() {
        return duree / 1000;
    }

    public long getDureeMs() {
        return duree;
    }

    public String getDureeTxt() {
        return timeToHMS(getDureeSec());
    }

    public int getHeure() {
        return (int) (getDureeSec() / 3600);
    }

    public int getMinute() {
        return (int) ((getDureeSec() % 3600) / 60);
    }

    public int getSeconde() {
        return (int) (getDureeSec() % 60);
    }

    public static String timeToHMS(long tempsS) {

        // IN : (long) temps en secondes
        // OUT : (String) temps au format texte : "1 h 26 min 3 s"

        int h = (int) (tempsS / 3600);
        int m = (int) ((tempsS % 3600) / 60);
        int s = (int) (tempsS % 60);

        String r="";

        if(h>0) {r+=h+" h ";}
        if(m>0) {r+=m+" min ";}
        if(s>0) {r+=s+" s";}
        if(h<=0 && m<=0 && s<=0) {r="0 s";}

        return r;
    }

}
