package net.modekh.tweasks.utils.messages;

public class MessageUtils {
    public static RewardMessage getRandom(RewardMessage[] values, int messageOrd) {
        int nextOrd;

        do {
            nextOrd = (int) (Math.random() * (values.length - 1));
        } while (messageOrd == nextOrd);

        return values[nextOrd];
    }
}
