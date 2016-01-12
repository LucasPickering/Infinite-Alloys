package infinitealloys.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import infinitealloys.block.BlockMachine;
import infinitealloys.util.Funcs;
import io.netty.buffer.ByteBuf;

public final class MessageOpenGui implements IMessage, IMessageHandler<MessageOpenGui, IMessage> {

  private BlockPos machinePos;

  public MessageOpenGui() {
  }

  public MessageOpenGui(BlockPos machinePos) {
    this.machinePos = machinePos;
  }

  @Override
  public void fromBytes(ByteBuf bytes) {
    machinePos = Funcs.readBlockPosFromByteBuf(bytes);
  }

  @Override
  public void toBytes(ByteBuf bytes) {
    Funcs.writeBlockPosToByteBuf(bytes, machinePos);
  }

  @Override
  public IMessage onMessage(MessageOpenGui message, MessageContext context) {
    machinePos = message.machinePos;

    EntityPlayer player = context.getServerHandler().playerEntity;
    ((BlockMachine) player.worldObj.getBlockState(machinePos).getBlock()).openGui(player.worldObj,
                                                                                  player, machinePos);

    return null;
  }
}
