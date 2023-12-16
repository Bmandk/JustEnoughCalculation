package me.towdium.jecalculation.gui.guis.pickers;

import static me.towdium.jecalculation.gui.Resource.ICN_TEXT;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.data.label.labels.LFluidStack;
import me.towdium.jecalculation.data.label.labels.LOreDict;
import me.towdium.jecalculation.gui.guis.IGui;
import me.towdium.jecalculation.gui.widgets.WIcon;
import me.towdium.jecalculation.gui.widgets.WLabelScroll;
import me.towdium.jecalculation.gui.widgets.WSearch;
import me.towdium.jecalculation.polyfill.MethodsReturnNonnullByDefault;

/**
 * Author: towdium
 * Date: 17-9-28.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SideOnly(Side.CLIENT)
public class PickerSimple extends IPicker.Impl implements IGui {

    /**
     * @param labels label to be displayed for selection
     */
    public PickerSimple(List<ILabel> labels) {
        WLabelScroll ls = new WLabelScroll(7, 33, 8, 7, false).setLabels(labels)
            .setLsnrLeftClick(
                (i, v) -> notifyLsnr(
                    i.get(v)
                        .getLabel()));
        add(new WIcon(7, 7, 20, 20, ICN_TEXT, "common.search"));
        add(new WSearch(26, 7, 90, ls));
        add(ls);
    }

    public static class FluidStack extends PickerSimple {

        public FluidStack() {
            super(
                FluidRegistry.getRegisteredFluids()
                    .values()
                    .stream()
                    // filter the fluid with no icon
                    .filter(fluid -> fluid.getStillIcon() != null || fluid.getFlowingIcon() != null)
                    .map(fluid -> new LFluidStack(1000, fluid))
                    .collect(Collectors.toList()));
        }
    }

    public static class OreDict extends PickerSimple {

        public OreDict() {
            super(generate());
        }

        static List<ILabel> generate() {
            return Arrays.stream(OreDictionary.getOreNames())
                .filter(
                    i -> !OreDictionary.getOres(i)
                        .isEmpty())
                .map(LOreDict::new)
                .collect(Collectors.toList());
        }
    }
}
