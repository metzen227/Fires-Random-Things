package the_fireplace.frt.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import the_fireplace.frt.entity.tile.TileEntityPopFurnace;

/**
 * @author The_Fireplace
 */
public class ContainerPopFurnace extends Container {
    private TileEntityPopFurnace te;

    public ContainerPopFurnace(InventoryPlayer invPlayer, TileEntityPopFurnace entity) {
        this.te = entity;

        for (int x = 0; x < 9; x++) {
            this.addSlotToContainer(new Slot(invPlayer, x, 8 + x * 18, 142 - 14 - 4 - 6 - 5));//player inventory IDs 0 to 8
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlotToContainer(new Slot(invPlayer, 9 + x + y * 9, 8 + x * 18, 84 + y * 18 - 14 - 4 - 6 - 5));//player inventory IDs 9 to 35
            }
        }

        for (int x = 0; x < 5; x++) {
            this.addSlotToContainer(new SlotPopFurnaceInput(entity, x, 80 + x * 18, 26 - 4 - 6 - 5));//tile entity IDs 0 to 4
        }

        for (int x = 0; x < 5; x++) {
            this.addSlotToContainer(new SlotPopFurnaceOutput(entity, 5 + x, 80 + x * 18, 26 + 18 + 4 - 4 - 6 - 5));//tile entity IDs 5 to 9
        }
        this.addSlotToContainer(new SlotGunpowder(entity, 10, 8, 26 - 4 - 6 - 5));//tile entity ID 10, gunpowder slot
        this.addSlotToContainer(new SlotFirestarter(entity, 11, 8, 48 - 4 - 6 - 5));//tile entity ID 11, firestarter slot
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return te.isUseableByPlayer(playerIn);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int i) {
        Slot slot = getSlot(i);
        if (slot != null && slot.getHasStack()) {
            ItemStack is = slot.getStack();
            ItemStack result = is.copy();

            if (i >= 36) {
                if (!mergeItemStack(is, 0, 36, false)) {
                    return null;
                }
            } else if (!mergeItemStack(is, 36, 36 + te.getSizeInventory(), false)) {
                return null;
            }
            if (is.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }
            slot.onPickupFromSlot(player, is);
            return result;
        }
        return null;
    }
}
