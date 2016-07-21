package frogcraftrebirth.common.block;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import frogcraftrebirth.common.item.ItemIngot;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

public class BlockNitricAcid extends BlockFluidClassic {
	
	public BlockNitricAcid(Fluid fluid) {
		super(fluid, Material.water);
		this.setBlockName("nitricAcid");
		this.setDensity(fluid.getDensity());
		this.setQuantaPerBlock(8);
		this.setTickRate(10);
	}
	
	@Override
	public boolean canDisplace(IBlockAccess world, int x, int y, int z) {
		if (world.getBlock(x, y, z).getMaterial().isLiquid()) {
			return false;
		}
		return super.canDisplace(world, x, y, z);
	}

	@Override
	public boolean displaceIfPossible(World world, int x, int y, int z) {
		if (world.getBlock(x, y, z).getMaterial().isLiquid()) {
			return false;
		}
		return super.displaceIfPossible(world, x, y, z);
	}

	private int corrosion;
	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		super.updateTick(world, x, y, z, rand);
		if (rand.nextBoolean()) return;
		
		for (int m=-3;m<3;m++) {
			for (int n=-3;n<3;n++) {
				int randInt = rand.nextInt(10);
				if (world.getBlock(x+m, y, z+n) == Blocks.grass && randInt < 7) {
					world.setBlock(x+m, y, z+n, Blocks.dirt);
					++corrosion;
					checkCorrosion(world, x, y, z);
				}
				if (world.getBlock(x+m, y, z+n) == Blocks.dirt && randInt < 5) {
					world.setBlock(x+m, y, z+n, Blocks.sand);
					++corrosion;
					checkCorrosion(world, x, y, z);
				}
				if (world.getBlock(x+m, y, z+n) == Blocks.stone && randInt < 5) {
					world.setBlock(x+m, y, z+n, Blocks.cobblestone);
					++corrosion;
					checkCorrosion(world, x, y, z);
				}
				if (world.getBlock(x+m, y, z+n) == Blocks.cobblestone && randInt < 8) {
					world.setBlock(x+m, y, z+n, Blocks.gravel);
					++corrosion;
					checkCorrosion(world, x, y, z);
				}
				if (world.getBlock(x+m, y, z+n) == Blocks.gravel && randInt < 6) {
					world.setBlock(x+m, y, z+n, Blocks.sand);
					++corrosion;
					checkCorrosion(world, x, y, z);	
				}
			}
		}
	}
	
	private void checkCorrosion(World world, int x, int y, int z) {
		if (corrosion > 20)
			world.setBlockToAir(new BlockPos(x, y, z));
	}
	
	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
		super.onEntityCollidedWithBlock(world, pos, state, entity);
		if (entity instanceof EntityItem) {
			ItemStack stack = ((EntityItem)entity).getEntityItem();
			if (stack.getItem() instanceof ItemIngot && stack.getItemDamage() == 0) {
				world.createExplosion(entity, pos.getX(), pos.getY(), pos.getZ(), 15F, true);
				//TODO: modify explosion scale
			}
		}
	}

}
