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
import com.bioxx.tfc.Items.ItemFlatGeneric;
import com.bioxx.tfc.Items.ItemLooseRock;
import com.bioxx.tfc.Reference;
import com.bioxx.tfc.TFCItems;
import com.bioxx.tfc.api.Crafting.CraftingManagerTFC;
import com.bioxx.tfc.api.Crafting.ShapedRecipesTFC;
import net.dries007.tfcnei.util.Helper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static cpw.mods.fml.relauncher.ReflectionHelper.getPrivateValue;

/**
 * @author Dries007
 */
public class KnappingRecipeHandler extends TemplateRecipeHandler
{
    private static List<IRecipe> recipeList;

    @Override
    public String getGuiTexture()
    {
        return Reference.ModID + ":" + Reference.AssetPathGui + "gui_knapping.png";
    }

    @Override
    public String getRecipeName()
    {
        return "Knapping";
    }

    @Override
    public String getOverlayIdentifier()
    {
        return "knapping";
    }

    @SuppressWarnings("unchecked")
    @Override
    public TemplateRecipeHandler newInstance()
    {
        if (recipeList == null) recipeList = CraftingManagerTFC.getInstance().getRecipeList();
        return super.newInstance();
    }

    @Override
    public int recipiesPerPage()
    {
        return 1;
    }

    @Override
    public void loadTransferRects()
    {
        transferRects.add(new RecipeTransferRect(new Rectangle(0, 0, 5 * 16, 5 * 16), "knapping"));
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results)
    {
        if (outputId.equals("knapping") && getClass() == KnappingRecipeHandler.class)
        {
            for (IRecipe recipe : recipeList)
                if (recipe.getRecipeSize() > 9 && recipe instanceof ShapedRecipesTFC) // Filter out junk for optimisation. All knapping recipes are > 9 and are shaped
                {
                    // START COPY CODE PART
                    ItemStack[] inputs = getPrivateValue(ShapedRecipesTFC.class, (ShapedRecipesTFC) recipe, "recipeItems"); // Get inputs
                    for (ItemStack inStack : inputs)
                    {
                        if (inStack == null) continue; // Loop over until we find a not null entry
                        if (!(inStack.getItem() instanceof ItemFlatGeneric)) break; // If its not a flat type item, break out now
                        if (inStack.getItem() == TFCItems.FlatClay) // if its clay, we need some special code
                        {
                            if (inStack.getItemDamage() == 1) // Normal clay (damage value 1)
                            {
                                ItemStack actualInput = new ItemStack(TFCItems.ClayBall, 5, 0);
                                ItemStack flatType2 = new ItemStack(inStack.getItem(), 1, 0); // Dark texture has data value 0
                                arecipes.add(new CachedKnappingRecipe(Helper.getPrivateValue(ShapedRecipesTFC.class, int.class, (ShapedRecipesTFC) recipe, "recipeWidth"), Helper.getPrivateValue(ShapedRecipesTFC.class, int.class, (ShapedRecipesTFC) recipe, "recipeHeight"), flatType2, inputs, recipe.getRecipeOutput(), actualInput));
                            }
                            else if (inStack.getItemDamage() == 3) // Fire clay (damage value 3)
                            {
                                ItemStack actualInput = new ItemStack(TFCItems.ClayBall, 5, 1);
                                ItemStack flatType2 = new ItemStack(inStack.getItem(), 1, 2); // Dark texture has data value 3
                                arecipes.add(new CachedKnappingRecipe(Helper.getPrivateValue(ShapedRecipesTFC.class, int.class, (ShapedRecipesTFC) recipe, "recipeWidth"), Helper.getPrivateValue(ShapedRecipesTFC.class, int.class, (ShapedRecipesTFC) recipe, "recipeHeight"), flatType2, inputs, recipe.getRecipeOutput(), actualInput));
                            }
                        }
                        else // If not clay (aka Leather or stone) add the recipe without a 'dark' texture in place.
                        {
                            ItemStack actualInput = null;
                            if (inStack.getItem() == TFCItems.FlatLeather) actualInput = new ItemStack(TFCItems.Leather);
                            else if (inStack.getItem() == TFCItems.FlatRock) actualInput = new ItemStack(TFCItems.LooseRock);
                            else if (inStack.getItem() == TFCItems.FlatClay) actualInput = new ItemStack(TFCItems.ClayBall, 5);
                            arecipes.add(new CachedKnappingRecipe(Helper.getPrivateValue(ShapedRecipesTFC.class, int.class, (ShapedRecipesTFC) recipe, "recipeWidth"), Helper.getPrivateValue(ShapedRecipesTFC.class, int.class, (ShapedRecipesTFC) recipe, "recipeHeight"), null, inputs, recipe.getRecipeOutput(), actualInput));
                        }
                        break;
                    }
                    // END COPY CODE PART
                }
        }
        else
            super.loadCraftingRecipes(outputId, results);
    }

