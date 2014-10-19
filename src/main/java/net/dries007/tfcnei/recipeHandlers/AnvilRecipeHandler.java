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
import com.bioxx.tfc.Core.Recipes;
import com.bioxx.tfc.GUI.GuiAnvil;
import com.bioxx.tfc.Items.Tools.ItemHammer;
import com.bioxx.tfc.TFCItems;
import com.bioxx.tfc.api.Crafting.AnvilManager;
import com.bioxx.tfc.api.Crafting.AnvilRecipe;
import com.bioxx.tfc.api.Crafting.AnvilReq;
import net.dries007.tfcnei.util.Helper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static codechicken.lib.gui.GuiDraw.changeTexture;
import static codechicken.lib.gui.GuiDraw.drawTexturedModalRect;
import static net.dries007.tfcnei.recipeHandlers.AnvilRecipeHandler.TYPE.*;
import static net.dries007.tfcnei.util.Helper.getPrivateItemStack;
import static net.dries007.tfcnei.util.Helper.getPrivateValue;

/**
 * @author Dries007
 */
public class AnvilRecipeHandler extends TemplateRecipeHandler
{
    private static List<AnvilRecipe> recipeList, weldRecipeList;
    private static ItemStack[] hammers;

    @Override
    public String getGuiTexture()
    {
        return GuiAnvil.texture.toString();
    }

    @Override
    public String getRecipeName()
    {
        return "Anvil";
    }

    @Override
    public String getOverlayIdentifier()
    {
        return "tfcanvil";
    }

    @Override
    public void loadTransferRects()
    {
        transferRects.add(new RecipeTransferRect(new Rectangle(13 - 21, 53 + 5, 36, 20), "tfcanvil"));
        transferRects.add(new RecipeTransferRect(new Rectangle(72 - 21, 8 + 5, 64, 36), "tfcanvil"));
        transferRects.add(new RecipeTransferRect(new Rectangle(69 - 21, 64 + 5, 70, 34), "tfcanvil"));
        transferRects.add(new RecipeTransferRect(new Rectangle(148 - 21, 6 + 5, 54, 72), "tfcanvil"));
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results)
    {
        if (outputId.equals("tfcanvil") && getClass() == AnvilRecipeHandler.class)
        {
            for (AnvilRecipe recipe : recipeList) arecipes.add(new CachedAnvilRecipe(NORMAL, recipe));
            for (AnvilRecipe recipe : weldRecipeList) arecipes.add(new CachedAnvilRecipe(WELD, recipe));
        }
        else
            super.loadCraftingRecipes(outputId, results);
    }

