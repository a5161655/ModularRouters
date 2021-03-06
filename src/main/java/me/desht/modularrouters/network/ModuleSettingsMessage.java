package me.desht.modularrouters.network;

import io.netty.buffer.ByteBuf;
import me.desht.modularrouters.ModularRouters;
import me.desht.modularrouters.block.tile.TileEntityItemRouter;
import me.desht.modularrouters.item.module.ItemModule;
import me.desht.modularrouters.util.MFLocator;
import me.desht.modularrouters.util.ModuleHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Received on: SERVER
 *
 * Sent by client when a player updates a module's settings via its GUI.
 */
public class ModuleSettingsMessage {
    private MFLocator locator;
    private CompoundNBT payload;

    @SuppressWarnings("unused")
    public ModuleSettingsMessage() {
    }

    ModuleSettingsMessage(ByteBuf byteBuf) {
        PacketBuffer pb = new PacketBuffer(byteBuf);
        locator = MFLocator.fromBuffer(pb);
        payload = pb.readCompoundTag();
    }

    public ModuleSettingsMessage(MFLocator locator, CompoundNBT payload) {
        this.locator = locator;
        this.payload = payload;
    }

    public void toBytes(ByteBuf byteBuf) {
        PacketBuffer pb = new PacketBuffer(byteBuf);
        locator.writeBuf(pb);
        pb.writeCompoundTag(payload);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            PlayerEntity player = ctx.get().getSender();
            ItemStack moduleStack = locator.getModuleStack(player);

            if (moduleStack.getItem() instanceof ItemModule) {
                CompoundNBT compound = ModuleHelper.validateNBT(moduleStack);
                for (String key : payload.keySet()) {
                    compound.put(key, payload.get(key));
                }
                if (locator.routerPos != null) {
                    TileEntityItemRouter.getRouterAt(player.getEntityWorld(), locator.routerPos)
                            .ifPresent(router -> router.recompileNeeded(TileEntityItemRouter.COMPILE_MODULES));
                }
            } else {
                ModularRouters.LOGGER.warn("ignoring ModuleSettingsMessage for " + player.getDisplayName().getString() + " - expected module not found @ " + locator.toString());
            }
        });

        ctx.get().setPacketHandled(true);
    }

}
