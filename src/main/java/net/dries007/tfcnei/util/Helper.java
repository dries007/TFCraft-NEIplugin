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

package net.dries007.tfcnei.util;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

import static net.minecraftforge.fluids.FluidContainerRegistry.BUCKET_VOLUME;
import static net.minecraftforge.fluids.FluidContainerRegistry.getRegisteredFluidContainerData;

/**
 * Static stuff for all to use :p
 *
 * @author Dries007
 */
public class Helper
{
    private Helper()
    {
    }

    /**
     * For ease of use when using some field or method in for example an if statement
     */
    public static <T, E> T getPrivateValue(Class<? super E> classToAccess, Class<T> returnType, E instance, String... fieldNames)
    {
        return ReflectionHelper.getPrivateValue(classToAccess, instance, fieldNames);
    }

    /**
     * Because I needed it somewhere, and then I was too lazy to switch back to the ReflectionHelper itself.
     */
    public static <E> ItemStack getPrivateItemStack(Class<? super E> classToAccess, E instance, String... fieldNames)
    {
        return ReflectionHelper.getPrivateValue(classToAccess, instance, fieldNames);
    }

    public static <E> FluidStack getPrivateFluidStack(Class<? super E> classToAccess, E instance, String... fieldNames)
    {
        return ReflectionHelper.getPrivateValue(classToAccess, instance, fieldNames);
    }

    public static ItemStack[] getItemStacksForFluid(FluidStack fluidStack)
    {
        if (fluidStack == null) return null;

        List<ItemStack> itemStacks = new ArrayList<>();
        for (FluidContainerRegistry.FluidContainerData data : getRegisteredFluidContainerData())
        {
            if (data.fluid.isFluidEqual(fluidStack))
            {
                ItemStack itemStack = data.filledContainer.copy();
                itemStack.stackSize = fluidStack.amount / FluidContainerRegistry.getContainerCapacity(data.fluid, data.emptyContainer);
                itemStacks.add(itemStack);
            }
        }
        if (itemStacks.size() == 0)
        {
            ItemStack itemStack = new ItemStack(fluidStack.getFluid().getBlock(), fluidStack.amount / BUCKET_VOLUME);
            if (itemStack.getItem() == null)
            {
                itemStack = new ItemStack(Blocks.sponge, itemStack.stackSize).setStackDisplayName(fluidStack.getLocalizedName());
                itemStack.getTagCompound().setString("FLUID", fluidStack.getFluid().getName());
            }
            itemStacks.add(itemStack);
        }
        return itemStacks.toArray(new ItemStack[itemStacks.size()]);
    }

    public static void drawCenteredString(FontRenderer fontrenderer, String s, int i, int j, int k)
    {
        fontrenderer.drawString(s, i - fontrenderer.getStringWidth(s) / 2, j, k);
    }

    /**
     * true if both null
     * true if items are equal and [ meta are equal or if inputStack's meta is wildcard ]
     */
    public static boolean areItemStacksEqual(ItemStack inputStack, ItemStack recipeStack)
    {
        return inputStack == recipeStack || OreDictionary.itemMatches(recipeStack, inputStack, false);
    }
}
