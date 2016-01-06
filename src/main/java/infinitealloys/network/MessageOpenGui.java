package infinitealloys.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import infinitealloys.block.BlockMachine;
import infinitealloys.tile.TileEntityMachine;
import infinitealloys.util.Funcs;
import io.netty.buffer.ByteBuf;

public final class MessageOpenGui implements IMessage, IMessageHandler<MessageOpenGui, IMessage> {

  private BlockPos machine;

  public MessageOpenGui() {
  }

  public MessageOpenGui(BlockPos machine) {
    this.machine = machine;
  }

  @Override
  public void fromBytes(ByteBuf bytes) {
    machine = Funcs.readBlockPosFromByteBuf(bytes);
  }

  @Override
  public void toBytes(ByteBuf bytes) {
    Funcs.writeBlockPosToByteBuf(bytes, machine);
  }

  @Override
  public IMessage onMessage(MessageOpenGui message, MessageContext context) {
    machine = message.machine;

    EntityPlayer player = context.getServerHandler().playerEntity;
    ((BlockMachine) player.worldObj.getBlockState(machine).getBlock())
        .openGui(player.worldObj, player, (TileEntityMachine) player.worldObj.getTileEntity(machine));

    return null;
  }
}
