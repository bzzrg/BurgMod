package com.bzzrg.burgmod.debug.fieldwatching;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

@IFMLLoadingPlugin.Name("BurgModFieldWatcher")
@IFMLLoadingPlugin.MCVersion("1.8.9")
public class CoremodLoader implements IFMLLoadingPlugin {

    @Override
    public String[] getASMTransformerClass() {
        return new String[] {
                "com.bzzrg.burgmod.debug.fieldwatching.FieldWriteTransformer"
        };
    }

    @Override public String getModContainerClass() { return null; }
    @Override public String getSetupClass() { return null; }
    @Override public void injectData(Map<String, Object> data) {}
    @Override public String getAccessTransformerClass() { return null; }
}