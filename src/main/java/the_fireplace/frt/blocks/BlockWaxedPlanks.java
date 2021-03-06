package the_fireplace.frt.blocks;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import the_fireplace.frt.FRT;

import javax.annotation.Nonnull;

/**
 * @author The_Fireplace
 */
@MethodsReturnNonnullByDefault
public class BlockWaxedPlanks extends FRTBlock {
	public static final PropertyEnum<BlockPlanks.EnumType> VARIANT = PropertyEnum.create("variant", BlockPlanks.EnumType.class);

	public BlockWaxedPlanks() {
		super(Material.WOOD);
		setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockPlanks.EnumType.OAK));
		setHardness(2.0F);
		setResistance(5.0F);
		setSoundType(SoundType.WOOD);
		setUnlocalizedName("waxed_planks");
		setCreativeTab(FRT.TabFRT);
		this.slipperiness = 0.75F;
	}

	@Override
	public int damageDropped(IBlockState state) {
		return (state.getValue(VARIANT)).getMetadata();
	}

	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
		for (BlockPlanks.EnumType type : BlockPlanks.EnumType.values()) {
			list.add(new ItemStack(this, 1, type.getMetadata()));
		}
	}

	@Override
	@Nonnull
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(VARIANT, BlockPlanks.EnumType.byMetadata(meta));
	}

	@Override
	@Nonnull
	public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		return (state.getValue(VARIANT)).getMapColor();
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return (state.getValue(VARIANT)).getMetadata();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, VARIANT);
	}
}
