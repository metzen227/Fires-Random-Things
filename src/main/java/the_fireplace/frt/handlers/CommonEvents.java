package the_fireplace.frt.handlers;

import com.google.common.collect.Maps;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import the_fireplace.frt.FRT;
import the_fireplace.frt.blocks.internal.BlockStrawBed;
import the_fireplace.frt.network.PacketDispatcher;
import the_fireplace.frt.network.UpdatePotionMessage;
import the_fireplace.frt.potion.HallucinationPotion;

import java.util.HashMap;

/**
 * @author The_Fireplace
 */
@SuppressWarnings("unused")
@Mod.EventBusSubscriber
public final class CommonEvents {

	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
		if (eventArgs.getModID().equals(FRT.MODID))
			FRT.instance.syncConfig();
	}

	private static HashMap<EntityPlayer, BlockPos> bedLocations = Maps.newHashMap();

	@SubscribeEvent
	public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
		if (event.getEntityLiving() instanceof EntityPlayer) {
			boolean remote = event.getEntityLiving().getEntityWorld().isRemote;
			boolean potionActive = event.getEntityLiving().isPotionActive(FRT.hallucination);
			if (remote && !potionActive) {
				if (FRT.instance.clientCooldownTicks == 0) {
					FRT.proxy.tryRemoveShader();
					FRT.instance.clientCooldownTicks = -1;
				} else if (FRT.instance.clientCooldownTicks > 0)
					FRT.instance.clientCooldownTicks--;
			} else if (!remote && potionActive)
				FRT.hallucination.performEffect(event.getEntityLiving(), 0);
		}
	}

	@SubscribeEvent
	public static void tickEvent(TickEvent.WorldTickEvent event) {
		if (!event.world.isRemote && event.world.getTotalWorldTime() % 20 == 0 && !bedLocations.isEmpty())
			for (EntityPlayer player : bedLocations.keySet()) {
				if (!player.isPlayerSleeping()) {
					ReflectionHelper.setPrivateValue(EntityPlayer.class, player, bedLocations.get(player), "spawnChunk", "field_71077_c");
					bedLocations.remove(player);
				}
			}
	}

	@SubscribeEvent
	public static void itemUseFinish(LivingEntityUseItemEvent.Finish event) {
		if (!event.getEntityLiving().world.isRemote && event.getEntityLiving() instanceof EntityPlayer)
			for (PotionEffect effect : PotionUtils.getEffectsFromStack(event.getItem()))
				if (effect.getPotion() instanceof HallucinationPotion)
					PacketDispatcher.sendTo(new UpdatePotionMessage(effect.getDuration()), (EntityPlayerMP) event.getEntityLiving());
	}

	@SubscribeEvent
	public static void onPlayerWake(PlayerWakeUpEvent event) {
		if (event.getEntityPlayer().getEntityWorld().getBlockState(event.getEntityPlayer().bedLocation).getBlock() instanceof BlockStrawBed) {
			BlockPos pos = event.getEntityPlayer().getBedLocation();
			bedLocations.putIfAbsent(event.getEntityPlayer(), pos);
		}
	}

	@SubscribeEvent
	public static void furnaceBurn(FurnaceFuelBurnTimeEvent event) {
		if (FRTFuelHandler.getBurnTime(event.getItemStack()) != 0)
			event.setBurnTime(FRTFuelHandler.getBurnTime(event.getItemStack()));
	}
}
