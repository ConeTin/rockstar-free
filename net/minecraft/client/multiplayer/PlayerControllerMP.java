/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  io.netty.buffer.Unpooled
 */
package net.minecraft.client.multiplayer;

import io.netty.buffer.Unpooled;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCommandBlock;
import net.minecraft.block.BlockStructure;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.*;
import net.minecraft.stats.RecipeBook;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import ru.rockstar.api.event.event.EventAttackSilent;
import ru.rockstar.api.event.event.EventBlockInteract;


public class PlayerControllerMP {
    private final Minecraft mc;
    private final NetHandlerPlayClient connection;
    private BlockPos currentBlock = new BlockPos(-1, -1, -1);
    private ItemStack currentItemHittingBlock = ItemStack.field_190927_a;
    public float curBlockDamageMP;
    public float stepSoundTickCounter;
    public int blockHitDelay;
    private boolean isHittingBlock;
    private GameType currentGameType = GameType.SURVIVAL;
    private int currentPlayerItem;

    public PlayerControllerMP(Minecraft mcIn, NetHandlerPlayClient netHandler) {
        this.mc = mcIn;
        this.connection = netHandler;
    }

    public static void clickBlockCreative(Minecraft mcIn, PlayerControllerMP playerController, BlockPos pos, EnumFacing facing) {
        if (!mcIn.world.extinguishFire(Minecraft.getMinecraft().player, pos, facing)) {
            playerController.onPlayerDestroyBlock(pos);
        }
    }

    public void setPlayerCapabilities(EntityPlayer player) {
        this.currentGameType.configurePlayerCapabilities(player.capabilities);
    }

    public boolean isSpectator() {
        return this.currentGameType == GameType.SPECTATOR;
    }

    public void setGameType(GameType type) {
        this.currentGameType = type;
        this.currentGameType.configurePlayerCapabilities(Minecraft.getMinecraft().player.capabilities);
    }

    public void flipPlayer(EntityPlayer playerIn) {
        playerIn.rotationYaw = -180.0f;
    }

    public boolean shouldDrawHUD() {
        return this.currentGameType.isSurvivalOrAdventure();
    }

