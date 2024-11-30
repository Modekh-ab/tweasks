package net.modekh.tweasks.utils.messages;

import net.modekh.tweasks.utils.messages.event.base.EventMessage;

public class MessageUtils {
    public static EventMessage getRandom(EventMessage[] values, int messageOrd) {
        int nextOrd;

        do {
            nextOrd = (int) (Math.random() * (values.length - 1));
        } while (messageOrd == nextOrd);

        return values[nextOrd];
    }
}
