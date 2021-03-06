package me.desht.modularrouters.item.module;

import me.desht.modularrouters.block.tile.TileEntityItemRouter;
import me.desht.modularrouters.client.util.TintColor;
import me.desht.modularrouters.config.MRConfig;
import me.desht.modularrouters.core.ModItems;
import me.desht.modularrouters.logic.compiled.CompiledModule;
import me.desht.modularrouters.logic.compiled.CompiledSenderModule1;
import net.minecraft.item.ItemStack;

public class SenderModule1 extends ItemModule implements IRangedModule {
    public SenderModule1() {
        super(ModItems.defaultProps());
    }

    @Override
    public CompiledModule compile(TileEntityItemRouter router, ItemStack stack) {
        return new CompiledSenderModule1(router, stack);
    }

    @Override
    public int getBaseRange() {
        return MRConfig.Common.Module.sender1BaseRange;
    }

    @Override
    public int getHardMaxRange() {
        return MRConfig.Common.Module.sender1MaxRange;
    }

    @Override
    public TintColor getItemTint() {
        return new TintColor(221, 255, 163);
    }
}
