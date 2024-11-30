package net.modekh.tweasks.utils;

public enum Task {
    CHISELED_BOOKSHELF_CRAFT(1),
    AZALEA_LEAVES_PICKUP(1),
    HORSE_EQUIP(2),
    HORSE_EQUIP_ADVANCED(3),
    HORSE_EQUIP_DIAMOND(4),
    BAMBOO_RAFT_WITH_MOBS(2),
    DEATHS_LIMIT(-2),
    CAT_TAME_NAME(3),
    ITEM_GUESS(3),
    END(4);

    private final int reward;

    Task(int reward) {
        this.reward = reward;
    }

    public int getReward() {
        return reward;
    }
}
