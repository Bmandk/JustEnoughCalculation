package me.towdium.jecalculation.gui.widgets;

import static java.lang.String.join;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.polyfill.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.utils.Utilities;
import me.towdium.jecalculation.utils.Utilities.I18n;
import me.towdium.jecalculation.utils.wrappers.Pair;

/**
 * Author: towdium
 * Date: 17-9-16.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SideOnly(Side.CLIENT)
public abstract class WTooltip implements IWidget {

    public String name;
    protected Utilities.Timer timer = new Utilities.Timer();

    public WTooltip(@Nullable String name) {
        this.name = name;
    }

    @Override
    public boolean onDraw(JecaGui gui, int xMouse, int yMouse) {
        if (name != null) timer.setState(mouseIn(xMouse, yMouse));
        return false;
    }

    @Override
    public boolean onTooltip(JecaGui gui, int xMouse, int yMouse, List<String> tooltip) {
        if (timer.getTime() > 500) {
            List<Pair<String, Boolean>> suffix = getSuffix().stream()
                .map(s -> I18n.search(s.isEmpty() ? join(".", "gui", name) : join(".", "gui", name, s)))
                .collect(Collectors.toList());
            String str = suffix.stream()
                .filter(p -> p.two)
                .findFirst()
                .map(p -> p.one)
                .orElse(JecaGui.ALWAYS_TOOLTIP ? suffix.get(0).one : null);
            if (str != null) Collections.addAll(tooltip, str.split("\n"));
        }
        return false;
    }

    protected List<String> getSuffix() {
        return Collections.singletonList("");
    }

    public abstract boolean mouseIn(int xMouse, int yMouse);
}
