package com.slackow.endfight.speedrunigt;

import com.redlimerl.speedrunigt.api.SpeedRunIGTApi;
import com.redlimerl.speedrunigt.timer.category.RunCategory;

public class EndFightCategory implements SpeedRunIGTApi {
    public static final RunCategory END_FIGHT_CATEGORY =
            new RunCategory("end_fight", "https://github.com/Slackow/EndFightMod---Fabric", "End Fight", null, null, false, false, true, timer -> false);


    @Override
    public RunCategory registerCategory() {
        return END_FIGHT_CATEGORY;
    }
}