    public boolean onPlayerDestroyBlock(BlockPos pos) {
        WorldClient world;
        IBlockState iblockstate;
        Block block;
        if (this.currentGameType.isAdventure()) {
            if (this.currentGameType == GameType.SPECTATOR) {
                return false;
            }
            if (!Minecraft.getMinecraft().player.isAllowEdit()) {
                ItemStack itemstack = Minecraft.getMinecraft().player.getHeldItemMainhand();
                if (itemstack.func_190926_b()) {
                    return false;
                }
                if (!itemstack.canDestroy(this.mc.world.getBlockState(pos).getBlock())) {
                    return false;
                }
            }
        }
        if (this.currentGameType.isCreative()) {
            if (!Minecraft.getMinecraft().player.getHeldItemMainhand().func_190926_b()) {
                if (Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() instanceof ItemSword) {
                    return false;
                }
            }
        }
        if ((block = (iblockstate = (world = this.mc.world).getBlockState(pos)).getBlock()) instanceof BlockCommandBlock || block instanceof BlockStructure) {
            if (!Minecraft.getMinecraft().player.canUseCommandBlock()) {
                return false;
            }
        }
        if (iblockstate.getMaterial() == Material.AIR) {
            return false;
        }
        world.playEvent(2001, pos, Block.getStateId(iblockstate));
        block.onBlockHarvested(world, pos, iblockstate, Minecraft.getMinecraft().player);
        boolean flag = ((World) world).setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
        if (flag) {
            block.onBlockDestroyedByPlayer(world, pos, iblockstate);
        }
        this.currentBlock = new BlockPos(this.currentBlock.getX(), -1, this.currentBlock.getZ());
        if (!this.currentGameType.isCreative()) {
            ItemStack itemstack1 = Minecraft.getMinecraft().player.getHeldItemMainhand();
            if (!itemstack1.func_190926_b()) {
                itemstack1.onBlockDestroyed(world, iblockstate, pos, Minecraft.getMinecraft().player);
                if (itemstack1.func_190926_b()) {
                    Minecraft.getMinecraft().player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.field_190927_a);
                }
            }
        }
        return flag;
    }

    /*
     * Enabled aggressive block sorting
     */
    public boolean clickBlock(BlockPos loc, EnumFacing face) {
        boolean flag;
        if (this.currentGameType.isAdventure()) {
            if (this.currentGameType == GameType.SPECTATOR) {
                return false;
            }
            if (!Minecraft.getMinecraft().player.isAllowEdit()) {
                ItemStack itemstack = Minecraft.getMinecraft().player.getHeldItemMainhand();
                if (itemstack.func_190926_b()) {
                    return false;
                }
                if (!itemstack.canDestroy(this.mc.world.getBlockState(loc).getBlock())) {
                    return false;
                }
            }
        }
        if (!this.mc.world.getWorldBorder().contains(loc)) {
            return false;
        }
        if (this.currentGameType.isCreative()) {
            this.mc.func_193032_ao().func_193294_a(this.mc.world, loc, this.mc.world.getBlockState(loc), 1.0f);
            this.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, loc, face));
            PlayerControllerMP.clickBlockCreative(this.mc, this, loc, face);
            this.blockHitDelay = 5;
            return true;
        }
        if (this.isHittingBlock) {
            if (this.isHittingPosition(loc)) return true;
        }
        if (this.isHittingBlock) {
            this.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.currentBlock, face));
        }
        IBlockState iblockstate = this.mc.world.getBlockState(loc);
        this.mc.func_193032_ao().func_193294_a(this.mc.world, loc, iblockstate, 0.0f);
        this.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, loc, face));
        boolean bl = flag = iblockstate.getMaterial() != Material.AIR;
        if (flag && this.curBlockDamageMP == 0.0f) {
            iblockstate.getBlock().onBlockClicked(this.mc.world, loc, Minecraft.getMinecraft().player);
        }
        if (flag) {
            if (iblockstate.getPlayerRelativeBlockHardness(Minecraft.getMinecraft().player, Minecraft.getMinecraft().player.world, loc) >= 1.0f) {
                this.onPlayerDestroyBlock(loc);
                return true;
            }
        }
        this.isHittingBlock = true;
        this.currentBlock = loc;
        this.currentItemHittingBlock = Minecraft.getMinecraft().player.getHeldItemMainhand();
        this.curBlockDamageMP = 0.0f;
        this.stepSoundTickCounter = 0.0f;
        this.mc.world.sendBlockBreakProgress(Minecraft.getMinecraft().player.getEntityId(), this.currentBlock, (int) (this.curBlockDamageMP * 10.0f) - 1);
        return true;
    }

    public void resetBlockRemoving() {
        if (this.isHittingBlock) {
            this.mc.func_193032_ao().func_193294_a(this.mc.world, this.currentBlock, this.mc.world.getBlockState(this.currentBlock), -1.0f);
            this.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.currentBlock, EnumFacing.DOWN));
            this.isHittingBlock = false;
            this.curBlockDamageMP = 0.0f;
            this.mc.world.sendBlockBreakProgress(Minecraft.getMinecraft().player.getEntityId(), this.currentBlock, -1);
            Minecraft.getMinecraft().player.resetCooldown();
        }
    }

    public boolean onPlayerDamageBlock(BlockPos posBlock, EnumFacing directionFacing) {
        this.syncCurrentPlayItem();

        EventBlockInteract eventBlockInteract = new EventBlockInteract(posBlock, directionFacing);
        eventBlockInteract.call();
        if (eventBlockInteract.isCancelled()) {
            return false;
        }

        if (this.blockHitDelay > 0) {
            --this.blockHitDelay;
            return true;
        }
        if (this.currentGameType.isCreative() && this.mc.world.getWorldBorder().contains(posBlock)) {
            this.blockHitDelay = 5;
            this.mc.func_193032_ao().func_193294_a(this.mc.world, posBlock, this.mc.world.getBlockState(posBlock), 1.0f);
            this.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, posBlock, directionFacing));
            PlayerControllerMP.clickBlockCreative(this.mc, this, posBlock, directionFacing);
            return true;
        }
        if (this.isHittingPosition(posBlock)) {
            IBlockState iblockstate = this.mc.world.getBlockState(posBlock);
            Block block = iblockstate.getBlock();
            if (iblockstate.getMaterial() == Material.AIR) {
                this.isHittingBlock = false;
                return false;
            }
            this.curBlockDamageMP += iblockstate.getPlayerRelativeBlockHardness(Minecraft.getMinecraft().player, Minecraft.getMinecraft().player.world, posBlock);
            if (this.stepSoundTickCounter % 4.0f == 0.0f) {
                SoundType soundtype = block.getSoundType();
                this.mc.getSoundHandler().playSound(new PositionedSoundRecord(soundtype.getHitSound(), SoundCategory.NEUTRAL, (soundtype.getVolume() + 1.0f) / 8.0f, soundtype.getPitch() * 0.5f, posBlock));
            }
            this.stepSoundTickCounter += 1.0f;
            this.mc.func_193032_ao().func_193294_a(this.mc.world, posBlock, iblockstate, MathHelper.clamp(this.curBlockDamageMP, 0.0f, 1.0f));
            if (this.curBlockDamageMP >= 1.0f) {
                this.isHittingBlock = false;
                this.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, posBlock, directionFacing));
                this.onPlayerDestroyBlock(posBlock);
                this.curBlockDamageMP = 0.0f;
                this.stepSoundTickCounter = 0.0f;
                this.blockHitDelay = 5;
            }
            this.mc.world.sendBlockBreakProgress(Minecraft.getMinecraft().player.getEntityId(), this.currentBlock, (int) (this.curBlockDamageMP * 10.0f) - 1);
            return true;
        }
        return this.clickBlock(posBlock, directionFacing);
    }

    public float getBlockReachDistance() {
        return this.currentGameType.isCreative() ? 5.0f : 4.5f;
    }

    public void updateController() {
        this.syncCurrentPlayItem();
        if (this.connection.getNetworkManager().isChannelOpen()) {
            this.connection.getNetworkManager().processReceivedPackets();
        } else {
            this.connection.getNetworkManager().checkDisconnected();
        }
    }

    private boolean isHittingPosition(BlockPos pos) {
        boolean flag;
        ItemStack itemstack = Minecraft.getMinecraft().player.getHeldItemMainhand();
        boolean bl = flag = this.currentItemHittingBlock.func_190926_b() && itemstack.func_190926_b();
        if (!this.currentItemHittingBlock.func_190926_b() && !itemstack.func_190926_b()) {
            flag = itemstack.getItem() == this.currentItemHittingBlock.getItem() && ItemStack.areItemStackTagsEqual(itemstack, this.currentItemHittingBlock) && (itemstack.isItemStackDamageable() || itemstack.getMetadata() == this.currentItemHittingBlock.getMetadata());
        }
        return pos.equals(this.currentBlock) && flag;
    }

    private void syncCurrentPlayItem() {
        int i = Minecraft.getMinecraft().player.inventory.currentItem;
        if (i != this.currentPlayerItem) {
            this.currentPlayerItem = i;
            this.connection.sendPacket(new CPacketHeldItemChange(this.currentPlayerItem));
        }
    }

    public EnumActionResult processRightClickBlock(EntityPlayerSP player, WorldClient worldIn, BlockPos stack, EnumFacing pos, Vec3d facing, EnumHand vec) {
        this.syncCurrentPlayItem();
        ItemStack itemstack = player.getHeldItem(vec);
        float f = (float) (facing.xCoord - (double) stack.getX());
        float f1 = (float) (facing.yCoord - (double) stack.getY());
        float f2 = (float) (facing.zCoord - (double) stack.getZ());
        boolean flag = false;
        if (!this.mc.world.getWorldBorder().contains(stack)) {
            return EnumActionResult.FAIL;
        }
        if (this.currentGameType != GameType.SPECTATOR) {
            ItemBlock itemblock;
            IBlockState iblockstate = worldIn.getBlockState(stack);
            if ((!player.isSneaking() || player.getHeldItemMainhand().func_190926_b() && player.getHeldItemOffhand().func_190926_b()) && iblockstate.getBlock().onBlockActivated(worldIn, stack, iblockstate, player, vec, pos, f, f1, f2)) {
                flag = true;
            }
            if (!flag && itemstack.getItem() instanceof ItemBlock && !(itemblock = (ItemBlock) itemstack.getItem()).canPlaceBlockOnSide(worldIn, stack, pos, player, itemstack)) {
                return EnumActionResult.FAIL;
            }
        }
        this.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(stack, pos, vec, f, f1, f2));
        if (!flag && this.currentGameType != GameType.SPECTATOR) {
            Block block;
            if (itemstack.func_190926_b()) {
                return EnumActionResult.PASS;
            }
            if (player.getCooldownTracker().hasCooldown(itemstack.getItem())) {
                return EnumActionResult.PASS;
            }
            if (itemstack.getItem() instanceof ItemBlock && !player.canUseCommandBlock() && ((block = ((ItemBlock) itemstack.getItem()).getBlock()) instanceof BlockCommandBlock || block instanceof BlockStructure)) {
                return EnumActionResult.FAIL;
            }
            if (this.currentGameType.isCreative()) {
                int i = itemstack.getMetadata();
                int j = itemstack.func_190916_E();
                EnumActionResult enumactionresult = itemstack.onItemUse(player, worldIn, stack, vec, pos, f, f1, f2);
                itemstack.setItemDamage(i);
                itemstack.func_190920_e(j);
                return enumactionresult;
            }
            return itemstack.onItemUse(player, worldIn, stack, vec, pos, f, f1, f2);
        }
        return EnumActionResult.SUCCESS;
    }

    public EnumActionResult processRightClick(EntityPlayer player, World worldIn, EnumHand stack) {
        if (this.currentGameType == GameType.SPECTATOR) {
            return EnumActionResult.PASS;
        }
        this.syncCurrentPlayItem();
        this.connection.sendPacket(new CPacketPlayerTryUseItem(stack));
        ItemStack itemstack = player.getHeldItem(stack);
        if (player.getCooldownTracker().hasCooldown(itemstack.getItem())) {
            return EnumActionResult.PASS;
        }
        int i = itemstack.func_190916_E();
        ActionResult<ItemStack> actionresult = itemstack.useItemRightClick(worldIn, player, stack);
        ItemStack itemstack1 = actionresult.getResult();
        if (itemstack1 != itemstack || itemstack1.func_190916_E() != i) {
            player.setHeldItem(stack, itemstack1);
        }
        return actionresult.getType();
    }

    public EntityPlayerSP func_192830_a(World p_192830_1_, StatisticsManager p_192830_2_, RecipeBook p_192830_3_) {
        return new EntityPlayerSP(this.mc, p_192830_1_, this.connection, p_192830_2_, p_192830_3_);
    }

    public void attackEntity(EntityPlayer playerIn, Entity targetEntity) {

        EventAttackSilent attackEvent = new EventAttackSilent(targetEntity);
        attackEvent.call();
        if (attackEvent.isCancelled()) {
            return;
        }


        this.syncCurrentPlayItem();
        this.connection.sendPacket(new CPacketUseEntity(targetEntity));

        if (this.currentGameType != GameType.SPECTATOR) {
            playerIn.attackTargetEntityWithCurrentItem(targetEntity);
            playerIn.resetCooldown();
        }
    }

    public EnumActionResult interactWithEntity(EntityPlayer player, Entity target, EnumHand heldItem) {
        this.syncCurrentPlayItem();
        this.connection.sendPacket(new CPacketUseEntity(target, heldItem));
        return this.currentGameType == GameType.SPECTATOR ? EnumActionResult.PASS : player.func_190775_a(target, heldItem);
    }

    public EnumActionResult interactWithEntity(EntityPlayer player, Entity target, RayTraceResult raytrace, EnumHand heldItem) {
        this.syncCurrentPlayItem();
        Vec3d vec3d = new Vec3d(raytrace.hitVec.xCoord - target.posX, raytrace.hitVec.yCoord - target.posY, raytrace.hitVec.zCoord - target.posZ);
        this.connection.sendPacket(new CPacketUseEntity(target, heldItem, vec3d));
        return this.currentGameType == GameType.SPECTATOR ? EnumActionResult.PASS : target.applyPlayerInteraction(player, vec3d, heldItem);
    }

    public ItemStack windowClick(int windowId, int slotId, int mouseButton, ClickType type, EntityPlayer player) {
        short short1 = player.openContainer.getNextTransactionID(player.inventory);
        ItemStack itemstack = player.openContainer.slotClick(slotId, mouseButton, type, player);
        this.connection.sendPacket(new CPacketClickWindow(windowId, slotId, mouseButton, type, itemstack, short1));
        return itemstack;
    }

    public void func_194338_a(int p_194338_1_, IRecipe p_194338_2_, boolean p_194338_3_, EntityPlayer p_194338_4_) {
        this.connection.sendPacket(new CPacketPlaceRecipe(p_194338_1_, p_194338_2_, p_194338_3_));
    }

    public void sendEnchantPacket(int windowID, int button) {
        this.connection.sendPacket(new CPacketEnchantItem(windowID, button));
    }

    public void sendSlotPacket(ItemStack itemStackIn, int slotId) {
        if (this.currentGameType.isCreative()) {
            this.connection.sendPacket(new CPacketCreativeInventoryAction(slotId, itemStackIn));
        }
    }

    public void sendPacketDropItem(ItemStack itemStackIn) {
        if (this.currentGameType.isCreative() && !itemStackIn.func_190926_b()) {
            this.connection.sendPacket(new CPacketCreativeInventoryAction(-1, itemStackIn));
        }
    }

    public void onStoppedUsingItem(EntityPlayer playerIn) {
        this.syncCurrentPlayItem();
        this.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
        playerIn.stopActiveHand();
    }

    public boolean gameIsSurvivalOrAdventure() {
        return this.currentGameType.isSurvivalOrAdventure();
    }

    public boolean isNotCreative() {
        return !this.currentGameType.isCreative();
    }

    public boolean isInCreativeMode() {
        return this.currentGameType.isCreative();
    }

    public boolean extendedReach() {
        return this.currentGameType.isCreative();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean isRidingHorse() {
        if (!Minecraft.getMinecraft().player.isRiding()) return false;
        if (!(Minecraft.getMinecraft().player.getRidingEntity() instanceof AbstractHorse)) return false;
        return true;
    }

    public boolean isSpectatorMode() {
        return this.currentGameType == GameType.SPECTATOR;
    }

    public GameType getCurrentGameType() {
        return this.currentGameType;
    }

    public boolean getIsHittingBlock() {
        return this.isHittingBlock;
    }

    public void pickItem(int index) {
        this.connection.sendPacket(new CPacketCustomPayload("MC|PickItem", new PacketBuffer(Unpooled.buffer()).writeVarIntToBuffer(index)));
    }
}

