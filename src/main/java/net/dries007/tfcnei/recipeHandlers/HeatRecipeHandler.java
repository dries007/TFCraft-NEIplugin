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
import com.bioxx.tfc.Containers.Slots.SlotCookableFoodOnly;
import com.bioxx.tfc.Containers.Slots.SlotFirepitIn;
import com.bioxx.tfc.Core.TFC_Core;
import com.bioxx.tfc.Food.ItemFoodTFC;
import com.bioxx.tfc.Items.ItemMeltedMetal;
import com.bioxx.tfc.Items.ItemOre;
import com.bioxx.tfc.api.*;
import com.bioxx.tfc.api.Interfaces.ISmeltable;
import net.dries007.tfcnei.util.Constants;
import net.dries007.tfcnei.util.Helper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Dries007
 */
public class HeatRecipeHandler extends TemplateRecipeHandler
{
    private static List<HeatIndex> recipeList;
    private static Item firepit, grill, forge, crucible;
    private static Slot firepitSlot = new SlotFirepitIn(null, null, 0, 0, 0);
    private static Slot grillSlot = new SlotCookableFoodOnly(null, 0, 0, 0);

    @Override
    public String getGuiTexture()
    {
        return Constants.HEATING_TEXTURE.toString();
    }

    @Override
    public String getRecipeName()
    {
        return "Heating";
    }

    @Override
    public String getOverlayIdentifier()
    {
        return "heating";
    }

    @Override
    public void loadTransferRects()
    {
        transferRects.add(new RecipeTransferRect(new Rectangle(25, 9 + 18, 18, 10), "heating"));
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results)
    {
        if (outputId.equals("heating") && getClass() == HeatRecipeHandler.class)
        {
            for (HeatIndex recipe : recipeList)
            {
                arecipes.add(new CachedHeatRecipe(recipe));
            }
        }
        else super.loadCraftingRecipes(outputId, results);
    }

    @SuppressWarnings("unchecked")
    @Override
    public TemplateRecipeHandler newInstance()
    {
        if (recipeList == null)
        {
            recipeList = HeatRegistry.getInstance().getHeatList();
            firepit = Item.getItemFromBlock(TFCBlocks.firepit);
            grill = Item.getItemFromBlock(TFCBlocks.grill);
            forge = Item.getItemFromBlock(TFCBlocks.forge);
            crucible = Item.getItemFromBlock(TFCBlocks.crucible);
        }
        return super.newInstance();
    }

    @Override
    public void loadCraftingRecipes(ItemStack result)
    {
        for (HeatIndex recipe : recipeList)
        {
            if (Helper.areItemStacksEqual(result, recipe.getOutput(new Random()))) arecipes.add(new CachedHeatRecipe(recipe));
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient)
    {
        Item item = ingredient.getItem();
        for (HeatIndex recipe : recipeList)
        {
            if ((item == firepit && isValidForFirepit(recipe)) || (item == grill && isValidForGrill(recipe)) || (item == forge && isValidForForge(recipe)) || (item == crucible && isValidForCrucible(recipe)))
            {
                arecipes.add(new CachedHeatRecipe(recipe));
            }
            else if (recipe.matches(ingredient))
            {
                // override the input to be exactly what was requested
                arecipes.add(new CachedHeatRecipe(recipe, ingredient));
            }
        }
    }

    @Override
    public void drawExtras(int recipe)
    {
        super.drawExtras(recipe);
        CachedRecipe cr = arecipes.get(recipe);
        if (cr instanceof CachedHeatRecipe) ((CachedHeatRecipe) cr).drawExtras();
    }

    private boolean isValidForFirepit(HeatIndex recipe)
    {
        return firepitSlot.isItemValid(recipe.input) && recipe.meltTemp < 1360;
    }

    private boolean isValidForGrill(HeatIndex recipe)
    {
        return grillSlot.isItemValid(recipe.input) && recipe.meltTemp < 1360;
    }

    private boolean isValidForForge(HeatIndex recipe)
    {
        return !(recipe.input.getItem() instanceof ItemOre) && !(recipe.input.getItem() instanceof ItemFoodTFC);
    }

    private boolean isValidForCrucible(HeatIndex recipe)
    {
        Item item = recipe.input.getItem();
        return ((item instanceof ISmeltable && ((ISmeltable)item).isSmeltable(recipe.input)) || item instanceof ItemMeltedMetal) && item != TFCItems.rawBloom && (item != TFCItems.bloom || recipe.input.getItemDamage() <= 100) && !TFC_Core.isOreIron(recipe.input);
    }

    public class CachedHeatRecipe extends CachedRecipe
    {
        final PositionedStack ingred;
        final PositionedStack result;
        final String temp;
        final List<PositionedStack> heatingItems = new ArrayList<>(5);

        public CachedHeatRecipe(HeatIndex recipe)
        {
            this(recipe, recipe.input);
        }

        public CachedHeatRecipe(HeatIndex recipe, ItemStack input)
        {
            this.ingred = new PositionedStack(recipe.input, 25, 9);
            ItemStack result = getActualResult(recipe.getOutput(input, new Random()), input);
            this.result = result == null ? null : new PositionedStack(result, 25, 37);
            this.temp = TFC_ItemHeat.getHeatColor(recipe.meltTemp, Integer.MAX_VALUE);

            if (isValidForFirepit(recipe)) heatingItems.add(new PositionedStack(new ItemStack(firepit), 50, 20));
            if (isValidForGrill(recipe)) heatingItems.add(new PositionedStack(new ItemStack(grill), 70, 20));
            if (isValidForForge(recipe)) heatingItems.add(new PositionedStack(new ItemStack(forge), 90, 20));
            if (isValidForCrucible(recipe)) heatingItems.add(new PositionedStack(new ItemStack(crucible), 110, 20));
        }

        private ItemStack getActualResult(ItemStack result, ItemStack ingred)
        {
            if (result != null) return result;
            if (ingred.getItem() instanceof ISmeltable)
            {
                ISmeltable smelt = (ISmeltable)ingred.getItem();
                ItemStack smeltedItem = new ItemStack(smelt.getMetalType(ingred).meltedItem);
                int units = smelt.getMetalReturnAmount(ingred);
                smeltedItem.stackSize = units / 100;
                return smeltedItem;
            }
            return ingred;
        }

        @Override
        public PositionedStack getResult()
        {
            return result;
        }

        @Override
        public PositionedStack getIngredient()
        {
            return ingred;
        }

        @Override
        public List<PositionedStack> getOtherStacks()
        {
            for (PositionedStack stack : heatingItems) stack.setPermutationToRender(cycleticks / 24 % stack.items.length);
            return heatingItems;
        }

        public void drawExtras()
        {
            FontRenderer renderer = Minecraft.getMinecraft().fontRenderer;
            renderer.drawString("Can be heated in: ", 54, 9, 0x000000);
            renderer.drawString("Temp: ", 54, 40, 0x000000);
            renderer.drawString(temp, 84, 40, 0x000000);
        }
    }
}
