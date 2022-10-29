package ru.rockstar.client.features.impl.movement;

import net.minecraft.network.play.client.CPacketPlayer;
import ru.rockstar.Main;
import ru.rockstar.api.event.EventTarget;
import ru.rockstar.api.event.event.EventPreMotionUpdate;
import ru.rockstar.api.event.event.EventStep;
import ru.rockstar.api.utils.world.TimerHelper;
import ru.rockstar.client.features.Category;
import ru.rockstar.client.features.Feature;
import ru.rockstar.client.features.impl.player.NoClip;
import ru.rockstar.client.ui.settings.impl.BooleanSetting;
import ru.rockstar.client.ui.settings.impl.ListSetting;
import ru.rockstar.client.ui.settings.impl.NumberSetting;

public class Step extends Feature {

    public static TimerHelper time = new TimerHelper();
    public static NumberSetting delay;
    public static NumberSetting heightStep;
    public static ListSetting stepMode;
    public BooleanSetting reverseStep;
    public boolean jump;
    boolean resetTimer;

    public Step() {
        super("Step", "������������� ���������� �� �����", 0, Category.MOVEMENT);
        stepMode = new ListSetting("Step Mode", "Motion", () -> true, "Motion", "Vanilla");
        delay = new NumberSetting("Step Delay", 0, 0, 1, 0.1F, () -> true);
        heightStep = new NumberSetting("Height", 1F, 1, 10, 0.5F, () -> true);
        reverseStep = new BooleanSetting("Reverse Step", false, () -> true);
        addSettings(stepMode, heightStep, delay, reverseStep);
    }

    @EventTarget
    public void onStep(EventStep step) {
        String mode = stepMode.getOptions();
        float delayValue = delay.getNumberValue() * 1000;
        float stepValue = heightStep.getNumberValue();
        if (Main.instance.featureDirector.getFeatureByClass(NoClip.class).isToggled()) {
            return;
        }
        double height = mc.player.getEntityBoundingBox().minY - mc.player.posY;
        boolean canStep = height >= 0.625F;
        if (canStep) {
            time.reset();
        }
        if (resetTimer) {
            resetTimer = false;
            mc.timer.timerSpeed = 1;
        }
        if (mode.equalsIgnoreCase("Motion")) {
            if (mc.player.isCollidedVertically && !mc.gameSettings.keyBindJump.isPressed() && time.hasReached(delayValue)) {
                step.setStepHeight(stepValue);
            }
            if (canStep) {
                mc.timer.timerSpeed = height > 1 ? 0.12F : 0.4F;
                resetTimer = true;
                ncpStep(height);
            }
        } else if (mode.equalsIgnoreCase("Vanilla")) {
            mc.player.stepHeight = heightStep.getNumberValue();
        }
    }

    private void ncpStep(double height) {
        double[] offset = {0.42, 0.333, 0.248, 0.083, -0.078};
        double posX = mc.player.posX;
        double posZ = mc.player.posZ;
        double y = mc.player.posY;
        if (height < 1.1) {
            double first = 0.42;
            double second = 0.75;
            mc.player.connection.sendPacket(new CPacketPlayer.Position(posX, y + first, posZ, false));
            if (y + second < y + height)
                mc.player.connection.sendPacket(new CPacketPlayer.Position(posX, y + second, posZ, true));
        } else if (height < 1.6) {
            for (double off : offset) {
                y += off;
                mc.player.connection.sendPacket(new CPacketPlayer.Position(posX, y, posZ, true));
            }
        } else if (height < 2.1) {
            double[] heights = {0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869};
            for (double off : heights) {
                mc.player.connection.sendPacket(new CPacketPlayer.Position(posX, y + off, posZ, true));
            }
        } else {
            double[] heights = {0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.907};
            for (double off : heights) {
                mc.player.connection.sendPacket(new CPacketPlayer.Position(posX, y + off, posZ, true));
            }
        }
    }

    @EventTarget
    public void onPreMotion(EventPreMotionUpdate event) {
        String mode = stepMode.getOptions();
        this.setSuffix(mode, true);
        if (mc.player.motionY > 0) {
            jump = true;
        } else if (mc.player.onGround) {
            jump = false;
        }
        if (reverseStep.getBoolValue() && !mc.gameSettings.keyBindJump.isKeyDown() && !mc.player.onGround && mc.player.motionY < 0 && mc.player.fallDistance < 1F && !jump) {
            mc.player.motionY = -1;
        }
    }

    @Override
    public void onDisable() {
        mc.player.stepHeight = 0.625F;
        mc.timer.timerSpeed = 1F;
        super.onDisable();
    }
}
