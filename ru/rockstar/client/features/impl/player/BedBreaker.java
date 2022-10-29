package ru.rockstar.client.features.impl.player;

import java.awt.Color;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import ru.rockstar.api.event.EventTarget;
import ru.rockstar.api.event.event.Event3D;
import ru.rockstar.api.event.event.EventPreMotionUpdate;
import ru.rockstar.api.utils.combat.RotationHelper;
import ru.rockstar.api.utils.render.DrawHelper;
import ru.rockstar.api.utils.world.TimerHelper;
import ru.rockstar.client.features.Category;
import ru.rockstar.client.features.Feature;
import ru.rockstar.client.ui.settings.impl.ListSetting;
import ru.rockstar.client.ui.settings.impl.NumberSetting;

public class BedBreaker extends Feature {
	public static NumberSetting rad;
    private final NumberSetting fuckerDelay;
    private final ListSetting mode;
    private final TimerHelper timerUtils = new TimerHelper();
    private int xPos;
    private int yPos;
    private int zPos;
    private int blockid;
    public BedBreaker() {
        super("BedBreaker", "������������� ����� ������� � ����� ������ �����", 0, Category.PLAYER);
        mode = new ListSetting("Block", "Bed", () -> true, "Bed", "Cake");
        rad = new NumberSetting("Brekaer Radius", 4, 1, 6, 0.5F, () -> true);
        fuckerDelay = new NumberSetting("Breaker Delay", 100, 0, 1000, 50, () -> true);
        addSettings(mode, rad, fuckerDelay);
    }
    Minecraft mc = Minecraft.getMinecraft();

    @EventTarget
    public void onPreMotion(EventPreMotionUpdate event) {
        float radius = rad.getNumberValue();
        for (int x = (int) -radius; x < radius; x++) {
            for (int y = (int) radius; y > -radius; y--) {
                for (int z = (int) -radius; z < radius; z++) {
                    this.xPos = (int) mc.player.posX + x;
                    this.yPos = (int) mc.player.posY + y;
                    this.zPos = (int) mc.player.posZ + z;
                    BlockPos blockPos = new BlockPos(this.xPos, this.yPos, this.zPos);
                    Block block = mc.world.getBlockState(blockPos).getBlock();
                    switch (mode.getOptions()) {
                        case "Bed":
                            blockid = 26;
                            break;
                        case "Cake":
                            blockid = 354;
                            break;
                    }
                    if (Block.getIdFromBlock(block) != blockid)
                        continue;
                    if (!(block == null && blockPos == null)) {
                        float[] rotations = RotationHelper.getRotationVector(new Vec3d(blockPos.getX() + 0.5F, blockPos.getY() + 0.5F, blockPos.getZ() + 0.5F), true, 2, 2, 360);
                        event.setYaw(rotations[0]);
                        event.setPitch(rotations[1]);
                        mc.player.renderYawOffset = rotations[0];
                        mc.player.rotationYawHead = rotations[0];
                        mc.player.rotationPitchHead = rotations[1];
                        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos, mc.player.getHorizontalFacing()));
                        if (timerUtils.hasReached(fuckerDelay.getNumberValue())) {
                            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, mc.player.getHorizontalFacing()));
                            mc.player.swingArm(EnumHand.MAIN_HAND);
                            timerUtils.reset();
                        }
                    }
                }
            }
        }
    }

    @EventTarget
    public void onRender3D(Event3D event) {
        int playerX = (int) mc.player.posX;
        int playerZ = (int) mc.player.posZ;
        int playerY = (int) mc.player.posY;
        int range = (int) rad.getNumberValue();
        for (int y = playerY - range; y <= playerY + range; y++) {
            for (int x = playerX - range; x <= playerX + range; x++) {
                for (int z = playerZ - range; z <= playerZ + range; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (mc.world.getBlockState(pos).getBlock() == Blocks.BED) {
                        if (pos != null && mc.world.getBlockState(pos).getBlock() != Blocks.AIR) {
                            DrawHelper.blockEsp(pos, Color.RED, true);
                        }
                    }
                }
            }
        }
    }
}
