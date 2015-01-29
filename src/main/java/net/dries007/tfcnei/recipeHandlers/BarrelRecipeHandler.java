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

import codechicken.nei.NEIClientConfig;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.GuiUsageRecipe;
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
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static codechicken.lib.gui.GuiDraw.getMousePosition;
import static net.dries007.tfcnei.util.Helper.getItemStacksForFluid;
import static net.minecraftforge.fluids.FluidContainerRegistry.getFluidForFilledItem;

/**
 * @author Dries007
 */
public class BarrelRecipeHandler extends TemplateRecipeHandler
{
    private static List<BarrelRecipe> recipeList;
    private static ItemStack[]        foodToBrine;
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
            foodToBrine = items.toArray(new ItemStack[items.size()]);
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
                arecipes.add(new CachedBarrelRecipe(recipe));
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

            if ((outItem != null && Helper.areItemStacksEqual(result, outItem)) ||
                (outFluid != null && outFluid.isFluidEqual(result)))
            {
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
            ItemStack inItem = recipe.getInItem();
            FluidStack inFluid = recipe.getInFluid();

            if ((inItem != null && Helper.areItemStacksEqual(inItem, ingredient)) ||
                (inFluid != null && inFluid.isFluidEqual(fluidStack)))
            {
                arecipes.add(new CachedBarrelRecipe(recipe));
            }
        }
    }

    @Override
    public void drawExtras(int recipe)
    {
        CachedRecipe crecipe = arecipes.get(recipe);
        if (crecipe instanceof CachedBarrelRecipe)
        {
            Helper.drawCenteredString(Minecraft.getMinecraft().fontRenderer, ((CachedBarrelRecipe) crecipe).techLvlString(), 83, 8, 0x820093);
            Helper.drawCenteredString(Minecraft.getMinecraft().fontRenderer, ((CachedBarrelRecipe) crecipe).sealTimeString(), 83, 48, 0x555555);

            if (((CachedBarrelRecipe) crecipe).getInFluid() != null)
                Helper.drawFluidInRect(((CachedBarrelRecipe) crecipe).getInFluid().getFluid(), recipeInFluidRect());
            if (((CachedBarrelRecipe) crecipe).getOutFluid() != null)
                Helper.drawFluidInRect(((CachedBarrelRecipe) crecipe).getOutFluid().getFluid(), recipeOutFluidRect());
        }
    }

    @Override
    public List<String> handleItemTooltip(GuiRecipe gui, ItemStack stack, List<String> currenttip, int recipe)
    {
        CachedRecipe irecipe = arecipes.get(recipe);
        if (irecipe instanceof CachedBarrelRecipe)
        {
            Point mousepos = getMousePosition();
            Point offset = gui.getRecipePosition(recipe);
            Point relMouse = new Point(mousepos.x - gui.guiLeft - offset.x, mousepos.y - gui.guiTop - offset.y);
            if (recipeOutFluidRect().contains(relMouse) &&
                (((CachedBarrelRecipe) irecipe).getOutFluid() != null))
                currenttip.add(Helper.tooltipForFluid(((CachedBarrelRecipe) irecipe).getOutFluid()));
            if (recipeInFluidRect().contains(relMouse) &&
                (((CachedBarrelRecipe) irecipe).getInFluid() != null))
                currenttip.add(Helper.tooltipForFluid(((CachedBarrelRecipe) irecipe).getInFluid()));
        }
        return currenttip;
    }

    @Override
    public boolean keyTyped(GuiRecipe gui, char keyChar, int keyCode, int recipe)
    {
        if (keyCode == NEIClientConfig.getKeyBinding("gui.recipe"))
        {
            if (transferFluid(gui, recipe, false))
                return true;
        }
        else if (keyCode == NEIClientConfig.getKeyBinding("gui.usage"))
        {
            if (transferFluid(gui, recipe, true))
                return true;
        }

        return super.keyTyped(gui, keyChar, keyCode, recipe);
    }

    @Override
    public boolean mouseClicked(GuiRecipe gui, int button, int recipe)
    {
        if (button == 0)
        {
            if (transferFluid(gui, recipe, false))
                return true;
        }
        else if (button == 1)
        {
            if (transferFluid(gui, recipe, true))
                return true;
        }

        return super.mouseClicked(gui, button, recipe);
    }

    private boolean transferFluid(GuiRecipe gui, int recipe, boolean usage)
    {
        CachedRecipe crecipe = arecipes.get(recipe);
        if (crecipe instanceof CachedBarrelRecipe)
        {
            Point mousepos = getMousePosition();
            Point offset = gui.getRecipePosition(recipe);
            Point relMouse = new Point(mousepos.x - gui.guiLeft - offset.x, mousepos.y - gui.guiTop - offset.y);
            ItemStack fluidStack = null;
            if (recipeOutFluidRect().contains(relMouse) &&
                (((CachedBarrelRecipe) crecipe).getOutFluid() != null))
                fluidStack = Helper.getItemStacksForFluid(((CachedBarrelRecipe) crecipe).getOutFluid())[0];
            if (recipeInFluidRect().contains(relMouse) &&
                (((CachedBarrelRecipe) crecipe).getInFluid() != null))
                fluidStack = Helper.getItemStacksForFluid(((CachedBarrelRecipe) crecipe).getInFluid())[0];
            if (fluidStack != null && (usage ? GuiUsageRecipe.openRecipeGui("item", fluidStack) : GuiCraftingRecipe.openRecipeGui("item", fluidStack)))
                return true;
        }
        return false;
    }

    public static Rectangle recipeInFluidRect()
    {
        return new Rectangle(12, 7, 8, 50);
    }

    public static Rectangle recipeOutFluidRect()
    {
        return new Rectangle(146, 7, 8, 50);
    }

    public class CachedBarrelRecipe extends CachedRecipe
    {
        int             minTechLevel, sealTime;
        PositionedStack inItem, outItem;
        FluidStack      inFluid, outFluid;

        public CachedBarrelRecipe(BarrelRecipe recipe)
        {
            this.minTechLevel = recipe.getMinTechLevel();
            this.sealTime = (recipe.isSealedRecipe()) ? recipe.getSealTime() : 0;
            this.inFluid = recipe.getInFluid();
            this.outFluid = recipe.getRecipeOutFluid();
            setInItem(recipe.getInItem());
            setOutItem(recipe.getRecipeOutIS());

            if (recipe instanceof BarrelLiquidToLiquidRecipe)
                setInItem(getItemStacksForFluid(((BarrelLiquidToLiquidRecipe) recipe).getInputfluid()));
            if (recipe instanceof BarrelMultiItemRecipe)
                this.outFluid = null;
            if (recipe instanceof BarrelVinegarRecipe)
                setInItem(fruitForVinegar);
            if (recipe instanceof BarrelBriningRecipe)
            {
                this.outFluid = null;
                setInItem(foodToBrine);
                setOutItem(foodToBrine);
            }
        }

        @Override
        public PositionedStack getIngredient()
        {
            if (inItem != null) randomRenderPermutation(inItem, cycleticks / 24);
            return inItem;
        }

        @Override
        public PositionedStack getResult()
        {
            if (outItem != null) randomRenderPermutation(outItem, cycleticks / 24);
            return outItem;
        }

        public void setInItem(Object inItem)
        {
            this.inItem = inItem == null ? null : new PositionedStack(inItem, 39, 24);
        }

        public void setOutItem(Object outItem)
        {
            this.outItem = outItem == null ? null : new PositionedStack(outItem, 111, 24);
        }

        public FluidStack getInFluid()
        {
            return inFluid;
        }

        public FluidStack getOutFluid()
        {
            return outFluid;
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
