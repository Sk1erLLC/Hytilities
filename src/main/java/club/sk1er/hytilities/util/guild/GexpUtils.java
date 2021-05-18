/*
 * Hytilities - Hypixel focused Quality of Life mod.
 * Copyright (C) 2021  Sk1er LLC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package club.sk1er.hytilities.util.guild;

import club.sk1er.hytilities.config.HytilitiesConfig;
import club.sk1er.hytilities.util.APIUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class GexpUtils {
    JsonObject apiObj;
    public JsonArray guildMembers;
    long lastRequest = System.currentTimeMillis();
    public static GexpUtils instance = new GexpUtils();

    private static String getCurrentESTTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("EST"));
        return simpleDateFormat.format(new Date());
    }

    public String getGEXP() {
        String gexp = null;
        try {
            apiObj = APIUtils.getJSONResponse("https://api.hypixel.net/guild?key=" + HytilitiesConfig.apiKey + ";player=" + Minecraft.getMinecraft().getSession().getPlayerID()); //Hypixel API only supports short uuids
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        try {
            guildMembers = apiObj.get("guild").getAsJsonObject().get("members").getAsJsonArray();
            for (JsonElement e : guildMembers) {
                if (e.getAsJsonObject().get("uuid").getAsString().equalsIgnoreCase(Minecraft.getMinecraft().getSession().getPlayerID())) {
                    gexp = e.getAsJsonObject().get("expHistory").getAsJsonObject().get(getCurrentESTTime()).getAsString();
                    break;
                }
            }
            lastRequest = System.currentTimeMillis();
            return gexp;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getGEXP(String username) {
        String gexp = null;
        String uuid;
        try {
            apiObj = APIUtils.getJSONResponse("https://api.hypixel.net/guild?key=" + HytilitiesConfig.apiKey + ";player=" + APIUtils.getUUID(username));
            uuid = APIUtils.getUUID(username);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        try {
            guildMembers = apiObj.get("guild").getAsJsonObject().get("members").getAsJsonArray();
            for (JsonElement e : guildMembers) {
                if (e.getAsJsonObject().get("uuid").getAsString().equalsIgnoreCase(uuid)) {
                    gexp = e.getAsJsonObject().get("expHistory").getAsJsonObject().get(getCurrentESTTime()).getAsString();
                    break;
                }
            }
            return gexp;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
