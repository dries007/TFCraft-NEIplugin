/*
 * Copyright (c) 2014 Dries007
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted (subject to the limitations in the
 * disclaimer below) provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the
 *    distribution.
 *
 *  * Neither the name of Dries007 nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE
 * GRANTED BY THIS LICENSE.  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT
 * HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
 * IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.dries007.tfcnei.recipeHandlers;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.TemplateRecipeHandler;

import com.bioxx.tfc.api.Crafting.BarrelBriningRecipe;
import com.bioxx.tfc.api.Crafting.BarrelLiquidToLiquidRecipe;
import com.bioxx.tfc.api.Crafting.BarrelManager;
import com.bioxx.tfc.api.Crafting.BarrelMultiItemRecipe;
import com.bioxx.tfc.api.Crafting.BarrelRecipe;
import com.bioxx.tfc.api.Crafting.BarrelVinegarRecipe;
import com.bioxx.tfc.api.Enums.EnumFoodGroup;
import com.bioxx.tfc.api.Interfaces.IFood;

import net.dries007.tfcnei.util.Constants;
import net.dries007.tfcnei.util.Helper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import static codechicken.lib.gui.GuiDraw.*;
import static com.bioxx.tfc.Core.TFCFluid.BRINE;
import static net.dries007.tfcnei.util.Helper.getItemStacksForFluid;
import static net.minecraftforge.fluids.FluidContainerRegistry.BUCKET_VOLUME;
import static net.minecraftforge.fluids.FluidContainerRegistry.getFluidForFilledItem;

/**
 * @author Dries007
 */
public class BarrelRecipeHandler extends TemplateRecipeHandler
{
    private static List<BarrelRecipe> recipeList;
    private static ItemStack[]        fooditems;
    private static ItemStack[]        fruitForVinegar;

    @Override
    public String getGuiTexture()
    {
        return Constants.BARREL_TEXTURE.toString();
    }

    @Override
    public String getRecipeName()
    {
        return "Barrel";
    }

    @Override
    public String getOverlayIdentifier()
    {
        return "barrel";
    }

    @SuppressWarnings("unchecked")
    @Override
    public TemplateRecipeHandler newInstance()
    {
        if (recipeList == null)
        {
            recipeList = BarrelManager.getInstance().getRecipes();
            List<ItemStack> items = new ArrayList<>();
            List<ItemStack> fruits = new ArrayList<>();
            for (Item item : (Iterable<Item>) Item.itemRegistry)
            {
                if (item instanceof IFood)
                {
                    item.getSubItems(item, CreativeTabs.tabAllSearch, items);
                    if (((IFood) item).getFoodGroup() == EnumFoodGroup.Fruit)
                        item.getSubItems(item, CreativeTabs.tabAllSearch, fruits);
                }
            }
            fooditems = items.toArray(new ItemStack[items.size()]);
            fruitForVinegar = fruits.toArray(new ItemStack[fruits.size()]); 
        }
        return super.newInstance();
    }

    @Override
    public void loadTransferRects()
    {
        transferRects.add(new RecipeTransferRect(new Rectangle(71, 23, 24, 18), "barrel"));
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results)
    {
        if (outputId.equals("barrel") && getClass() == BarrelRecipeHandler.class)
        {
            for (BarrelRecipe recipe : recipeList)
            {
                if (recipe instanceof BarrelLiquidToLiquidRecipe)
                    arecipes.add(new CachedBarrelRecipe((BarrelLiquidToLiquidRecipe) recipe));
                else if (recipe instanceof BarrelBriningRecipe)
                    arecipes.add(new CachedBarrelRecipe(recipe.minTechLevel));
                else
                    arecipes.add(new CachedBarrelRecipe(recipe));
            }
        }
        else
            super.loadCraftingRecipes(outputId, results);
    }

