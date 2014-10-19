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
import com.bioxx.tfc.api.Crafting.LoomManager;
import com.bioxx.tfc.api.Crafting.LoomRecipe;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.dries007.tfcnei.util.Constants;
import net.dries007.tfcnei.util.Helper;
import net.minecraft.item.ItemStack;

import java.awt.*;
import java.util.List;

/**
 * @author Dries007
 */
public class LoomRecipeHandler extends TemplateRecipeHandler
{
    private static List<LoomRecipe> recipeList;

    @Override
    public String getGuiTexture()
    {
        return Constants.LOOM_TEXTURE.toString();
    }

    @Override
    public String getRecipeName()
    {
        return "Loom";
    }

    @Override
    public String getOverlayIdentifier()
    {
        return "loom";
    }

    @Override
    public void loadTransferRects()
    {
        transferRects.add(new RecipeTransferRect(new Rectangle(88, 44 - 18, 25, 18), "loom"));
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results)
    {
        if (outputId.equals("loom") && getClass() == LoomRecipeHandler.class)
        {
            for (LoomRecipe recipe : recipeList) arecipes.add(new CachedLoomRecipe(recipe));
        }
        else
            super.loadCraftingRecipes(outputId, results);
    }

    @SuppressWarnings("unchecked")
    @Override
    public TemplateRecipeHandler newInstance()
    {
        if (recipeList == null) recipeList = ReflectionHelper.getPrivateValue(LoomManager.class, LoomManager.getInstance(), "recipes");
        return super.newInstance();
    }

    @Override
    public void loadCraftingRecipes(ItemStack result)
    {
        for (LoomRecipe recipe : recipeList)
        {
            ItemStack out = Helper.getPrivateItemStack(LoomRecipe.class, recipe, "outItemStack");
            if (Helper.areItemStacksEqual(result, out))
                arecipes.add(new CachedLoomRecipe(recipe.getInItem(), out));
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient)
    {
        for (LoomRecipe recipe : recipeList)
            if (Helper.areItemStacksEqual(ingredient, recipe.getInItem()))
                arecipes.add(new CachedLoomRecipe(recipe));
    }

    public class CachedLoomRecipe extends CachedRecipe
    {
        PositionedStack ingred;
        PositionedStack result;

        public CachedLoomRecipe(ItemStack ingred, ItemStack result)
        {
            this.ingred = new PositionedStack(ingred, 59, 24);
            this.result = new PositionedStack(result, 119, 24);
        }

        public CachedLoomRecipe(LoomRecipe recipe)
        {
            this(recipe.getInItem(), Helper.getPrivateItemStack(LoomRecipe.class, recipe, "outItemStack"));
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
    }
}
