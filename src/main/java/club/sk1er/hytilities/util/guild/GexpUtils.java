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
import club.sk1er.mods.core.util.JsonHolder;
import club.sk1er.mods.core.util.WebUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class GexpUtils {
    JsonHolder apiObj;
    public JsonArray guildMembers;
    public static GexpUtils instance = new GexpUtils();

    private static String getCurrentESTTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("EST"));
        return simpleDateFormat.format(new Date());
    }

    public String getGEXP() {
        String gexp = null;
        try {
            apiObj = WebUtil.fetchJSON("https://api.hypixel.net/guild?key=" + HytilitiesConfig.apiKey + ";player=" + Minecraft.getMinecraft().getSession().getPlayerID()); //Hypixel API only supports short uuids
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        try {
            guildMembers = apiObj.optJSONObject("guild").optJSONArray("members");
            for (JsonElement e : guildMembers) {
                if (e.getAsJsonObject().get("uuid").getAsString().equalsIgnoreCase(Minecraft.getMinecraft().getSession().getPlayerID())) {
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

    public String getGEXP(String username) {
        String gexp = null;
        String uuid;
        try {
            uuid = getUUID(username);
            apiObj = WebUtil.fetchJSON("https://api.hypixel.net/guild?key=" + HytilitiesConfig.apiKey + ";player=" + uuid);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        try {
            guildMembers = apiObj.optJSONObject("guild").optJSONArray("members");
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

    /**
     * Taken from Danker's Skyblock Mod under GPL 3.0 license
     * https://github.com/bowser0000/SkyblockMod/blob/master/LICENSE
     *
     * @author bowser0000
     */
    private static String getUUID(String username) {
        JsonObject uuidResponse = WebUtil.fetchJSON("https://api.mojang.com/users/profiles/minecraft/" + username).getObject();
        return uuidResponse.get("id").getAsString();
    }

}
