package me.towdium.jecalculation.gui.widgets;

import static me.towdium.jecalculation.gui.Resource.WGT_PAGER_F;
import static me.towdium.jecalculation.gui.Resource.WGT_PANEL_N;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.data.label.ILabel.RegistryEditor.Record;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.polyfill.MethodsReturnNonnullByDefault;

/**
 * Author: towdium
 * Date: 17-9-16.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SideOnly(Side.CLIENT)
public class WPage extends WTooltip {

    protected int index;
    protected Record record;
    protected boolean focused;
    protected ListenerAction<? super WPage> listener;

    public WPage(int index, Record record, boolean focused) {
        super(record.localizeKey);
        this.index = index;
        this.record = record;
        this.focused = focused;
    }

    @Override
    public boolean onDraw(JecaGui gui, int xMouse, int yMouse) {
        Resource resource = focused ? WGT_PAGER_F : WGT_PANEL_N;
        gui.drawResourceContinuous(resource, index * 24 + 3, -21, 24, 25, 4, 4, 4, 4);
        record.representation.drawLabel(gui, index * 24 + 7, -17, false);
        super.onDraw(gui, xMouse, yMouse);
        return false;
    }

    @Override
    public boolean onTooltip(JecaGui gui, int xMouse, int yMouse, List<String> tooltip) {
        return super.onTooltip(gui, xMouse, yMouse, tooltip) || mouseIn(xMouse, yMouse);
    }

    @Override
    public boolean onMouseClicked(JecaGui gui, int xMouse, int yMouse, int button) {
        if (mouseIn(xMouse, yMouse) && listener != null && !focused) {
            listener.invoke(this);
            return true;
        } else return false;
    }

    @Override
    public boolean mouseIn(int xMouse, int yMouse) {
        return JecaGui.mouseIn(index * 24, -21, 24, 21, xMouse, yMouse);
    }

    public WPage setListener(ListenerAction<? super WPage> r) {
        listener = r;
        return this;
    }
}
