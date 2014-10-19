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
import com.bioxx.tfc.api.Crafting.KilnCraftingManager;
import com.bioxx.tfc.api.Crafting.KilnRecipe;
import net.dries007.tfcnei.util.Constants;
import net.dries007.tfcnei.util.Helper;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import java.awt.*;
import java.util.List;

/**
 * @author Dries007
 */
public class KilnRecipeHandler extends TemplateRecipeHandler
{
    private static List<KilnRecipe> recipeList;

    @Override
    public String getGuiTexture()
    {
        return Constants.KILN_TEXTURE.toString();
    }

    @Override
    public String getRecipeName()
    {
        return "Pit Kiln";
    }

    @Override
    public String getOverlayIdentifier()
    {
        return "pitkiln";
    }

    @Override
    public void loadTransferRects()
    {
        transferRects.add(new RecipeTransferRect(new Rectangle(43, 44 - 18, 18, 18), "pitkiln"));
        transferRects.add(new RecipeTransferRect(new Rectangle(88, 44 - 18, 25, 18), "pitkiln"));
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results)
    {
        if (outputId.equals("pitkiln") && getClass() == KilnRecipeHandler.class)
        {
            for (KilnRecipe recipe : recipeList) arecipes.add(new CachedKilnRecipe(recipe));
        }
        else
            super.loadCraftingRecipes(outputId, results);
    }

    @SuppressWarnings("unchecked")
    @Override
    public TemplateRecipeHandler newInstance()
    {
        if (recipeList == null) recipeList = KilnCraftingManager.getInstance().getRecipeList();
        return super.newInstance();
    }

    @Override
    public void loadCraftingRecipes(ItemStack result)
    {
        for (KilnRecipe recipe : recipeList)
            if (Helper.areItemStacksEqual(result, recipe.getCraftingResult()))
                arecipes.add(new CachedKilnRecipe(recipe));
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient)
    {
        for (KilnRecipe recipe : recipeList)
        {
            ItemStack in = Helper.getPrivateItemStack(KilnRecipe.class, recipe, "input1");
            if (Helper.areItemStacksEqual(ingredient, in))
                arecipes.add(new CachedKilnRecipe(in, recipe.getCraftingResult()));
        }
    }

    public class CachedKilnRecipe extends CachedRecipe
    {
        PositionedStack ingred;
        PositionedStack result;

        public CachedKilnRecipe(ItemStack ingred, ItemStack result)
        {
            this.ingred = new PositionedStack(ingred, 43, 44);
            this.result = new PositionedStack(result, 119, 24);
        }

        public CachedKilnRecipe(KilnRecipe recipe)
        {
            this(Helper.getPrivateItemStack(KilnRecipe.class, recipe, "input1"), recipe.getCraftingResult());
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
        public PositionedStack getOtherStack()
        {
            return new PositionedStack(new ItemStack(Blocks.fire), 43, 9);
        }
    }
}
