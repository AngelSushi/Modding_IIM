package world.nations.koth.runnable;

import java.util.List;

import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import world.nations.koth.data.KothData;

public class RunningKoth {
    private KothData koth;
    private Player capper;
    private List<Player> playersInside;

    public RunningKoth(KothData koth) {
        this.koth = koth;
        this.capper = null;
        this.playersInside = Lists.newArrayList();
    }

    public KothData getKoth() {
        return this.koth;
    }

    public Player getCapper() {
        return this.capper;
    }

    public List<Player> getPlayersInside() {
        return this.playersInside;
    }

    public void setKoth(KothData koth) {
        this.koth = koth;
    }

    public void setCapper(Player capper) {
        this.capper = capper;
    }

    public void setPlayersInside(List<Player> playersInside) {
        this.playersInside = playersInside;
    }
}
