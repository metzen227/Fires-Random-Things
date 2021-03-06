package the_fireplace.frt.tileentity;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import the_fireplace.frt.FRT;
import the_fireplace.frt.config.ConfigValues;
import the_fireplace.frt.recipes.ItemExploderRecipeManager;
import the_fireplace.frt.tools.MiscTools;

import java.util.Iterator;
import java.util.Map.Entry;

/**
 * @author The_Fireplace
 */
@MethodsReturnNonnullByDefault
public class TileEntityItemExploder extends TileEntity implements ISidedInventory {
	private ItemStack[] inventory;
	public static final String PROP_NAME = "TileEntityPopFurnace";
	int storedGunpowder = 0;
	int storedFirestarter = 0;

	private boolean isActive = false;
	private int tempItemCounter = 0;

	public TileEntityItemExploder() {
		inventory = new ItemStack[12];
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(this.pos, getBlockMetadata(), getUpdateTag());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}

	@Override
	public String getName() {
		return FRT.proxy.translateToLocal("tile.pop_furnace.name");
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return null;
	}

	@Override
	public int getSizeInventory() {
		return inventory.length;
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemStack : inventory)
			if (!itemStack.isEmpty())
				return false;
		return true;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		if (inventory[index] != null)
			return inventory[index];
		else
			return ItemStack.EMPTY;
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		ItemStack is = getStackInSlot(index);
		if (!is.isEmpty()) {
			if (is.getCount() <= count) {
				setInventorySlotContents(index, ItemStack.EMPTY);
			} else {
				is = is.splitStack(count);
				markDirty();
			}
		}
		return is;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		ItemStack is = getStackInSlot(index);
		setInventorySlotContents(index, ItemStack.EMPTY);
		return is;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		inventory[index] = stack;

