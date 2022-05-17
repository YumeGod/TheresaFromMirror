package cn.loli.client.module.modules.render;

import cn.loli.client.Main;
import cn.loli.client.events.EmoteEvent;
import cn.loli.client.events.Render2DEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.module.modules.misc.HUD;


import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import dev.xix.event.bus.IEventListener;
import dev.xix.property.impl.EnumProperty;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.EnumChatFormatting;

import java.util.Collection;
import java.util.List;

public class Scoreboard extends Module {

    private enum MODE {
        NORMAL("Normal"), ARRAYS("Arrays");

        private final String name;

        MODE(String s) {
            this.name = s;
        }

        @Override
        public String toString() {
            return name;
        }
    }
    
    EnumProperty mode = new EnumProperty<>("Position", MODE.ARRAYS);

    public Scoreboard() {
        super("Scoreboard", "Render the scoreboard", ModuleCategory.RENDER);
    }

    private final IEventListener<Render2DEvent> onEvent = event ->
    {
        net.minecraft.scoreboard.Scoreboard scoreboard = mc.theWorld.getScoreboard();
        ScaledResolution scaledRes = new ScaledResolution(mc);
        ScoreObjective objective = scoreboard.getObjectiveInDisplaySlot(1);
        Collection<Score> collection = scoreboard.getSortedScores(scoreboard.getObjectiveInDisplaySlot(1));
        List<Score> list = Lists.newArrayList(Iterables.filter(collection, p_apply_1_ -> p_apply_1_.getPlayerName() != null && !p_apply_1_.getPlayerName().startsWith("#")));

        if (list.size() > 15) {
            collection = Lists.newArrayList(Iterables.skip(list, collection.size() - 15));
        } else {
            collection = list;
        }

        int i = mc.fontRendererObj.getStringWidth(objective.getDisplayName());

        for (Score score : collection) {
            ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(score.getPlayerName());
            String s = ScorePlayerTeam.formatPlayerName(scoreplayerteam, score.getPlayerName()) + ": " + EnumChatFormatting.RED + score.getScorePoints();
            i = Math.max(i, mc.fontRendererObj.getStringWidth(s));
        }

        int i1 = collection.size() * mc.fontRendererObj.FONT_HEIGHT;
        int j1 = 0;
        if (mode.getPropertyValue().toString().equals("Normal")) {
            j1 = scaledRes.getScaledHeight() / 2 - i1 / 3;
        } else if (mode.getPropertyValue().toString().equals("Arrays")) {
            j1 = (int) Main.INSTANCE.moduleManager.getModule(HUD.class).maxY;
        }
        j1 += i1 + 4;
        int k1 = 3;
        int l1 = scaledRes.getScaledWidth() - i - k1;
        int j = 0;

        for (int b = 0; b <= collection.size(); b++) {
            int k = j1 - b * mc.fontRendererObj.FONT_HEIGHT;
            int l = scaledRes.getScaledWidth() - k1 + 2;
            if (b == collection.size()) {
                String s3 = objective.getDisplayName();
                Gui.drawRect(l1 - 2, k, l, k + mc.fontRendererObj.FONT_HEIGHT, 1610612736);
                Gui.drawRect(l1 - 2, k, l, k, 1342177280);
                mc.fontRendererObj.drawString(s3, l1 + i / 2 - mc.fontRendererObj.getStringWidth(s3) / 2, k, 553648127);
                return;
            }
            Score score1 = (Score) collection.toArray()[b];
            ScorePlayerTeam scoreplayerteam1 = scoreboard.getPlayersTeam(score1.getPlayerName());
            String s1 = ScorePlayerTeam.formatPlayerName(scoreplayerteam1, score1.getPlayerName());
            String s2 = EnumChatFormatting.RED + "" + score1.getScorePoints();
            Gui.drawRect(l1 - 2, k, l, k + mc.fontRendererObj.FONT_HEIGHT, 1342177280);
            mc.fontRendererObj.drawString(s1, l1, k, 553648127);
            mc.fontRendererObj.drawString(s2, l - mc.fontRendererObj.getStringWidth(s2), k, 553648127);

        }
    };

}
