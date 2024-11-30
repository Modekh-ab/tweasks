package net.modekh.tweasks.utils.messages.event;

import net.modekh.tweasks.utils.messages.MessageUtils;
import net.modekh.tweasks.utils.messages.event.base.EventMessage;

import javax.annotation.Nullable;

public enum DeathMessage implements EventMessage {
    MSG_0("Broo....");

    private final String message;

    DeathMessage(String message) {
        this.message = message;
    }

    @Override
    public String get() {
        return message;
    }

    public static EventMessage next(@Nullable DeathMessage deathMessage) {
        return MessageUtils.getRandom(values(), deathMessage != null ? deathMessage.ordinal() : 0);
    }
}
