package src;

public enum MonsterType {
    Troll,
    TX5;

    public String getImageName() {
        switch (this) {
            case Troll: return "m_troll.gif";
            case TX5: return "m_tx5.gif";
            default: {
                assert false;
            }
        }
        return null;
    }
}