    @Override
    public void loadCraftingRecipes(ItemStack result)
    {
        for (BarrelRecipe recipe : recipeList)
        {
            ItemStack outItem = recipe.getRecipeOutIS();
            FluidStack outFluid = recipe.getRecipeOutFluid();

            Fluid fluid = null;
            if (result.getItem() == Item.getItemFromBlock(Blocks.sponge)) fluid = FluidRegistry.getFluid(result.getTagCompound().getString("FLUID"));

            if ((outItem != null && Helper.areItemStacksEqual(result, outItem) || (outFluid != null && (outFluid.isFluidEqual(result) || (fluid != null && outFluid.getFluid() == fluid)))))
            {
                if (recipe instanceof BarrelLiquidToLiquidRecipe)
                    arecipes.add(new CachedBarrelRecipe((BarrelLiquidToLiquidRecipe) recipe));
                else if (recipe instanceof BarrelBriningRecipe)
                    arecipes.add(new CachedBarrelRecipe(recipe.minTechLevel));
                else
                    arecipes.add(new CachedBarrelRecipe(recipe));
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient)
    {
        FluidStack fluidStack = getFluidForFilledItem(ingredient);
        for (BarrelRecipe recipe : recipeList)
        {
            try
            {
                if (Helper.areItemStacksEqual(recipe.getInItem(), ingredient))
                {
                    if (recipe instanceof BarrelLiquidToLiquidRecipe)
                        arecipes.add(new CachedBarrelRecipe((BarrelLiquidToLiquidRecipe) recipe));
                    else if (recipe instanceof BarrelBriningRecipe)
                        arecipes.add(new CachedBarrelRecipe(recipe.minTechLevel));
                    else
                        arecipes.add(new CachedBarrelRecipe(recipe));
                }
            }
            catch (NullPointerException e)
            {
            }

            try
            {
                if (ingredient.getItem() == Item.getItemFromBlock(Blocks.sponge))
                {
                    fluidStack = new FluidStack(FluidRegistry.getFluid(ingredient.getTagCompound().getString("FLUID")), ingredient.getMaxStackSize() * BUCKET_VOLUME);
                }
                if (recipe.isInFluid(fluidStack))
                {
                    if (recipe instanceof BarrelLiquidToLiquidRecipe)
                        arecipes.add(new CachedBarrelRecipe((BarrelLiquidToLiquidRecipe) recipe));
                    else if (recipe instanceof BarrelBriningRecipe)
                        arecipes.add(new CachedBarrelRecipe(recipe.minTechLevel));
                    else
                        arecipes.add(new CachedBarrelRecipe(recipe));
                }
            }
            catch (NullPointerException e)
            {
            }
        }
    }

    @Override
    public void drawExtras(int recipe)
    {
        super.drawExtras(recipe);
        CachedRecipe cr = arecipes.get(recipe);
        if (cr instanceof CachedBarrelRecipe)
        {
            Helper.drawCenteredString(Minecraft.getMinecraft().fontRenderer, ((CachedBarrelRecipe) cr).techLvlString(), 83, 8, 0x820093);
            Helper.drawCenteredString(Minecraft.getMinecraft().fontRenderer, ((CachedBarrelRecipe) cr).sealTimeString(), 83, 48, 0x555555);

            ((CachedBarrelRecipe) cr).drawInFluid();
            ((CachedBarrelRecipe) cr).drawOutFluid();
        }
    }

    @Override
    public List<String> handleItemTooltip(GuiRecipe gui, ItemStack stack, List<String> currenttip, int recipe)
    {
        CachedRecipe irecipe = arecipes.get(recipe);
        Point mousepos = getMousePosition();
        Point offset = gui.getRecipePosition(recipe);
        Point relMouse = new Point(mousepos.x - gui.guiLeft - offset.x, mousepos.y - gui.guiTop - offset.y);

        if (irecipe instanceof CachedBarrelRecipe)
        {
            if (((CachedBarrelRecipe) irecipe).getOutFluidRect().contains(relMouse))
                currenttip.add(((CachedBarrelRecipe) irecipe).getOutFluidTooltip());
            if (((CachedBarrelRecipe) irecipe).getInFluidRect().contains(relMouse))
                currenttip.add(((CachedBarrelRecipe) irecipe).getInFluidTooltip());
        }
        return currenttip;
    }

    public class CachedBarrelRecipe extends CachedRecipe
    {
        int             minTechLevel, sealTime;
        PositionedStack inItem, outItem;
        FluidStack      inFluid, outFluid;

        public CachedBarrelRecipe(BarrelLiquidToLiquidRecipe recipe)
        {
            this(recipe.minTechLevel, recipe.sealTime, recipe.getInputfluid(), recipe.getInFluid(), recipe.getRecipeOutIS(), recipe.getRecipeOutFluid());
        }

        public CachedBarrelRecipe(BarrelRecipe recipe)
        {
            this(recipe.minTechLevel, recipe.sealTime, (recipe instanceof BarrelVinegarRecipe) ? fruitForVinegar : recipe.getInItem(), recipe.getInFluid(), recipe.getRecipeOutIS(), (recipe instanceof BarrelMultiItemRecipe) ? null : recipe.getRecipeOutFluid());
        }

        public CachedBarrelRecipe(int minTechLevel, int sealTime, FluidStack inFluid1, FluidStack inFluid2, ItemStack outItem, FluidStack outFluid)
        {
            this(minTechLevel, sealTime, getItemStacksForFluid(inFluid1), inFluid2, outItem, outFluid);
        }

        /**
         * @param inItem Itemstack or ItemStack[]
         */
        public CachedBarrelRecipe(int minTechLevel, int sealTime, Object inItem, FluidStack inFluid, ItemStack outItem, FluidStack outFluid)
        {
            this.minTechLevel = minTechLevel;
            this.sealTime = sealTime;
            this.inItem = inItem == null ? null : new PositionedStack(inItem, 39, 24);
            this.outItem = outItem == null ? null : new PositionedStack(outItem, 111, 24);
            this.inFluid = inFluid;
            this.outFluid = outFluid;
        }

        public CachedBarrelRecipe(int minTechLevel)
        {
            this.minTechLevel = minTechLevel;
            this.sealTime = 0;
            this.inItem = new PositionedStack(fooditems, 39, 24);
            this.outItem = new PositionedStack(fooditems, 111, 24);
            this.inFluid = new FluidStack(BRINE, BUCKET_VOLUME);
        }

        @Override
        public PositionedStack getIngredient()
        {
            if (inItem != null) randomRenderPermutation(inItem, cycleticks / 12);
            return inItem;
        }

        @Override
        public PositionedStack getResult()
        {
            if (outItem != null) randomRenderPermutation(outItem, cycleticks / 12);
            return outItem;
        }

        public Rectangle getInFluidRect()
        {
            return new Rectangle(11, 6, 10, 52);
        }

        public String getInFluidTooltip()
        {
            if (inFluid != null)
                return inFluid.getLocalizedName() + " (" + inFluid.amount + "mB)";
            else
                return "Empty";
        }

        public void drawInFluid()
        {
            if (inFluid != null)
            {
                IIcon inFluidIcon = inFluid.getFluid().getIcon();
                Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
                int color = inFluid.getFluid().getColor();
                GL11.glColor4ub((byte)((color >> 16) & 255), (byte)((color >> 8) & 255), (byte)(color & 255), (byte)(0xaa & 255));
                gui.drawTexturedModelRectFromIcon(12, 7, inFluidIcon, 8, 50);
            }
        }

        public Rectangle getOutFluidRect()
        {
            return new Rectangle(145, 6, 10, 52);
        }

        public String getOutFluidTooltip()
        {
            if (outFluid != null)
                return outFluid.getLocalizedName() + " (" + outFluid.amount + "mB)";
            else
                return "Empty";
        }

        public void drawOutFluid()
        {
            if (outFluid != null)
            {
                IIcon outFluidIcon = outFluid.getFluid().getIcon();
                Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
                int color = outFluid.getFluid().getColor();
                GL11.glColor4ub((byte)((color >> 16) & 255), (byte)((color >> 8) & 255), (byte)(color & 255), (byte)(0xaa & 255));
                gui.drawTexturedModelRectFromIcon(146, 7, outFluidIcon, 8, 50);
            }
        }

        public String sealTimeString()
        {
            if (sealTime == 0)
                return "Instant";
            else
                return sealTime + " hours";
        }

        public String techLvlString()
        {
            switch (minTechLevel)
            {
                case 0:
                    return "Vessel or Barrel";
                case 1:
                    return "Barrel only";
                default:
                    return "Unknown.";
            }
        }
    }
}
