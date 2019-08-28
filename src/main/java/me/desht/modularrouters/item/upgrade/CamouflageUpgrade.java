package me.desht.modularrouters.item.upgrade;

import me.desht.modularrouters.block.tile.TileEntityItemRouter;
import me.desht.modularrouters.core.ModBlocks;
import me.desht.modularrouters.core.ModSounds;
import me.desht.modularrouters.util.MiscUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

public class CamouflageUpgrade extends ItemUpgrade {
    public static final String NBT_STATE_NAME = "BlockStateName";

    public CamouflageUpgrade(Properties props) {
        super(props);
    }

    @Override
    public void addExtraInformation(ItemStack itemstack, List<ITextComponent> list) {
        if (itemstack.hasTag() && itemstack.getTag().contains(NBT_STATE_NAME)) {
            list.add(MiscUtil.xlate("itemText.camouflage.held")
                    .appendText(TextFormatting.AQUA.toString())
                    .appendSibling(getCamoStateDisplayName(itemstack)));
        }
    }

    @Override
    public void onCompiled(ItemStack stack, TileEntityItemRouter router) {
        super.onCompiled(stack, router);
        router.setCamouflage(getCamoState(stack));
    }

    private static void setCamoState(ItemStack stack, BlockState camoState) {
        stack.getOrCreateTag().put(NBT_STATE_NAME, NBTUtil.writeBlockState(camoState));
    }

    public static BlockState readFromNBT(CompoundNBT compound) {
        return NBTUtil.readBlockState(compound);
    }

    private static BlockState getCamoState(ItemStack stack) {
        return stack.hasTag() ? readFromNBT(stack.getTag().getCompound(NBT_STATE_NAME)) : null;
    }

    private static ITextComponent getCamoStateDisplayName(ItemStack stack) {
        BlockState state = getCamoState(stack);
        if (state != null) {
            Block b = state.getBlock();
            Item item = b.asItem();
            return new ItemStack(item).getDisplayName();
        }
        return new StringTextComponent("<?>");
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext ctx) {
        PlayerEntity player = ctx.getPlayer();
        assert player != null;
        ItemStack stack = ctx.getItem();

        BlockState state = ctx.getWorld().getBlockState(ctx.getPos());
        if (isBlockOKForCamo(state)) {
            setCamoState(stack, state);
            if (!ctx.getWorld().isRemote) {
                player.sendStatusMessage(MiscUtil.xlate("itemText.camouflage.held")
                        .appendText(TextFormatting.AQUA.toString())
                        .appendSibling(getCamoStateDisplayName(stack))
                        .applyTextStyle(TextFormatting.YELLOW), false);
            } else {
                player.playSound(ModSounds.SUCCESS, 1.0f, 1.5f);
            }
            return ActionResultType.SUCCESS;
        } else if (ctx.getWorld().isRemote) {
            player.playSound(ModSounds.ERROR, 1.0f, 1.0f);
            return ActionResultType.FAIL;
        }
        return ActionResultType.PASS;
    }

    private static boolean isBlockOKForCamo(BlockState state) {
        // trying to camo a router as itself = recursion hell
        return state.getRenderType() == BlockRenderType.MODEL && state.getBlock() != ModBlocks.ITEM_ROUTER;
    }
}
