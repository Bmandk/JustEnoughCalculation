package me.towdium.jecalculation.data.structure;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import me.towdium.jecalculation.JecaConfig;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.polyfill.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.polyfill.NBTHelper;
import me.towdium.jecalculation.utils.Utilities;
import me.towdium.jecalculation.utils.Utilities.ReversedIterator;
import me.towdium.jecalculation.utils.wrappers.Pair;

/**
 * Author: towdium
 * Date: 18-8-28.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class Recipes {

    HashMap<String, List<Recipe>> records = new HashMap<>();

    public Recipes() {
        File file = JecaConfig.defaultFile;
        NBTTagCompound nbt = Utilities.Json.read(file);
        if (nbt == null) JustEnoughCalculation.logger.info("Failed to load default records at " + file + ".");
        else {
            JustEnoughCalculation.logger.info("Loading default records at " + file + ".");
            deserialize(nbt);
        }
    }

    public Recipes(NBTTagCompound nbt) {
        deserialize(nbt);
    }

    protected void deserialize(NBTTagCompound nbt) {
        // noinspection unchecked
        Set<String> keySet = (Set<String>) nbt.func_150296_c();
        keySet.stream()
            .sorted()
            .forEach(i -> {
                NBTTagList group = nbt.getTagList(i, 10);
                StreamSupport.stream(NBTHelper.spliterator(group), false)
                    .filter(r -> r instanceof NBTTagCompound)
                    .forEach(r -> {
                        try {
                            add(i, new Recipe((NBTTagCompound) r));
                        } catch (IllegalArgumentException e) {
                            JustEnoughCalculation.logger.warn("Invalid recipe record :" + r);
                        }
                    });
            });
    }

    public void add(String group, Recipe recipe) {
        records.computeIfAbsent(group, k -> new ArrayList<>())
            .add(recipe);
    }

    public void renameGroup(String old, String neu) {
        List<Recipe> rs = records.get(old);
        records.remove(old);
        records.put(neu, rs);
    }

    public void modify(String neu, @Nullable String old, int index, @Nullable Recipe recipe) {
        if (index == -1) {
            if (recipe != null) add(neu, recipe);
            else if (old != null) renameGroup(old, neu);
            else remove(neu);
        } else {
            if (recipe == null) remove(neu, index);
            else if (old == null || old.equals(neu)) set(neu, index, recipe);
            else set(neu, old, index, recipe);
        }
    }

    public void set(String group, int index, Recipe recipe) {
        records.get(group)
            .set(index, recipe);
    }

    public void set(String neu, String old, int index, Recipe recipe) {
        remove(old, index);
        add(neu, recipe);
    }

    public int size() {
        return records.size();
    }

    public Stream<Pair<String, List<Recipe>>> stream() {
        return records.entrySet()
            .stream()
            .map(i -> new Pair<>(i.getKey(), i.getValue()));
    }

    public void remove(String group, int index) {
        List<Recipe> l = records.get(group);
        l.remove(index);
        if (l.isEmpty()) records.remove(group);
    }

    public void remove(String group) {
        records.remove(group);
    }

    public Recipe getRecipe(String group, int index) {
        return getGroup(group).get(index);
    }

    public List<Recipe> getRecipes() {
        return records.values()
            .stream()
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    public List<Recipe> getRecipes(String group) {
        return records.get(group);
    }

    public void forEach(BiConsumer<String, List<Recipe>> consumer) {
        records.forEach(consumer);
    }

    /**
     * Do not modify return value!
     *
     * @param group Name of group to get
     * @return List of Recipes in group
     */
    public List<Recipe> getGroup(String group) {
        return records.get(group);
    }

    public NBTTagCompound serialize(Collection<String> groups) {
        NBTTagCompound ret = new NBTTagCompound();
        groups.forEach(i -> {
            NBTTagList l = new NBTTagList();
            getGroup(i).forEach(r -> l.appendTag(r.serialize()));
            ret.setTag(i, l);
        });
        return ret;
    }

    public List<String> getGroups() {
        return records.keySet()
            .stream()
            .sorted()
            .collect(Collectors.toList());
    }

    public NBTTagCompound serialize() {
        return serialize(getGroups());
    }

    public RecipeIterator recipeIterator() {
        return new RecipeIterator();
    }

    public RecipeIterator recipeIterator(String group) {
        return new RecipeIterator(group);
    }

    public class RecipeIterator implements Iterator<Recipe> {

        String group;
        int index;
        Iterator<String> i;
        Iterator<Recipe> j;

        public RecipeIterator() {
            i = getGroups().iterator();
        }

        public RecipeIterator(String group) {
            i = Collections.singleton(group)
                .iterator();
        }

        @Override
        public boolean hasNext() {
            while (j == null || !j.hasNext()) {
                if (i.hasNext()) {
                    group = i.next();
                    List<Recipe> rs = records.get(group);
                    index = rs.size();
                    j = new ReversedIterator<>(rs);
                } else return false;
            }
            return true;
        }

        @Override
        public Recipe next() {
            // noinspection ResultOfMethodCallIgnored
            hasNext();
            index--;
            return j.next();
        }

        public String getGroup() {
            return group;
        }

        public int getIndex() {
            return index;
        }

        public Stream<Recipe> stream() {
            return StreamSupport.stream(Spliterators.spliteratorUnknownSize(this, Spliterator.ORDERED), false);
        }
    }
}
