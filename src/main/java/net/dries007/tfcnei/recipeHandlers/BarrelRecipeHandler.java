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
import codechicken.nei.recipe.TemplateRecipeHandler;
import com.bioxx.tfc.api.Crafting.BarrelBriningRecipe;
import com.bioxx.tfc.api.Crafting.BarrelLiquidToLiquidRecipe;
import com.bioxx.tfc.api.Crafting.BarrelManager;
import com.bioxx.tfc.api.Crafting.BarrelRecipe;
import com.bioxx.tfc.api.Interfaces.IFood;
import net.dries007.tfcnei.util.Constants;
import net.dries007.tfcnei.util.Helper;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.bioxx.tfc.Core.TFCFluid.BRINE;
import static cpw.mods.fml.relauncher.ReflectionHelper.getPrivateValue;
import static net.dries007.tfcnei.util.Helper.*;
import static net.minecraftforge.fluids.FluidContainerRegistry.BUCKET_VOLUME;
import static net.minecraftforge.fluids.FluidContainerRegistry.getFluidForFilledItem;

/**
 * @author Dries007
 */
public class BarrelRecipeHandler extends TemplateRecipeHandler
{
    private static List<BarrelRecipe> recipeList;
    private static ItemStack[]        fooditems;

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
            recipeList = getPrivateValue(BarrelManager.class, BarrelManager.getInstance(), "recipes");
            List<ItemStack> items = new ArrayList<>();
            for (Item item : (Iterable<Item>) Item.itemRegistry)
            {
                if (item instanceof IFood) item.getSubItems(item, CreativeTabs.tabAllSearch, items);
            }
            fooditems = items.toArray(new ItemStack[items.size()]);
        }
        return super.newInstance();
    }

    @Override
    public void loadTransferRects()
    {
        transferRects.add(new RecipeTransferRect(new Rectangle(60, 22, 30, 22), "barrel"));
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
    public void loadCraftingRecipes(ItemStack result)
    {
        for (BarrelRecipe recipe : recipeList)
        {
            ItemStack outItem = getPrivateValue(BarrelRecipe.class, recipe, "outItemStack");
            FluidStack outFluid = getPrivateValue(BarrelRecipe.class, recipe, "outFluid");

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
    public void drawExtras(int recipe)
    {
        super.drawExtras(recipe);
        CachedRecipe cr = arecipes.get(recipe);
        if (cr instanceof CachedBarrelRecipe) Helper.drawCenteredString(Minecraft.getMinecraft().fontRenderer, ((CachedBarrelRecipe) cr).techLvlString(), 80, 10, 0x820093);
    }

    public class CachedBarrelRecipe extends CachedRecipe
    {
        int             minTechLevel;
        PositionedStack inItem, inFluid;
        PositionedStack outItem, outFluid;

        public CachedBarrelRecipe(BarrelLiquidToLiquidRecipe recipe)
        {
            this(recipe.minTechLevel, getPrivateFluidStack(BarrelLiquidToLiquidRecipe.class, recipe, "inputfluid"), getPrivateFluidStack(BarrelRecipe.class, recipe, "barrelFluid"), getPrivateItemStack(BarrelRecipe.class, recipe, "outItemStack"), getPrivateFluidStack(BarrelRecipe.class, recipe, "outFluid"));
        }

        public CachedBarrelRecipe(BarrelRecipe recipe)
        {
            this(recipe.minTechLevel, getPrivateItemStack(BarrelRecipe.class, recipe, "inItemStack"), getPrivateFluidStack(BarrelRecipe.class, recipe, "barrelFluid"), getPrivateItemStack(BarrelRecipe.class, recipe, "outItemStack"), getPrivateFluidStack(BarrelRecipe.class, recipe, "outFluid"));
        }

        public CachedBarrelRecipe(int minTechLevel, FluidStack inFluid1, FluidStack inFluid2, ItemStack outItem, FluidStack outFluid)
        {
            this(minTechLevel, getItemStacksForFluid(inFluid1), inFluid2, outItem, outFluid);
        }

        /**
         * @param inItem Itemstack or ItemStack[]
         */
        public CachedBarrelRecipe(int minTechLevel, Object inItem, FluidStack inFluid, ItemStack outItem, FluidStack outFluid)
        {
            this.minTechLevel = minTechLevel;
            this.inItem = inItem == null ? null : new PositionedStack(inItem, 3, 24);
            this.outItem = outItem == null ? null : new PositionedStack(outItem, 99, 24);
            ItemStack inFluidStack[] = getItemStacksForFluid(inFluid);
            this.inFluid = inFluidStack == null ? null : new PositionedStack(inFluidStack, 39, 24);
            ItemStack outFluidStack[] = getItemStacksForFluid(outFluid);
            this.outFluid = outFluidStack == null ? null : new PositionedStack(outFluidStack, 143, 24);
        }

        public CachedBarrelRecipe(int minTechLevel)
        {
            this.minTechLevel = minTechLevel;
            this.inItem = new PositionedStack(fooditems, 3, 24);
            this.outItem = new PositionedStack(fooditems, 99, 24);
            this.inFluid = new PositionedStack(getItemStacksForFluid(new FluidStack(BRINE, BUCKET_VOLUME)), 39, 24);
        }

        @Override
        public List<PositionedStack> getIngredients()
        {
            if (inItem != null) randomRenderPermutation(inItem, cycleticks / 12);
            if (inFluid != null) randomRenderPermutation(inFluid, cycleticks / 12);
            ArrayList<PositionedStack> list = new ArrayList<>(2);
            if (inItem != null) list.add(inItem);
            if (inFluid != null) list.add(inFluid);
            return list;
        }

        @Override
        public PositionedStack getOtherStack()
        {
            if (outFluid != null) randomRenderPermutation(outFluid, cycleticks / 12);
            return outFluid;
        }

        @Override
        public PositionedStack getResult()
        {
            if (outItem != null) randomRenderPermutation(outItem, cycleticks / 12);
            return outItem;
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
