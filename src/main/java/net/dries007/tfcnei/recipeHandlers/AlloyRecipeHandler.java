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
import com.bioxx.tfc.Core.Metal.Alloy;
import com.bioxx.tfc.Core.Metal.AlloyManager;
import com.bioxx.tfc.Core.Metal.AlloyMetal;
import com.bioxx.tfc.Core.Metal.AlloyMetalCompare;
import com.bioxx.tfc.Items.ItemOre;
import com.bioxx.tfc.TFCItems;
import com.bioxx.tfc.api.Metal;
import com.google.common.collect.HashMultimap;
import net.dries007.tfcnei.util.Constants;
import net.dries007.tfcnei.util.Helper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Dries007
 */
public class AlloyRecipeHandler extends TemplateRecipeHandler
{
    private static List<Alloy> alloyList;
    private static HashMultimap<Metal, ItemStack> metalItemMap = HashMultimap.create();

    @Override
    public String getGuiTexture()
    {
        return Constants.ALLOY_TEXTURE.toString();
    }

    @Override
    public String getRecipeName()
    {
        return "Alloy";
    }

    @Override
    public String getOverlayIdentifier()
    {
        return "alloy";
    }

    @SuppressWarnings("unchecked")
    @Override
    public TemplateRecipeHandler newInstance()
    {
        if (alloyList == null)
        {
            alloyList = AlloyManager.instance.Alloys;
            Metal metal;
            ItemStack itemStack = new ItemStack(TFCItems.OreChunk);
            while ((metal = ((ItemOre) TFCItems.OreChunk).GetMetalType(itemStack)) != null)
            {
                metalItemMap.put(metal, itemStack.copy());
                itemStack.setItemDamage(itemStack.getItemDamage() + 1);
            }
        }
        return super.newInstance();
    }

    @Override
    public void loadTransferRects()
    {
        transferRects.add(new RecipeTransferRect(new Rectangle(0, 30, 160, 30), "alloy"));
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results)
    {
        if (outputId.equals("alloy") && getClass() == AlloyRecipeHandler.class)
        {
            for (Alloy recipe : alloyList) arecipes.add(new CachedAlloyRecipe(recipe));
        }
        else super.loadCraftingRecipes(outputId, results);
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient)
    {
        for (Alloy recipe : alloyList)
        {
            for (AlloyMetal alloyMetal : recipe.AlloyIngred)
            {
                if (alloyMetal.metalType.MeltedItem == ingredient.getItem() || alloyMetal.metalType.Ingot == ingredient.getItem()) arecipes.add(new CachedAlloyRecipe(recipe));
                else if (ingredient.getItem() instanceof ItemOre && ((ItemOre) ingredient.getItem()).GetMetalType(ingredient) == alloyMetal.metalType) arecipes.add(new CachedAlloyRecipe(recipe));
            }
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result)
    {
        for (Alloy recipe : alloyList)
        {
            if (recipe.outputType.Ingot == result.getItem() || recipe.outputType.MeltedItem == result.getItem()) arecipes.add(new CachedAlloyRecipe(recipe));
        }
    }

    @Override
    public void drawExtras(int recipe)
    {
        super.drawExtras(recipe);
        CachedRecipe cr = arecipes.get(recipe);
        if (cr instanceof CachedAlloyRecipe) ((CachedAlloyRecipe) cr).drawExtras();
    }

    public static final int SPACING = 30;

    public class CachedAlloyRecipe extends CachedRecipe
    {
        private PositionedStack outItem;
        private ArrayList<PositionedStack> ingredients = new ArrayList<>();
        private ArrayList<String>          min         = new ArrayList<>();
        private ArrayList<String>          max         = new ArrayList<>();
        private String                     tech        = "?";

        public CachedAlloyRecipe(Alloy recipe)
        {
            outItem = new PositionedStack(new ItemStack[]{new ItemStack(recipe.outputType.MeltedItem), new ItemStack(recipe.outputType.Ingot)}, 10, 10);
            int x = SPACING / 2;
            for (AlloyMetal alloyMetal : recipe.AlloyIngred)
            {
                List<ItemStack> list = new LinkedList<>();
                list.add(new ItemStack(alloyMetal.metalType.MeltedItem));
                list.add(new ItemStack(alloyMetal.metalType.Ingot));
                list.addAll(metalItemMap.get(alloyMetal.metalType));
                ingredients.add(new PositionedStack(list, x += SPACING, 10));

                if (alloyMetal instanceof AlloyMetalCompare)
                {
                    min.add(String.format("%2.0f%%", Helper.getPrivateValue(AlloyMetalCompare.class, float.class, (AlloyMetalCompare) alloyMetal, "metalMin")));
                    max.add(String.format("%2.0f%%", Helper.getPrivateValue(AlloyMetalCompare.class, float.class, (AlloyMetalCompare) alloyMetal, "metalMax")));
                }
                else
                {
                    min.add("100%");
                    max.add("");
                }
            }

            tech = String.valueOf(Helper.getPrivateValue(Alloy.class, Alloy.EnumTier.class, recipe, "furnaceTier").tier);

            switch (Helper.getPrivateValue(Alloy.class, Alloy.EnumTier.class, recipe, "furnaceTier").tier)
            {
                case 1:
                    tech = "Pit Kiln";
                    break;
                case 2:
                    tech = "Beehive Kiln";
                    break;
                case 3:
                    tech = "Bloomery";
                    break;
                case 4:
                    tech = "Blast Furnace";
                    break;
                case 5:
                    tech = "Crucible";
                    break;
            }
        }

        @Override
        public List<PositionedStack> getIngredients()
        {
            for (PositionedStack positionedStack : ingredients) randomRenderPermutation(positionedStack, cycleticks / 12);
            return ingredients;
        }

        @Override
        public PositionedStack getResult()
        {
            randomRenderPermutation(outItem, cycleticks / 12);
            return outItem;
        }

        public void drawExtras()
        {
            FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
            Helper.drawCenteredString(fr, tech, 80, 0, 0x820093);
            int x = 16;
            for (String min1 : min) fr.drawString(min1, x += SPACING, 30, 0x000000);
            x = 16;
            for (String max1 : max) fr.drawString(max1, x += SPACING, 40, 0x000000);
        }
    }
}