		if (!stack.isEmpty() && stack.getCount() > getInventoryStackLimit()) {
			stack.setCount(getInventoryStackLimit());
		}
		markDirty();
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return player.getDistanceSq(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D) <= 64;
	}

	@Override
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public void closeInventory(EntityPlayer player) {
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return true;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		for (int i = 0; i < inventory.length; ++i) {
			inventory[i] = ItemStack.EMPTY;
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);

		NBTTagList list = new NBTTagList();
		for (int i = 0; i < getSizeInventory(); i++) {
			ItemStack is = getStackInSlot(i);
			if (!is.isEmpty()) {
				NBTTagCompound item = new NBTTagCompound();

				item.setByte("SlotPopFurnace", (byte) i);
				is.writeToNBT(item);

				list.appendTag(item);
			}
		}
		compound.setInteger("StoredFirestarter", storedFirestarter);
		compound.setInteger("StoredGunpowder", storedGunpowder);
		compound.setBoolean("IsPopFurnaceActive", isActive);
		compound.setInteger("CountUntilGunpowder", tempItemCounter);
		compound.setTag("ItemsPopFurnace", list);
		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		NBTTagList list = (NBTTagList) compound.getTag("ItemsPopFurnace");
		if (list != null) {
			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound item = (NBTTagCompound) list.get(i);
				int slot = item.getByte("SlotPopFurnace");
				if (slot >= 0 && slot < getSizeInventory()) {
					setInventorySlotContents(slot, new ItemStack(item));
				}
			}
		} else {
			FRT.logError("List was null when reading TileEntityItemExploder NBTTagCompound");
		}
		this.storedFirestarter = compound.getInteger("StoredFirestarter");
		this.storedGunpowder = compound.getInteger("StoredGunpowder");
		this.isActive = compound.getBoolean("IsPopFurnaceActive");
		this.tempItemCounter = compound.getInteger("CountUntilGunpowder");
	}

	public void addToGunpowder(int amount) {
		storedGunpowder += amount;
		if (this.world.getMinecraftServer() != null)
			for (WorldServer server : this.world.getMinecraftServer().worlds) {
				server.getPlayerChunkMap().markBlockForUpdate(getPos());
			}
	}

	public void removeFromGunpowder(int amount) {
		storedGunpowder -= amount;
		if (storedGunpowder < 0) {
			storedGunpowder = 0;
		}
		if (this.world.getMinecraftServer() != null)
			for (WorldServer server : this.world.getMinecraftServer().worlds) {
				server.getPlayerChunkMap().markBlockForUpdate(getPos());
			}
	}

	public void addToFireStarter(int amount) {
		storedFirestarter += amount;
		if (this.world.getMinecraftServer() != null)
			for (WorldServer server : this.world.getMinecraftServer().worlds) {
				server.getPlayerChunkMap().markBlockForUpdate(getPos());
			}
	}

	public void removeFromFirestarter(int amount) {
		storedFirestarter -= amount;
		if (storedFirestarter < 0) {
			storedFirestarter = 0;
		}
		if (this.world.getMinecraftServer() != null)
			for (WorldServer server : this.world.getMinecraftServer().worlds) {
				server.getPlayerChunkMap().markBlockForUpdate(getPos());
			}
	}

	public int getStoredGunpowder() {
		return storedGunpowder;
	}

	public int getStoredFirestarter() {
		return storedFirestarter;
	}

	public void popItems() {
		if (canPop()) {
			if (!isActive) {
				if (storedFirestarter > 0) {
					removeFromFirestarter(1);
					this.isActive = true;
				} else
					return;
			}
			popItem();
		} else {
			if (isActive) isActive = false;
		}
	}

	private boolean canPop() {
		return popItem(false);
	}

	private void popItem() {
		popItem(true);
	}

	private boolean popItem(boolean performPop) {
		Integer firstSuitableInputSlot = null;
		Integer firstSuitableOutputSlot = null;
		ItemStack result = null;

		for (int inputSlot = 0; inputSlot < 5; inputSlot++) {
			if (inventory[inputSlot] != null && ItemExploderRecipeManager.instance().getPoppingResult(inventory[inputSlot]) != null) {
				result = new ItemStack(ItemExploderRecipeManager.instance().getPoppingResult(inventory[inputSlot]).getItem(), ItemExploderRecipeManager.instance().getResultCount(inventory[inputSlot]), ItemExploderRecipeManager.instance().getPoppingResult(inventory[inputSlot]).getMetadata());
				if (!result.isEmpty()) {
					for (int outputSlot = 5; outputSlot < 10; outputSlot++) {
						ItemStack outputStack = inventory[outputSlot];
						if (outputStack == null) {
							firstSuitableInputSlot = inputSlot;
							firstSuitableOutputSlot = outputSlot;
							break;
						}

						if (outputStack.getItem() == result.getItem() && (!outputStack.getHasSubtypes() || outputStack.getMetadata() == outputStack.getMetadata()) && ItemStack.areItemStackTagsEqual(outputStack, result)) {
							int combinedSize = inventory[outputSlot].getCount() + result.getCount();
							if (combinedSize <= getInventoryStackLimit() && combinedSize <= inventory[outputSlot].getMaxStackSize()) {
								firstSuitableInputSlot = inputSlot;
								firstSuitableOutputSlot = outputSlot;
								break;
							}
						}
					}
					if (firstSuitableInputSlot != null) break;
				}
			}
		}

		if (firstSuitableInputSlot == null) return false;
		if (this.storedGunpowder <= 0) return false;
		if (!performPop) return true;

		if (!this.getWorld().isRemote)
			this.getWorld().playSound(null, this.pos, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 3.5F, (1.0F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F) * 1.5F);

		tempItemCounter++;
		if (tempItemCounter >= ConfigValues.ITEMSPERGUNPOWDER) {
			removeFromGunpowder(1);
			tempItemCounter = 0;
		}
		inventory[firstSuitableInputSlot].shrink(1);
		if (inventory[firstSuitableOutputSlot].isEmpty()) {
			inventory[firstSuitableOutputSlot] = result.copy();
		} else {
			inventory[firstSuitableOutputSlot].grow(result.getCount());
		}
		markDirty();
		return true;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		switch (side) {
			case EAST:
			case WEST:
			case NORTH:
			case SOUTH:
			case UP:
				return new int[]{0, 1, 2, 3, 4, 10, 11};
			case DOWN:
				return new int[]{5, 6, 7, 8, 9};
			default:
				throw new IllegalArgumentException("Invalid side: " + side);
		}
	}

	@Override
	public boolean canInsertItem(int index, ItemStack stack, EnumFacing direction) {
		if (!stack.isEmpty()) {
			if (index >= 0 && index < 5) {
				Iterator iterator = ItemExploderRecipeManager.instance().getPoppingList().entrySet().iterator();
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
			if (index == 10) {
				return ItemExploderRecipeManager.instance().isGunpowder(stack);
			}
			if (index == 11) {
				return ItemExploderRecipeManager.instance().isFirestarter(stack);
			}
		}
		return false;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return !stack.isEmpty() && index >= 5 && index < 10;
	}

	IItemHandler handlerTop = new SidedInvWrapper(this, EnumFacing.UP);
	IItemHandler handlerBottom = new SidedInvWrapper(this, EnumFacing.DOWN);
	IItemHandler handlerSide = new SidedInvWrapper(this, EnumFacing.WEST);

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (facing != null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			if (facing == EnumFacing.DOWN)
				return (T) handlerBottom;
			else if (facing == EnumFacing.UP)
				return (T) handlerTop;
			else
				return (T) handlerSide;
		return super.getCapability(capability, facing);
	}
}
