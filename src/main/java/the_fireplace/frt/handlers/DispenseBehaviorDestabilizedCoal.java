package the_fireplace.frt.handlers;

import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import the_fireplace.frt.entity.coal.EntityDestabilizedCoal;

/**
 * @author The_Fireplace
 */
public class DispenseBehaviorDestabilizedCoal extends BehaviorProjectileDispense {
	@Override
	protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stack){
		return new EntityDestabilizedCoal(worldIn, position.getX(), position.getY(), position.getZ());
	}
}