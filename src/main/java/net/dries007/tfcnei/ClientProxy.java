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

package net.dries007.tfcnei;

import codechicken.nei.api.API;
import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.GuiUsageRecipe;
import net.dries007.tfcnei.gui.NEIGuiHandler;
import net.dries007.tfcnei.recipeHandlers.*;
import net.minecraftforge.common.config.Configuration;

import static net.minecraftforge.common.config.Configuration.CATEGORY_GENERAL;

/**
 * @author Dries007
 */
public class ClientProxy extends CommonProxy
{
    boolean anvil, quern, kiln, loom, knapping, barrel, alloy, heat;

    private final AnvilRecipeHandler anvilRecipeHandler = new AnvilRecipeHandler();
    private final QuernRecipeHandler quernRecipeHandler = new QuernRecipeHandler();
    private final KilnRecipeHandler kilnRecipeHandler = new KilnRecipeHandler();
    private final LoomRecipeHandler loomRecipeHandler = new LoomRecipeHandler();
    private final KnappingRecipeHandler knappingRecipeHandler = new KnappingRecipeHandler();
    private final BarrelRecipeHandler barrelRecipeHandler = new BarrelRecipeHandler();
    private final AlloyRecipeHandler alloyRecipeHandler = new AlloyRecipeHandler();
    private final HeatRecipeHandler heatRecipeHandler = new HeatRecipeHandler();

    @Override
    public void config(Configuration cfg)
    {
        super.config(cfg);

        TerraFirmaCraftNEIplugin.log.info("Updating TerraFirmaCraftNEIplugin modules...");

        anvil = cfg.getBoolean("anvil", CATEGORY_GENERAL, true, "");
        quern = cfg.getBoolean("quern", CATEGORY_GENERAL, true, "");
        kiln = cfg.getBoolean("kiln", CATEGORY_GENERAL, true, "");
        loom = cfg.getBoolean("loom", CATEGORY_GENERAL, true, "");
        knapping = cfg.getBoolean("knapping", CATEGORY_GENERAL, true, "");
        barrel = cfg.getBoolean("barrel", CATEGORY_GENERAL, true, "");
        alloy = cfg.getBoolean("alloy", CATEGORY_GENERAL, true, "");
        heat = cfg.getBoolean("heat", CATEGORY_GENERAL, true, "");

        API.registerNEIGuiHandler(new NEIGuiHandler());

        if (anvil)
        {
            API.registerRecipeHandler(anvilRecipeHandler);
            API.registerUsageHandler(anvilRecipeHandler);
        }
        else
        {
            GuiCraftingRecipe.craftinghandlers.remove(anvilRecipeHandler);
            GuiUsageRecipe.usagehandlers.remove(anvilRecipeHandler);
        }
        if (quern)
        {
            API.registerRecipeHandler(quernRecipeHandler);
            API.registerUsageHandler(quernRecipeHandler);
        }
        else
        {
            GuiCraftingRecipe.craftinghandlers.remove(quernRecipeHandler);
            GuiUsageRecipe.usagehandlers.remove(quernRecipeHandler);
        }
        if (kiln)
        {
            API.registerRecipeHandler(kilnRecipeHandler);
            API.registerUsageHandler(kilnRecipeHandler);
        }
        else
        {
            GuiCraftingRecipe.craftinghandlers.remove(kilnRecipeHandler);
            GuiUsageRecipe.usagehandlers.remove(kilnRecipeHandler);
        }
        if (loom)
        {
            API.registerRecipeHandler(loomRecipeHandler);
            API.registerUsageHandler(loomRecipeHandler);
        }
        else
        {
            GuiCraftingRecipe.craftinghandlers.remove(loomRecipeHandler);
            GuiUsageRecipe.usagehandlers.remove(loomRecipeHandler);
        }
        if (knapping)
        {
            API.registerRecipeHandler(knappingRecipeHandler);
            API.registerUsageHandler(knappingRecipeHandler);
        }
        else
        {
            GuiCraftingRecipe.craftinghandlers.remove(knappingRecipeHandler);
            GuiUsageRecipe.usagehandlers.remove(knappingRecipeHandler);
        }
        if (barrel)
        {
            API.registerRecipeHandler(barrelRecipeHandler);
            API.registerUsageHandler(barrelRecipeHandler);
        }
        else
        {
            GuiCraftingRecipe.craftinghandlers.remove(barrelRecipeHandler);
            GuiUsageRecipe.usagehandlers.remove(barrelRecipeHandler);
        }
        if (alloy)
        {
            API.registerRecipeHandler(alloyRecipeHandler);
            API.registerUsageHandler(alloyRecipeHandler);
        }
        else
        {
            GuiCraftingRecipe.craftinghandlers.remove(alloyRecipeHandler);
            GuiUsageRecipe.usagehandlers.remove(alloyRecipeHandler);
        }
        if (heat)
        {
            API.registerRecipeHandler(heatRecipeHandler);
            API.registerUsageHandler(heatRecipeHandler);
        }
        else
        {
            GuiCraftingRecipe.craftinghandlers.remove(heatRecipeHandler);
            GuiUsageRecipe.usagehandlers.remove(heatRecipeHandler);
        }
    }
}
