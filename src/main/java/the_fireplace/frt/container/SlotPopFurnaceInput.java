package the_fireplace.frt.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import the_fireplace.frt.recipes.PopFurnaceRecipes;
import the_fireplace.frt.tools.MiscTools;

import java.util.Iterator;
import java.util.Map.Entry;

/**
 * @author The_Fireplace
 */
public class SlotPopFurnaceInput extends Slot {

    public SlotPopFurnaceInput(IInventory inventoryIn, int index,
                               int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        Iterator iterator = PopFurnaceRecipes.instance().getPoppingList().entrySet().iterator();
        Entry entry;
        do {
            if (!iterator.hasNext()) {
                return false;
            }
            entry = (Entry) iterator.next();
        }
        while (!MiscTools.areItemStacksEqual(new ItemStack(stack.getItem(), stack.getMetadata()), (ItemStack) entry.getKey()));
        return true;
    }
}
