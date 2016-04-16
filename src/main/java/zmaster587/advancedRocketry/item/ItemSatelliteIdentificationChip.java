package zmaster587.advancedRocketry.item;

import java.util.List;

import zmaster587.advancedRocketry.api.SatelliteRegistry;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.api.satellite.SatelliteProperties;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.DimensionManager;

public class ItemSatelliteIdentificationChip extends Item {

	private static String name = "name";

	@Override
	public boolean isDamageable() {
		return false;
	}

	public long getSatelliteId(ItemStack stack) {
		if(stack.hasTagCompound()) {
			NBTTagCompound nbt = stack.getTagCompound();

			return nbt.getLong("satelliteId");
		}
		return -1;
	}

	public SatelliteBase getSatellite(ItemStack stack) {
		if(stack.hasTagCompound()) {
			NBTTagCompound nbt = stack.getTagCompound();

			long satId = nbt.getLong("satelliteId");

			SatelliteBase satellite = zmaster587.advancedRocketry.dimension.DimensionManager.getInstance().getSatellite(satId);

			if(satellite != null) {

				if(!nbt.hasKey("dimId") || nbt.getInteger("dimId") == -1) {
					nbt.setInteger("dimId", satellite.getDimensionId());
				}


				World world;
				if( !nbt.hasKey(null) && (world = DimensionManager.getWorld(satellite.getDimensionId())) != null)
					nbt.setString(name, world.provider.getDimensionName());
			}


			return satellite;
		}
		return null;
	}

	public void setSatellite(ItemStack stack, SatelliteBase satellite) {
		NBTTagCompound nbt;
		if(stack.hasTagCompound())
			nbt = stack.getTagCompound();
		else 
			nbt = new NBTTagCompound();

		nbt.setString("satelliteName", satellite.getName());
		nbt.setInteger("dimId", satellite.getDimensionId());
		nbt.setLong("satelliteId", satellite.getId());
	}

	/**
	 * Note: this method does not modify dimension info
	 * @param stack itemStack
	 * @param satellite properties of satellite to set info with
	 */
	public void setSatellite(ItemStack stack, SatelliteProperties satellite) {
		erase(stack);
		SatelliteBase satellite2 = SatelliteRegistry.getSatallite(satellite.getSatelliteType());
		if(satellite2 != null) {
			NBTTagCompound nbt;
			if(stack.hasTagCompound())
				nbt = stack.getTagCompound();
			else 
				nbt = new NBTTagCompound();


			nbt.setString("satelliteName", satellite2.getName());
			nbt.setLong("satelliteId", satellite.getId());

			stack.setTagCompound(nbt);
		}
	}

	public void erase(ItemStack stack) {
		stack.setTagCompound(null);
	}

	public void setDim(ItemStack stack, int dimId) {
		NBTTagCompound nbt;
		if(stack.hasTagCompound())
			nbt = stack.getTagCompound();
		else 
			return;

		nbt.setInteger("dimId", dimId);
	}

	public String getSatelliteName(ItemStack stack) {
		if(stack.hasTagCompound()) {
			NBTTagCompound nbt = stack.getTagCompound();

			return nbt.getString("satelliteName");
		}
		return "";
	}

	public int getWorldId(ItemStack stack) {
		NBTTagCompound nbt;

		if(stack.hasTagCompound() && (nbt = stack.getTagCompound()).hasKey("dimId") ) {


			return nbt.getInteger("dimId");
		}
		return -1; // Cant have a nether satellite anyway
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player,
			List list, boolean bool) {
		int worldId = getWorldId(stack);
		long satId = getSatelliteId(stack);

		String satelliteName = getSatelliteName(stack);

		if(satId != -1) {

			if(worldId != -1) {

				if(stack.getTagCompound().hasKey(name)) {

					list.add("ID: " + satId);
					list.add("Planet: " + stack.getTagCompound().getString(name));
					list.add("Satellite: " + satelliteName);
				}
				else {
					list.add("Planet: " +  "Unknown");
					list.add("Satellite: " + "Contact Lost"); //TODO: make satellite respond with name until
				}
			}
			else {
				list.add("ID: " + satId);
				list.add("Planet: Unknown");
				list.add("Satellite: " + satelliteName);
			}
		}
		else
			list.add("Unprogrammed");
	}
}
