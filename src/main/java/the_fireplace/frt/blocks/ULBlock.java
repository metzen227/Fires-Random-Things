package the_fireplace.frt.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import the_fireplace.frt.FRT;

public class ULBlock extends Block {

	public ULBlock(Material materialIn) {
		super(materialIn);
		setCreativeTab(FRT.TabFRT);
	}
}