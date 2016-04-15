package the_fireplace.frt.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import the_fireplace.frt.FRT;

import java.util.Random;

/**
 * @author The_Fireplace
 */
public class ChangeShaderMessage implements IMessage {
	public ChangeShaderMessage(){}

	@Override
	public void fromBytes(ByteBuf buf) {

	}

	@Override
	public void toBytes(ByteBuf buf) {

	}

	public static class Handler extends AbstractClientMessageHandler<ChangeShaderMessage>{
		@Override
		public IMessage handleClientMessage(EntityPlayer player, ChangeShaderMessage message, MessageContext ctx) {
			IThreadListener listener = Minecraft.getMinecraft();
			listener.addScheduledTask(() -> {
				Random rand = new Random();
				for(int i=rand.nextInt(25);i>=0;i--) {
					FRT.proxy.activateNextShader();
				}
			});
			return null;
		}
	}
}