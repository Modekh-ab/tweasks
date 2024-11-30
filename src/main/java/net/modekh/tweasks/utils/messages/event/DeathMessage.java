package net.modekh.tweasks.utils.messages.event;

import net.modekh.tweasks.utils.messages.MessageUtils;
import net.modekh.tweasks.utils.messages.event.base.EventMessage;

public enum DeathMessage implements EventMessage {
    MSG_0("Yeah, take your award!");

    private final String message;

    DeathMessage(String message) {
        this.message = message;
    }

    @Override
    public String get() {
        return message;
    }

    public static EventMessage next(DeathMessage deathMessage) {
        return MessageUtils.getRandom(values(), deathMessage.ordinal());
    }
}
