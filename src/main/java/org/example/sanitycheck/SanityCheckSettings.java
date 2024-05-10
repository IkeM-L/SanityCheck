package org.example.sanitycheck;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;

@State(
        name = "SanityCheckSettings",
        storages = @Storage("SanityCheckPlugin.xml")
)
public class SanityCheckSettings implements PersistentStateComponent<SanityCheckSettings> {
    public String modelName = "defaultModel";
    public String apiUrl = "http://localhost:5000/ask";

    public static SanityCheckSettings getInstance() {
        return ServiceManager.getService(SanityCheckSettings.class);
    }

    @Override
    public SanityCheckSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull SanityCheckSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
