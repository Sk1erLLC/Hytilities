/*
 * Hytilities - Hypixel focused Quality of Life mod.
 * Copyright (C) 2020  Sk1er LLC
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

package club.sk1er.hytilities.handlers.chat;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Module to simplify the reading of send chat messages and using them if modifying
 * or cancelling them is not necessary.
 */
public abstract class SendingReaderModule implements ChatSendModule {

    @Nullable
    @Override public String onMessageSend(@NotNull final String message) {
        readMessage(message);
        return message;
    }

    /**
     * Place your code here that reads the message and acts on it.
     *
     * @param message a chat message being sent
     */
    public abstract void readMessage(final String message);

}
