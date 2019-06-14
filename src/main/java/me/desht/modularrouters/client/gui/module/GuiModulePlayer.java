package me.desht.modularrouters.client.gui.module;

import com.google.common.collect.Lists;
import me.desht.modularrouters.client.render.RenderHelper;
import me.desht.modularrouters.client.gui.widgets.button.ItemStackCyclerButton;
import me.desht.modularrouters.client.gui.widgets.button.TexturedCyclerButton;
import me.desht.modularrouters.container.ContainerModule;
import me.desht.modularrouters.core.ModBlocks;
import me.desht.modularrouters.logic.compiled.CompiledPlayerModule;
import me.desht.modularrouters.logic.compiled.CompiledPlayerModule.Operation;
import me.desht.modularrouters.logic.compiled.CompiledPlayerModule.Section;
import net.minecraft.block.Blocks;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;

import java.util.Collections;
import java.util.List;

public class GuiModulePlayer extends GuiModule {
    private static final ItemStack mainInvStack = new ItemStack(Blocks.CHEST);
    private static final ItemStack armourStack = new ItemStack(Items.DIAMOND_CHESTPLATE);
    private static final ItemStack shieldStack = new ItemStack(Items.SHIELD);
    private static final ItemStack enderStack = new ItemStack(Blocks.ENDER_CHEST);
    private static final ItemStack routerStack = new ItemStack(ModBlocks.ITEM_ROUTER);

    private static final ItemStack[] stacks = new ItemStack[] { mainInvStack, armourStack, shieldStack, enderStack };

    private SectionButton secButton;
    private OperationButton opButton;

    public GuiModulePlayer(ContainerModule container, PlayerInventory inv, ITextComponent displayName) {
        super(container, inv, displayName);
    }

    @Override
    public void init() {
        super.init();

        CompiledPlayerModule cpm = new CompiledPlayerModule(null, moduleItemStack);

        addButton(secButton = new SectionButton(guiLeft + 169, guiTop + 32, 16, 16, true, stacks, cpm.getSection()));
        addButton(opButton = new OperationButton(guiLeft + 148, guiTop + 32, cpm.getOperation()));

        getMouseOverHelp().addHelpRegion(guiLeft + 127, guiTop + 29, guiLeft + 187, guiTop + 50, "guiText.popup.player.control");
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

        this.blit(guiLeft + 167, guiTop + 31, BUTTON_XY.x, BUTTON_XY.y, 18, 18);  // section "button" background

        RenderHelper.renderItemStack(minecraft, routerStack, guiLeft + 128, guiTop + 32, "");
    }

    @Override
    protected CompoundNBT buildMessageData() {
        CompoundNBT compound = super.buildMessageData();
        compound.putInt(CompiledPlayerModule.NBT_OPERATION, opButton.getState().ordinal());
        compound.putInt(CompiledPlayerModule.NBT_SECTION, secButton.getState().ordinal());
        return compound;
    }

    private class SectionButton extends ItemStackCyclerButton<Section> {
        private final List<List<String>> tips = Lists.newArrayList();

        SectionButton(int x, int y, int width, int height, boolean flat, ItemStack[] stacks, Section initialVal) {
            super(x, y, width, height, flat, stacks, initialVal, GuiModulePlayer.this);
            for (Section sect : Section.values()) {
                tips.add(Collections.singletonList(I18n.format("guiText.label.playerSect." + sect)));
            }
        }

        @Override
        public List<String> getTooltip() {
            return tips.get(getState().ordinal());
        }
    }

    private class OperationButton extends TexturedCyclerButton<Operation> {
        private final List<List<String>> tooltips = Lists.newArrayList();

        OperationButton(int x, int y, Operation initialVal) {
            super(x, y, 16, 16, initialVal, GuiModulePlayer.this);

            for (Operation op : Operation.values()) {
                tooltips.add(Collections.singletonList(I18n.format("guiText.label.playerOp." + op)));
            }
        }

        @Override
        protected int getTextureX() {
            return 160 + getState().ordinal() * 16;
        }

        @Override
        protected int getTextureY() {
            return 16;
        }

        @Override
        public java.util.List<String> getTooltip() {
            return tooltips.get(getState().ordinal());
        }
    }
}