    @Override
    public void loadCraftingRecipes(ItemStack result)
    {
        for (IRecipe recipe : recipeList)
            if (Helper.areItemStacksEqual(result, recipe.getRecipeOutput()) && recipe.getRecipeSize() > 9 && recipe instanceof ShapedRecipesTFC)
            {
                // START COPY CODE PART. FOR COMMENTS SEE loadCraftingRecipes
                ItemStack[] inputs = getPrivateValue(ShapedRecipesTFC.class, (ShapedRecipesTFC) recipe, "recipeItems");
                for (ItemStack inStack : inputs)
                {
                    if (inStack == null) continue;
                    if (!(inStack.getItem() instanceof ItemFlatGeneric)) break;
                    if (inStack.getItem() == TFCItems.FlatClay)
                    {
                        if (inStack.getItemDamage() == 1)
                        {
                            ItemStack actualInput = new ItemStack(TFCItems.ClayBall, 5, 0);
                            ItemStack flatType2 = new ItemStack(inStack.getItem(), 1, 0);
                            arecipes.add(new CachedKnappingRecipe(Helper.getPrivateValue(ShapedRecipesTFC.class, int.class, (ShapedRecipesTFC) recipe, "recipeWidth"), Helper.getPrivateValue(ShapedRecipesTFC.class, int.class, (ShapedRecipesTFC) recipe, "recipeHeight"), flatType2, inputs, recipe.getRecipeOutput(), actualInput));
                        }
                        else if (inStack.getItemDamage() == 3)
                        {
                            ItemStack actualInput = new ItemStack(TFCItems.ClayBall, 5, 1);
                            ItemStack flatType2 = new ItemStack(inStack.getItem(), 1, 2);
                            arecipes.add(new CachedKnappingRecipe(Helper.getPrivateValue(ShapedRecipesTFC.class, int.class, (ShapedRecipesTFC) recipe, "recipeWidth"), Helper.getPrivateValue(ShapedRecipesTFC.class, int.class, (ShapedRecipesTFC) recipe, "recipeHeight"), flatType2, inputs, recipe.getRecipeOutput(), actualInput));
                        }
                    }
                    else if (inStack.getItemDamage() == Short.MAX_VALUE)
                    {
                        ItemStack actualInput = null;
                        if (inStack.getItem() == TFCItems.FlatLeather) actualInput = new ItemStack(TFCItems.Leather);
                        else if (inStack.getItem() == TFCItems.FlatRock) actualInput = new ItemStack(TFCItems.LooseRock);
                        else if (inStack.getItem() == TFCItems.FlatClay) actualInput = new ItemStack(TFCItems.ClayBall, 5);
                        arecipes.add(new CachedKnappingRecipe(Helper.getPrivateValue(ShapedRecipesTFC.class, int.class, (ShapedRecipesTFC) recipe, "recipeWidth"), Helper.getPrivateValue(ShapedRecipesTFC.class, int.class, (ShapedRecipesTFC) recipe, "recipeHeight"), null, inputs, recipe.getRecipeOutput(), actualInput));
                    }
                    break;
                }
                // END COPY CODE PART
            }

    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient)
    {
        if (!(ingredient.getItem() instanceof ItemLooseRock)) return;
        Item flatType = getPrivateValue(ItemLooseRock.class, (ItemLooseRock) ingredient.getItem(), "specialCraftingType");
        for (IRecipe recipe : recipeList)
        {
            if (recipe.getRecipeSize() > 9 && recipe instanceof ShapedRecipesTFC)
            {
                ItemStack[] inputs = getPrivateValue(ShapedRecipesTFC.class, (ShapedRecipesTFC) recipe, "recipeItems");
                for (ItemStack inStack : inputs)
                {
                    if (inStack == null || flatType != inStack.getItem()) continue;
                    if (flatType == TFCItems.FlatClay)
                    {
                        if (ingredient.getItemDamage() == 0 && inStack.getItemDamage() == 1) // Compare to see if the ingredient is normal clay
                        {
                            ItemStack actualInput = new ItemStack(TFCItems.ClayBall, 5, 0);
                            ItemStack flatType2 = new ItemStack(flatType, 1, 0);
                            arecipes.add(new CachedKnappingRecipe(Helper.getPrivateValue(ShapedRecipesTFC.class, int.class, (ShapedRecipesTFC) recipe, "recipeWidth"), Helper.getPrivateValue(ShapedRecipesTFC.class, int.class, (ShapedRecipesTFC) recipe, "recipeHeight"), flatType2, inputs, recipe.getRecipeOutput(), actualInput));
                        }
                        else if (ingredient.getItemDamage() == 1 && inStack.getItemDamage() == 3) // Compare to see if the ingredient is fire clay
                        {
                            ItemStack actualInput = new ItemStack(TFCItems.ClayBall, 5, 0);
                            ItemStack flatType2 = new ItemStack(flatType, 1, 2);
                            arecipes.add(new CachedKnappingRecipe(Helper.getPrivateValue(ShapedRecipesTFC.class, int.class, (ShapedRecipesTFC) recipe, "recipeWidth"), Helper.getPrivateValue(ShapedRecipesTFC.class, int.class, (ShapedRecipesTFC) recipe, "recipeHeight"), flatType2, inputs, recipe.getRecipeOutput(), actualInput));
                        }
                    }
                    else if (inStack.getItemDamage() == Short.MAX_VALUE || ingredient.getItemDamage() == inStack.getItemDamage())  // In this case match damage value of stone too.
                    {
                        ItemStack actualInput = null;
                        if (inStack.getItem() == TFCItems.FlatLeather) actualInput = new ItemStack(TFCItems.Leather);
                        else if (inStack.getItem() == TFCItems.FlatRock) actualInput = new ItemStack(TFCItems.LooseRock);
                        else if (inStack.getItem() == TFCItems.FlatClay) actualInput = new ItemStack(TFCItems.ClayBall, 5);
                        arecipes.add(new CachedKnappingRecipe(Helper.getPrivateValue(ShapedRecipesTFC.class, int.class, (ShapedRecipesTFC) recipe, "recipeWidth"), Helper.getPrivateValue(ShapedRecipesTFC.class, int.class, (ShapedRecipesTFC) recipe, "recipeHeight"), null, inputs, recipe.getRecipeOutput(), actualInput));
                    }
                    break;
                }
            }
        }
    }

    public class CachedKnappingRecipe extends CachedRecipe
    {
        List<PositionedStack> inputs;
        PositionedStack       result;
        PositionedStack       actualInput;

        public CachedKnappingRecipe(final int W, final int H, ItemStack off, ItemStack[] inputs, ItemStack recipeOutput, ItemStack actualInput)
        {
            this.inputs = new ArrayList<>();
            for (int h = 0; h < H; h++)
                for (int w = 0; w < W; w++)
                {
                    if (inputs[h * W + w] != null) this.inputs.add(new PositionedStack(inputs[h * W + w], 16 * w, 16 * h));
                    else if (off != null) this.inputs.add(new PositionedStack(off, 16 * w, 16 * h));
                }
            this.result = new PositionedStack(recipeOutput, 123, 33);
            this.actualInput = new PositionedStack(actualInput, 123, 10);
        }

        @Override
        public List<PositionedStack> getIngredients()
        {
            return inputs;
        }

        @Override
        public PositionedStack getResult()
        {
            return result;
        }

        @Override
        public PositionedStack getOtherStack()
        {
            return actualInput;
        }
    }
}