    @Override
    public void loadCraftingRecipes(ItemStack result)
    {
        for (AnvilRecipe recipe : recipeList) if (Helper.areItemStacksEqual(result, recipe.getCraftingResult(result))) arecipes.add(new CachedAnvilRecipe(NORMAL, recipe));
        for (AnvilRecipe recipe : weldRecipeList) if (Helper.areItemStacksEqual(result, recipe.getCraftingResult(result))) arecipes.add(new CachedAnvilRecipe(WELD, recipe));
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient)
    {
        if (ingredient.getItem() instanceof ItemHammer)
        {
            for (AnvilRecipe recipe : recipeList) arecipes.add(new CachedAnvilRecipe(HAMMER_NORMAL, recipe));
            for (AnvilRecipe recipe : weldRecipeList) arecipes.add(new CachedAnvilRecipe(HAMMER_WELD, recipe));
        }
        else if (ingredient.getItem() == TFCItems.Powder && ingredient.getItemDamage() == 0)
        {
            for (AnvilRecipe recipe : weldRecipeList) arecipes.add(new CachedAnvilRecipe(WELD, recipe));
        }
        else
        {
            for (AnvilRecipe recipe : recipeList)
            {
                ItemStack i1 = getPrivateItemStack(AnvilRecipe.class, recipe, "input1");
                ItemStack i2 = getPrivateItemStack(AnvilRecipe.class, recipe, "input2");
                if (Helper.areItemStacksEqual(ingredient, i1) || Helper.areItemStacksEqual(ingredient, i2))
                    arecipes.add(new CachedAnvilRecipe(NORMAL, getPrivateValue(AnvilRecipe.class, int.class, recipe, "anvilreq"), recipe.getCraftingResult(), i1, i2));
            }

            for (AnvilRecipe recipe : weldRecipeList)
            {
                ItemStack i1 = getPrivateItemStack(AnvilRecipe.class, recipe, "input1");
                ItemStack i2 = getPrivateItemStack(AnvilRecipe.class, recipe, "input1");
                if (Helper.areItemStacksEqual(ingredient, i1) || Helper.areItemStacksEqual(ingredient, i2))
                    arecipes.add(new CachedAnvilRecipe(WELD, getPrivateValue(AnvilRecipe.class, int.class, recipe, "anvilreq"), recipe.getCraftingResult(), i1, i2));
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public TemplateRecipeHandler newInstance()
    {
        if (recipeList == null || weldRecipeList == null || hammers == null)
        {
            recipeList = AnvilManager.getInstance().getRecipeList();
            weldRecipeList = AnvilManager.getInstance().getWeldRecipeList();

            hammers = new ItemStack[Recipes.Hammers.length];
            for (int i = 0; i < hammers.length; i++)
                hammers[i] = new ItemStack(Recipes.Hammers[i]);
        }
        return super.newInstance();
    }

    @Override
    public int recipiesPerPage()
    {
        return 1;
    }

    @Override
    public void drawBackground(int recipe)
    {
        GL11.glColor4f(1, 1, 1, 1);
        changeTexture(getGuiTexture());
        drawTexturedModalRect(-21, 5, 0, 0, 208, 198);
    }

    @Override
    public void drawForeground(int recipe)
    {
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glDisable(GL11.GL_LIGHTING);
        changeTexture(getGuiTexture());
        drawExtras(recipe);
    }

    @Override
    public void drawExtras(int recipe)
    {
        super.drawExtras(recipe);
        CachedRecipe cr = arecipes.get(recipe);
        if (cr instanceof CachedAnvilRecipe) Helper.drawCenteredString(Minecraft.getMinecraft().fontRenderer, ((CachedAnvilRecipe) cr).anvilReq, 80, -3, 0x820093);
    }

    public static enum TYPE
    {
        NORMAL, WELD, HAMMER_NORMAL, HAMMER_WELD;

        public boolean isWeld()
        {
            return this == WELD || this == HAMMER_NORMAL;
        }
    }

    public class CachedAnvilRecipe extends CachedRecipe
    {
        PositionedStack i1, i2, out;
        TYPE type;
        public String anvilReq;

        public CachedAnvilRecipe(TYPE type, AnvilRecipe recipe)
        {
            this(type, getPrivateValue(AnvilRecipe.class, int.class, recipe, "anvilreq"), recipe.getCraftingResult(), getPrivateItemStack(AnvilRecipe.class, recipe, "input1"), getPrivateItemStack(AnvilRecipe.class, recipe, "input2"));
        }

        public CachedAnvilRecipe(TYPE type, int anvilreq, ItemStack out, ItemStack i1, ItemStack i2)
        {
            this.type = type;
            StringBuilder sb = new StringBuilder();
            for (AnvilReq a : AnvilReq.rules)
            {
                if (a.Tier != anvilreq) continue;
                sb.append(a.Name).append(" anvil or better");
                break;
            }
            this.anvilReq = sb.toString();
            if (i1 != null) this.i1 = new PositionedStack(i1, type.isWeld() ? -7 : 66, type.isWeld() ? 17 : 51);
            if (i2 != null) this.i2 = new PositionedStack(i2, type.isWeld() ? 11 : 84, type.isWeld() ? 17 : 51);
            this.out = new PositionedStack(out, type.isWeld() ? 2 : 103, type.isWeld() ? 39 : 51);
        }

        @Override
        public List<PositionedStack> getIngredients()
        {
            ArrayList<PositionedStack> stacks = new ArrayList<PositionedStack>();
            if (i1 != null)
                stacks.add(i1);
            if (i2 != null)
                stacks.add(i2);
            return stacks;
        }

        @Override
        public PositionedStack getResult()
        {
            return out;
        }

        @Override
        public List<PositionedStack> getOtherStacks()
        {
            List<PositionedStack> stacks = new ArrayList<>();

            stacks.add(new PositionedStack(hammers, -14, 100, false));
            if (type.isWeld()) stacks.add(new PositionedStack(new ItemStack(TFCItems.Powder), 164, 100));
            return stacks;
        }
    }
}
